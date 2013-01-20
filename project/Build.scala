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
    settings = commonSettings ++ Seq(
      libraryDependencies <+= (scalaVersion)("org.scala-lang.macro-paradise" % "scala-reflect" % _)
    )
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
      scalaVersion        := "2.11.0-SNAPSHOT",
      scalaOrganization   := "org.scala-lang.macro-paradise",

      scalacOptions       := Seq(
        "-feature",
        "-deprecation",
        "-unchecked"),

      resolvers           ++= Seq(
        Classpaths.typesafeSnapshots,
        Resolver.sonatypeRepo("snapshots")
      )
    )
}
