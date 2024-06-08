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

import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("architectury-plugin") version "3.4.+"
    id("dev.architectury.loom") version "1.5.+" apply false
    id("dev.ithundxr.silk") version "0.11.+" // https://github.com/IThundxr/silk
}

architectury {
    minecraft = "minecraft_version"()
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    repositories {
        maven { url = uri("https://maven.parchmentmc.org") } // Parchment mappings
        maven { url = uri("https://maven.quiltmc.org/repository/release") } // Quilt Mappings
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${"minecraft_version"()}")
        // layered mappings - Mojmap names, parchment and QM docs and parameters
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").layered {
            mappings("org.quiltmc:quilt-mappings:${"minecraft_version"()}+build.${"qm_version"()}:intermediary-v2")
            parchment("org.parchmentmc.data:parchment-${"minecraft_version"()}:${"parchment_version"()}@zip")
            officialMojangMappings { nameSyntheticMembers = false }
        })

        implementation("dev.yumi.commons:yumi-commons-event:${"yumi_event_version"()}")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    base.archivesName.set("archives_base_name"())
    version = "mod_version"()
    group = "maven_group"()

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
