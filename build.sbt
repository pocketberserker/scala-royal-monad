scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.6", "2.11.7")

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds"
)

fullResolvers ~= {_.filterNot(_.name == "jcenter")}

scalapropsSettings
scalapropsVersion := "0.1.16"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.5"
)
