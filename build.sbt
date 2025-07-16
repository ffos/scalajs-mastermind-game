enablePlugins(ScalaJSPlugin)

name := "MasterMind"

scalaVersion := "2.13.14"
scalaJSUseMainModuleInitializer := true


libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.9.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"

jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
