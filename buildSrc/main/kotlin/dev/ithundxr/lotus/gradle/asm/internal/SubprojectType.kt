package dev.ithundxr.lotus.gradle.asm.internal

import org.gradle.api.Project

enum class SubprojectType {
    COMMON, FABRIC, FORGE;

    companion object {
        fun getProjectType(project: Project): SubprojectType {
            return when (project.path) {
                ":common" -> COMMON
                ":fabric" -> FABRIC
                ":forge" -> FORGE
                else -> throw IllegalStateException("Invalid Project Type")
            }
        }
    }
}