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

package dev.ithundxr.lotus.gradle.asm

import dev.ithundxr.lotus.gradle.asm.internal.SubprojectType
import dev.ithundxr.lotus.gradle.asm.transformers.DevEnvMixinTransformer
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter

class LotusGradleASM {
    fun transformClass(project: Project, bytes: ByteArray): ByteArray {
        // Get project type
        val projectType = SubprojectType.getProjectType(project)

        val node = ClassNode()
        ClassReader(bytes).accept(node, 0)

        // Transformers
        DevEnvMixinTransformer().transform(projectType, node)

        // Verify the bytecode is valid
        val byteArray = ClassWriter(0).also { node.accept(it) }.toByteArray()
        ClassReader(byteArray).accept(CheckClassAdapter(null), 0)
        return byteArray
    }
}