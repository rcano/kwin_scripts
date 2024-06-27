name := "kwin-popups-behaviour"

scalaVersion := "3.4.2"

enablePlugins(ScalaJSPlugin)

scalaJSUseMainModuleInitializer := true
semanticdbEnabled := true

scalacOptions ++= Seq("-rewrite", "-source", "3.4-migration")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "2.8.0",
)

lazy val prepareKwinScript = taskKey[Unit]("prepares the script for kwin")

prepareKwinScript := {
  import java.nio.file._
  import scala.collection.JavaConverters._
  val srcFile = (Compile / fastOptJS).value.data

  val methodsToInhibit = Seq("freeze", "isSealed")
  val scriptLines = Files.readAllLines(srcFile.toPath, IO.utf8).asScala.map {
    case l if methodsToInhibit.exists(m => l.contains(s"""["$m"]""")) => "// " + l
    case other => other
  }
  val dstFile = srcFile.getParentFile / "kwin-script.js"
  Files.write(dstFile.toPath, scriptLines.asJava, IO.utf8)
}
