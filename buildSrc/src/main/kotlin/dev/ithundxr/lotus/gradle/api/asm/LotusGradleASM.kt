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

package dev.ithundxr.lotus.gradle.api.asm

import dev.ithundxr.lotus.gradle.api.asm.util.SubprojectType
import dev.ithundxr.lotus.gradle.api.asm.util.IClassTransformer
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.isSubclassOf

class LotusGradleASM private constructor() {
    companion object {
        private val storedClasses = mutableSetOf<KClass<*>>()

        internal fun transformClass(project: Project, bytes: ByteArray): ByteArray {
            // Get project type
            val projectType = SubprojectType.getProjectType(project)

            val node = ClassNode()
            ClassReader(bytes).accept(node, 0)

            for (kClass in storedClasses) {
                val instance = kClass.createInstance() as IClassTransformer
                val method = kClass.declaredMemberFunctions.find { it.name == "transform" }
                    ?: throw IllegalArgumentException("Method transform(SubprojectType, ClassNode) not found in class ${kClass.simpleName}")

                method.call(instance, projectType, node)
            }

            // Verify the bytecode is valid
            val byteArray = ClassWriter(0).also { node.accept(it) }.toByteArray()
            ClassReader(byteArray).accept(CheckClassAdapter(null), 0)
            return byteArray
        }

        /**
         * Adds a transformer that extends {@see IClassTransformer}, Will never add the same transformer twice
         * as this is backed by a set of KClasses.
         */
        fun addTransformer(kClass: KClass<*>) {
            if (!kClass.isSubclassOf(IClassTransformer::class))
                throw IllegalArgumentException("Class ${kClass.simpleName} does not implement IClassTransformer")

            storedClasses.add(kClass)
        }
    }
}