package util

import scala.scalajs.js

/**
 * Utility to generate an api file based on a starting introspection point (often, the global object or this)
 */
object JsApiGenerator {

  private object Utils {
    sealed trait Type
    case class Def(decl: String, name: String, tpe: Type)
    case class Raw(tpe: String) extends Type
    case class Obj(name: String, exampleInstance: js.Object) extends Type
    case class Template(name: String, fields: Seq[Def], templates: Seq[Template])
  }
  import Utils._

  def generate(obj: js.Object, maxLevel: Int = 10): String = {
    var nameIndex = -1
    def nextType = {
      nameIndex += 1
      "Type_" + nameIndex
    }

    def introspect(name: String, obj: js.Object, nestLevel: Int): Template = {
      val dynObj = obj.asInstanceOf[js.Dynamic]
      val defs = (for (p <- js.Object.properties(dynObj)) yield {
        val v = dynObj.selectDynamic(p).asInstanceOf[js.Dynamic]
        val propDescr: js.PropertyDescriptor | Unit = js.Object.getOwnPropertyDescriptor(obj, p)
        val t = js.typeOf(v) match {
          case "object" => Obj(nextType, v.asInstanceOf[js.Object])
          case "string" => Raw("String")
          case "number" => Raw("Int")
          case "real" => Raw("Double")
          case other => Raw(other)
        }
        
        val decl = if (propDescr.map(_.writable) getOrElse false) "var" else "def"
        Def(decl, p, t)
      }).toSeq

      //collect sub types
      val templates: Map[Template, Seq[Template]] = if (nestLevel < maxLevel) {
        //introspect references (note we will later dedup them). Pull sub templates out to our level (this means templates bubble up to the top)
        val subTypes = defs.collect { case Def(_, _, Obj(tpe, exampleInstance)) => 
            val t = introspect(tpe, exampleInstance, nestLevel + 1)
            val templates = t.templates
            t.copy(templates = Seq.empty) +: templates
        }.flatten
        //try to identify identic types
        subTypes.groupBy(t => t.fields -> t.templates) map {
          case (_, templates) => templates.head -> templates //keep only one
        }
      } else Map(Template("More", Seq.empty, Seq.empty) -> Seq.empty)

      //map the defs to the template dedup template
      val deduppedDefs = defs map {
        case d @ Def(decl, field, o: Obj) => templates.find(_._2.exists(_.name == o.name)).fold(d)(entry => Def(decl, field, Obj(entry._1.name, null)))
        case other => other
      }

      Template(name, deduppedDefs, templates.keys.toSeq)
    }
    val root = introspect("root", obj, 1)

    def render(t: Template, ident: Int = 0): String = {
      val prefix = "  " * ident
      var res = prefix + s"trait ${t.name} {"
      t.fields foreach {
        case Def(decl, name, Raw(tpe)) => res += "\n" + prefix + s"  $decl $name: $tpe = js.native"
        case Def(decl, name, Obj(tpe, _)) => res += "\n" + prefix + s"  $decl $name: $tpe = js.native"
      }
      for (t <- t.templates) res += "\n" + render(t, ident + 1)
      res += "\n" + prefix + "}"
      res
    }
    render(root)
  }
}
