name := "Playground"

version := "0.1"

scalaVersion := "2.13.6"

fork := false

libraryDependencies ++= Seq(
  "com.beachape"             %% "enumeratum-play"            % "1.6.1",
  "org.typelevel"            %% "cats-effect"                % "3.1.1",
  "com.typesafe.akka"        %% "akka-stream"                % "2.6.14",
  "com.typesafe.akka"        %% "akka-actor-typed"           % "2.6.14",
  "com.typesafe.akka"        %% "akka-serialization-jackson" % "2.6.14",
  "com.lightbend.akka"       %% "akka-stream-alpakka-csv"    % "3.0.1",
  "org.scala-lang.modules"   %% "scala-parallel-collections" % "1.0.3",
  "com.github.mjakubowski84" %% "parquet4s-akka"             % "1.7.0",
  "org.apache.hadoop"        % "hadoop-client"               % "3.3.0",
  "org.scalacheck"           %% "scalacheck"                 % "1.14.1",
  "com.storm-enroute"        %% "scalameter"                 % "0.21" % Test,
  "org.scalatest"            %% "scalatest"                  % "3.2.9" % Test
)

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

Test / parallelExecution := false

Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
