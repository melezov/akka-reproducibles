organization := "hr.element.akka_reproducibles"

name := "message-size-issue-client"

crossScalaVersions := Seq("2.10.4", "2.11.1")

scalaVersion := crossScalaVersions.value.head

scalacOptions := Seq(
  "-deprecation"
, "-encoding", "UTF-8"
, "-feature"
, "-language:existentials"
, "-language:implicitConversions"
, "-language:postfixOps"
, "-language:reflectiveCalls"
, "-optimise"
, "-unchecked"
, "-Xcheckinit"
, "-Xlint"
, "-Xmax-classfile-name", "72"
, "-Xno-forwarders"
, "-Xverify"
, "-Yclosure-elim"
, "-Ydead-code"
, "-Yinline-warnings"
, "-Yinline"
, "-Yrepl-sync"
, "-Ywarn-adapted-args"
, "-Ywarn-dead-code"
, "-Ywarn-inaccessible"
, "-Ywarn-nullary-override"
, "-Ywarn-nullary-unit"
, "-Ywarn-numeric-widen"
)

unmanagedSourceDirectories in Compile := Nil

unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core"   % "0.11.1" % "test"
, "ch.qos.logback"          %  "logback-classic" % "1.1.2"  % "test"
, "org.scalatest"           %% "scalatest"       % "2.2.0"  % "test"
, "junit"                   %  "junit"           % "4.11"   % "test"
)

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource
