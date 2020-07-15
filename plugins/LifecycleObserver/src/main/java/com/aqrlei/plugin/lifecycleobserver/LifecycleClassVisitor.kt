package com.aqrlei.plugin.lifecycleobserver

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * created by AqrLei on 2020/7/14
 */
class LifecycleClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM7, cv), Opcodes {
    private lateinit var clazzName: String

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        clazzName = name ?: ""
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        VisitHelper.dispatchMethodVisit(clazzName, name,
            callbackOnCreate = {
                return LifecycleOnCreateMethodVisitor(mv)

            },
            callbackOnDestroy = {
                return LifecycleOnDestroyMethodVisitor(mv)
            })
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}