name := "Playground"

version := "0.1"

scalaVersion := "2.13.5"

fork := true

libraryDependencies ++= Seq(
  "com.beachape"             %% "enumeratum-play"            % "1.6.1",
  "org.typelevel"            %% "cats-effect"                % "2.3.1",
  "com.typesafe.akka"        %% "akka-stream"                % "2.6.14",
  "com.typesafe.akka"        %% "akka-actor-typed"           % "2.6.14",
  "com.typesafe.akka"        %% "akka-serialization-jackson" % "2.6.14",
  "org.scala-lang.modules"   %% "scala-parallel-collections" % "1.0.0",
  "com.github.mjakubowski84" %% "parquet4s-akka"             % "1.7.0",
  "org.apache.hadoop"         % "hadoop-client"              % "3.3.0"
)
