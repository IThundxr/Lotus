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

plugins {
    kotlin("jvm") version "1.9.+"
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

val isRelease = System.getenv("RELEASE_BUILD")?.toBoolean() ?: false
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt()

val build = buildNumber?.let { "-build.${it}" } ?: "-local"

version = project.properties["version"]!!.toString() + if (isRelease) "" else build
base.archivesName.set("LotusGradle")
group = "dev.ithundxr.lotus"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:${"asm_version"()}")
    implementation("org.ow2.asm:asm-tree:${"asm_version"()}")
    implementation("org.ow2.asm:asm-util:${"asm_version"()}")
}

gradlePlugin {
    plugins {
        create("lotusPlugin") {
            id = "dev.ithundxr.lotus.gradle"
            implementationClass = "dev.ithundxr.lotus.gradle.LotusGradlePlugin"
        }
    }
}

publishing {
    repositories {
        val mavenToken = System.getenv("MAVEN_TOKEN")
        if (mavenToken != null && mavenToken.isNotEmpty() && isRelease) {
            maven {
                url = uri("https://maven.ithundxr.dev/releases")
                credentials {
                    username = "lotus-github"
                    password = mavenToken
                }
            }
        }
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
