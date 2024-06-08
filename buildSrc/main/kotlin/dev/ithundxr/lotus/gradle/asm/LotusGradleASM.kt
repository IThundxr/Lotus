package dev.ithundxr.lotus.gradle.asm

import dev.ithundxr.numismaticsgradle.asm.internal.SubprojectType
import dev.ithundxr.numismaticsgradle.asm.transformers.CCCapabilitiesTransformer
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter

class LotusGradleASM {
    fun transformClass(project: Project, bytes: ByteArray): ByteArray {
        // Get project type
        val projectType = SubprojectType.getProjectType(project)

        var node = ClassNode()
        ClassReader(bytes).accept(node, 0)

        // Transformers
        //node = CCCapabilitiesTransformer().transform(projectType, node)

        // Verify the bytecode is valid
        val byteArray = ClassWriter(0).also { node.accept(it) }.toByteArray()
        ClassReader(byteArray).accept(CheckClassAdapter(null), 0)
        return byteArray
    }
}