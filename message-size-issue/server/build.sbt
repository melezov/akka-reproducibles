organization := "hr.element.akka_reproducibles"

name := "message-size-issue-server"

scalaVersion := "2.10.4"

scalacOptions := Seq(
  "-unchecked"
, "-deprecation"
, "-optimise"
, "-encoding", "UTF-8"
, "-Xcheckinit"
, "-Yclosure-elim"
, "-Ydead-code"
, "-Yinline"
, "-Xmax-classfile-name", "72"
, "-Yrepl-sync"
, "-Xlint"
, "-Xverify"
, "-Ywarn-all"
, "-feature"
, "-language:postfixOps"
, "-language:implicitConversions"
, "-language:existentials"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"  % "2.3.2"
, "com.typesafe.akka" %% "akka-remote" % "2.3.2"
, "com.typesafe.akka" %% "akka-slf4j"  % "2.3.2"
, "ch.qos.logback" % "logback-classic" % "1.1.2"
)

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)

unmanagedSourceDirectories in Test := Nil

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
