/*
 * Lotus
 * Copyright (c) 2024 IThundxr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${"fabric_loader_version"()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${"fabric_api_version"()}")
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