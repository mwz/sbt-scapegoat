sbt-scapegoat
=============

sbt-scapegoat is a plugin for SBT that integrates the scapegoat [static code analysis library](http://en.wikipedia.org/wiki/Static_program_analysis). Find out more about scapegoat at the [scapegoat project page](https://github.com/sksamuel/scapegoat).

#### How to use

sbt-scapegoat is an [auto plugin](https://typesafe.com/blog/preview-of-upcoming-sbt-10-features-read-about-the-new-plugins). This means you need SBT 0.13.5 or higher. If you are using an earlier 0.13.x build, you should be able to upgrade to 0.13.5 without any issues.

*Please note*: [scapegoat](https://github.com/sksamuel/scapegoat) only works with Scala 2.11.x. There are no plans to release a 2.10.x branch.

Add the plugin to your build with the following in project/plugins.sbt:

```scala
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "0.90.2")
```

That's it! Then when SBT compiles your code, the scapegoat reports will be generated. You should find these inside 
`target/scala-2.11/scapegoat-report`

If the plugin is working properly then you should see output like this in your build log:

```
[info] [scapegoat] setting output dir to [/home/sam/development/workspace/elastic4s/target/scala-2.11/scapegoat-report]
[info] [scapegoat] disabling inspections: ExpressionAsStatement,VarUse
[info] Compiling 47 Scala sources to /home/sam/development/workspace/elastic4s/target/scala-2.11/classes...
[info] [scapegoat]: 55 activated inspections
[info] [scapegoat]: Beginning anaylsis...
[warn] [scapegoat]: Anaylsis complete - 17 errors; 1 warns 2 infos
[info] [scapegoat]: Written HTML report [/home/sam/development/workspace/elastic4s/target/scala-2.11/scapegoat-report/scapegoat.html]
[info] [scapegoat]: Written XML report [/home/sam/development/workspace/elastic4s/target/scala-2.11/scapegoat-report/scapegoat.xml]
```

#### False positives

Please note that scapegoat is a new project. While it's been tested on some common open source projects, there is still a good chance you'll find false positives. Please open up issues if you run into these so we can fix them.

#### Disabling inspections

Sometimes you might want to disable an inspection, should you disagree with it, or have a need to override it completely.

To do this add the following key to your sbt build, and include the simple name of the inspections you wish to disable. (The full names of inspections are displayed in the XML and HTML reports, in this key you include the simple names).

Eg,

```scala
disabledInspections := Seq("ExpressionAsStatement", "VarUse")
```

