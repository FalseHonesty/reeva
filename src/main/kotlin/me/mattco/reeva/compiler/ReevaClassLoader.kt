package me.mattco.reeva.compiler

import me.mattco.reeva.utils.expect

class ReevaClassLoader : ClassLoader() {
    private val classes = mutableMapOf<String, ByteArray>()

    fun addClass(className: String, bytes: ByteArray) {
        classes[className] = bytes
    }

    override fun findClass(name: String?): Class<*> {
        if (name in classes) {
            val bytes = classes[name]!!
            return defineClass(name, bytes, 0, bytes.size)
        }
        return super.findClass(name)
    }
}
