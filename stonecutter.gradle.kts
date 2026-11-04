plugins {
    id("dev.kikugie.stonecutter")
}

// The IDE opens this node. `gradlew build` still builds every node below.
stonecutter active "26.3-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
    filters.include("**/*.json", "**/*.json5", "**/*.toml", "**/*.mcmeta")
}
