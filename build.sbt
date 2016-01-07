name := "kwin-popups-behaviour"

scalaVersion := "2.11.7"

enablePlugins(ScalaJSPlugin)

lazy val prepareKwinScript = taskKey[Unit]("prepares the script for kwin")

prepareKwinScript := {
  import java.nio.file._
  import scala.collection.JavaConverters._
  val srcFile = (fastOptJS in Compile).value.data

  val methodsToInhibit = Seq("freeze", "isSealed")
  val scriptLines = Files.readAllLines(srcFile.toPath, IO.utf8).asScala.map {
    case l if methodsToInhibit.exists(m => l.contains(s"""["$m"]""")) => "// " + l
    case other => other
  }
  scriptLines += (mainClass in Compile).value.map(c => s"$c().main()").getOrElse(throw new IllegalStateException("No main class detected"))
  val dstFile = srcFile.getParentFile / "kwin-script.js"
  Files.write(dstFile.toPath, scriptLines.asJava, IO.utf8)
}