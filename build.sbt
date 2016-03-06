name := "scrawl"

scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("main.CrawlerSystem")

scalaSource in Compile := baseDirectory.value / "src"
resourceDirectory in Compile := baseDirectory.value / "resources"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.3.9",
  "org.scalatest" %% "scalatest" % "2.2.1"
)