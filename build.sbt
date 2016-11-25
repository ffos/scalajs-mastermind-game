enablePlugins(ScalaJSPlugin)

name := "MasterMind"

scalaVersion := "2.12.0"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.2"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
