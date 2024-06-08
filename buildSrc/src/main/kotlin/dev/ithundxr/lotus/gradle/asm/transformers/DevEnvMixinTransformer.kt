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

package dev.ithundxr.lotus.gradle.asm.transformers;

import dev.ithundxr.lotus.gradle.asm.internal.SubprojectType
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.util.*

class DevEnvMixinTransformer {
    fun transform(project:SubprojectType, node: ClassNode) {
        node.methods.removeIf { methodNode: MethodNode -> removeIfDevMixin(node.name, methodNode.visibleAnnotations) }
    }

    private fun removeIfDevMixin(nodeName: String, visibleAnnotations: List<AnnotationNode>?): Boolean {
        // Don't remove methods if it's not a GHA build/Release build
        if (System.getenv("GITHUB_RUN_NUMBER")?.toInt() == null || !nodeName.lowercase(Locale.ROOT).matches(Regex(".*/mixin/.*Mixin")))
            return false

        if (visibleAnnotations != null) {
            for (annotationNode in visibleAnnotations) {
                if (annotationNode.desc == "Ldev/ithundxr/lotus/annotation/mixin/DevEnvMixin;") {
                    println("Removed Method/Field Annotated With @DevEnvMixin from: $nodeName")
                    return true
                }
            }
        }

        return false
    }
}
