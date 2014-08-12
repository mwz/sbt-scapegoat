package com.sksamuel.scapegoat.sbt

import sbt._
import sbt.Keys._

/** @author Stephen Samuel */
object ScapegoatSbtPlugin extends AutoPlugin {

  val GroupId = "com.sksamuel.scapegoat"
  val ArtifactId = "scalac-scapegoat-plugin"

  object autoImport {
    lazy val scapegoatVersion = settingKey[String]("The version of the scala plugin to use")
    lazy val disabledInspections = settingKey[Seq[String]]("Inspections that are disabled globally")
    lazy val enabledInspections = settingKey[Seq[String]]("Inspections that are explicitly enabled")
    lazy val scapegoatMaxErrors = settingKey[Int]("Maximum number of errors before the build will fail")
    lazy val scapegoatMaxWarnings = settingKey[Int]("Maximum number of warnings before the build will fail")
    lazy val scapegoatMaxInfos = settingKey[Int]("Maximum number of infos before the build will fail")
    lazy val scapegoatConsoleOutput = settingKey[Boolean]("Output results of scan to the console during compilation")
    lazy val scapegoatOutputPath = settingKey[String]("Directory where reports will be written")
    lazy val scapegoatVerbose = settingKey[Boolean]("Verbose mode for inspections")
  }

  import autoImport._

  override def trigger = allRequirements
  override lazy val projectSettings = Seq(
    scapegoatVersion := "0.90.12",
    libraryDependencies ++= Seq(
      GroupId % (ArtifactId + "_" + scalaBinaryVersion.value) % scapegoatVersion.value % Compile.name
    ),
    scapegoatConsoleOutput := true,
    scapegoatVerbose := false,
    scapegoatMaxInfos := -1,
    scapegoatMaxWarnings := -1,
    scapegoatMaxErrors := -1,
    disabledInspections := Nil,
    enabledInspections := Nil,
    scapegoatOutputPath := (crossTarget in Compile).value.getAbsolutePath + "/scapegoat-report",
    scalacOptions in(Compile, compile) ++= {
      // find all deps for the compile scope
      val scapegoatDependencies = update.value matching configurationFilter(Compile.name)
      // ensure we have the scapegoat dependency on the classpath and if so add it as a scalac plugin
      scapegoatDependencies.find(_.getAbsolutePath.contains(ArtifactId)) match {
        case None => throw new Exception(s"Fatal: $ArtifactId not in libraryDependencies")
        case Some(classpath) =>
          val path = scapegoatOutputPath.value
          streams.value.log.info(s"[scapegoat] setting output dir to [$path]")

          val disabled = disabledInspections.value
          if (disabled.size > 0)
            streams.value.log.info(s"[scapegoat] disabled inspections: " + disabled.mkString(","))

          val enabled = enabledInspections.value
          if (enabled.size > 0)
            streams.value.log.info(s"[scapegoat] enabled inspections: " + enabled.mkString(","))

          Seq(
            "-Xplugin:" + classpath.getAbsolutePath,
            "-P:scapegoat:verbose:" + scapegoatVerbose.value,
            "-P:scapegoat:consoleOutput:" + scapegoatConsoleOutput.value,
            "-P:scapegoat:dataDir:" + path,
            "-P:scapegoat:disabledInspections:" + disabled.mkString(":"),
            "-P:scapegoat:enabledInspections:" + enabled.mkString(":")
          )
      }
    }
  )
}
