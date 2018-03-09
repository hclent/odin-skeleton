name := "padsystem"
version := "0.0.1"
organization := "org.vinci.pad"

scalaVersion := "2.12.4"

libraryDependencies ++= {
  val procVer = "7.1.0"

  Seq(
    "org.clulab"    %% "processors-main"          % procVer,
    "org.clulab"    %% "processors-corenlp"       % procVer,
    "org.clulab"    %% "processors-odin"          % procVer,
    "org.clulab"    %% "processors-modelsmain"    % procVer,
    "org.clulab"    %% "processors-modelscorenlp" % procVer,
    "ai.lum"        %% "common"                   % "0.0.8",
    "org.scalatest" %% "scalatest"                % "3.0.4" % "test",
    "com.typesafe"  %  "config"                   % "1.3.1",
    "net.sf.saxon"  % "saxon-dom"                 % "8.7",

    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "com.typesafe" % "config" % "1.2.1",
    "org.scalaj" % "scalaj-http_2.11" % "2.3.0"
  )
}

lazy val core = project in file(".")