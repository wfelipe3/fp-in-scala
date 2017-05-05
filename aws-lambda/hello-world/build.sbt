
import sbt.Keys._
import sbt._
import sbtassembly.Plugin._

assemblySettings

name := "hello-world"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.3.0"
libraryDependencies += "org.scalaz" % "scalaz-core_2.12" % "7.3.0-M11"
libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"


javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")


