/*
 * Copyright (c) 2013 Miles Sabin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._
import Keys._
import util.Properties

object ShapelessMacrosBuild extends Build {
  val keplerHome =  Properties.envOrElse("KEPLER_HOME", "/home/miles/projects/scala/kepler/build/pack")

  lazy val shapeless = Project(
    id = "shapeless",
    base = file("."),
    aggregate = Seq(shapelessBase, shapelessCore),
    settings = commonSettings ++ Seq(
      moduleName := "shapeless-root"
    )
  )

  lazy val shapelessBase = Project(
    id = "shapeless-base", 
    base = file("base"),
    settings = commonSettings
  )

  lazy val shapelessCore = Project(
    id = "shapeless-core", 
    base = file("core"),
    settings = commonSettings ++ Seq(
      moduleName := "shapeless",
      
      libraryDependencies ++= Seq(
        "com.novocode" % "junit-interface" % "0.7" % "test"
      )
    )
  ) dependsOn(shapelessBase)

  def commonSettings = Defaults.defaultSettings ++
    Seq(
      organization        := "com.chuusai",
      version             := "1.0.0-SNAPSHOT",
      scalaVersion        := "2.10.0",
      scalaHome           := Some(file(keplerHome)),
      unmanagedBase       := file(keplerHome+"/lib"),

      scalacOptions       := Seq(
        "-feature",
        "-deprecation",
        "-unchecked"),

      resolvers           ++= Seq(
        Classpaths.typesafeSnapshots,
        "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
      )
    )
}
