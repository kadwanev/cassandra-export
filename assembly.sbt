import AssemblyKeys._ // put this at the top of the file

assemblySettings

// your assembly settings here
test in assembly := {}

//jarName in assembly <<= (name, version) { (name, version) => name + "-" + version + ".jar" }

excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  val excludes = Set(
    "jsp-.*\\.jar",
    "jasper-compiler-.*\\.jar",
    "servlet-api-.*\\.jar",         // 3 of them?
    "minlog-.*\\.jar",              // Otherwise causes conflicts with Kyro (which bundles it)
    "janino-.*\\.jar",              // Janino includes a broken signature, and is not needed anyway
    "commons-beanutils-.*\\.jar",   // Clash with each other and with commons-collections
    "hadoop-core-.*\\.jar",
    "hadoop-tools-.*\\.jar"
  )
  cp filter { jar => excludes.map(pattern => jar.data.getName.matches(pattern)).contains(true) }
}

mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case "project.clj" => MergeStrategy.discard // Leiningen build files
    case x => old(x)
  }
}
