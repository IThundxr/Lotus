plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentFabric: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentFabric.extendsFrom(common)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }

    val mixinExtras = "io.github.llamalad7:mixinextras-fabric:${"mixin_extras_version"()}"

    annotationProcessor(mixinExtras)
    implementation(mixinExtras)
}

tasks.processResources {
    val properties = mapOf(
            "version" to version as String,
            "fabric_loader_version" to "fabric_loader_version"(),
            "fabric_api_version" to "fabric_api_version"(),
            "minecraft_version" to "minecraft_version"()
    )

    properties.forEach { (k, v) -> inputs.property(k, v) }

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier = null
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}