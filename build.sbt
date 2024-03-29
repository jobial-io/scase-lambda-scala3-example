/*
 * Copyright (c) 2020 Jobial OÜ. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
name := "scase-lambda-scala3-example"

ThisBuild / organization := "io.jobial"
ThisBuild / scalaVersion := "3.2.0"
ThisBuild / version := "0.6.0"
ThisBuild / javacOptions ++= Seq("-source", "11", "-target", "11")
ThisBuild / Test / packageBin / publishArtifact := true
ThisBuild / Test / packageSrc / publishArtifact := true
ThisBuild / Test / packageDoc / publishArtifact := true
Proguard / proguardVersion := "7.2.2"

import com.lightbend.sbt.SbtProguard.autoImport.proguardOptions
import sbt.Keys.{description, libraryDependencies, publishConfiguration}
import sbt.addCompilerPlugin
import xerial.sbt.Sonatype._

lazy val commonSettings = Seq(
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  publishTo := publishTo.value.orElse(sonatypePublishToBundle.value),
  sonatypeProjectHosting := Some(GitHubHosting("jobial-io", "scase", "orbang@jobial.io")),
  organizationName := "Jobial OÜ",
  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  description := "Run functional Scala code as a portable serverless function or microservice"
)

lazy val ScaseVersion = "0.6.0"

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .enablePlugins(SbtProguard)
  .settings(
    cloudformationStackClass := "io.jobial.scase.example.greeting.lambda.GreetingServiceStack$",
    libraryDependencies ++= Seq(
      "io.jobial" % "condense_2.13" % ScaseVersion,
      "io.jobial" % "scase-core_2.13" % ScaseVersion % "compile->compile;test->test" exclude("commons-logging", "commons-logging-api"),
      "io.jobial" % "scase-aws_2.13" % ScaseVersion % "compile->compile;test->test" exclude("commons-logging", "commons-logging-api"),
      "io.jobial" % "scase-spray-json_2.13" % ScaseVersion exclude("commons-logging", "commons-logging-api"),
      "io.github.paoloboni" %% "spray-json-derived-codecs" % "2.3.5"
    ),
    Proguard / proguardOptions ++= Seq(
      "-dontobfuscate", "-dontoptimize", "-dontnote", "-ignorewarnings",
      "-keep class io.jobial.scase.example.greeting.lambda.** {*;}",
      "-keepclassmembers class io.jobial.scase.aws.lambda.LambdaRequestHandler {*;}",
      "-keep class com.amazonaws.services.lambda.** {*;}",
      "-keep class scala.Symbol {*;}"
    ),
    Proguard / proguardInputFilter := { file =>
      file.name match {
        case _ => Some("!META-INF/**,!about.html,!org/apache/commons/logging/**")
      }
    },
    Proguard / proguard / javaOptions := Seq("-Xmx2G"),
    Proguard / proguardVersion := "7.2.2"
  )

