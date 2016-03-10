name := "scrawl"
version := "1.0.0"

scalaVersion := "2.11.7"

mainClass := Some("main.Scrawl")

scalaSource in Compile := baseDirectory.value / "src"
resourceDirectory in Compile := baseDirectory.value / "resources"

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits")
target in Compile in doc := baseDirectory.value / "doc"

exportJars := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.4.2",
  "org.scalatest" %% "scalatest" % "2.2.1"
)