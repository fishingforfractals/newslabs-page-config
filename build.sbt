import AssemblyKeys._

assemblySettings

organization  := "bbc"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

name := "newslabs-page-config"

version := "0.1"

mainClass in assembly := Some("pageconfig.Boot")

jarName in assembly := s"${name.value}.jar"


libraryDependencies ++= {
  val akkaVersion = "2.3.6"
  val sprayVersion = "1.3.1"
  val specs2Version = "3.7.2"
  Seq(
    "io.spray"                %%  "spray-can"        % sprayVersion,
    "io.spray"                %%  "spray-routing"    % sprayVersion,
    "org.json4s"              %%  "json4s-jackson"   % "3.3.0",
    "com.typesafe.akka"       %%  "akka-actor"       % akkaVersion,
    "io.spray"                %%  "spray-testkit"    % sprayVersion              % "test",
    "org.specs2"              %%  "specs2-core"      % specs2Version             % "test",
    "org.specs2"              %%  "specs2-mock"      % specs2Version             % "test"
  )
}

resolvers ++= Seq(
  "BBC Artifactory" at "https://dev.bbc.co.uk/artifactory/repo/",
  "BBC Maven Releases" at "https://dev.bbc.co.uk/maven2/releases/",
  "BBC Maven Snapshots" at "https://dev.bbc.co.uk/maven2/snapshots/",
  "Spray repository" at "http://repo.spray.io",
  "clojars.org" at "http://clojars.org/repo",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)