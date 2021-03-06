package me.mattco.reeva.core.environment

import me.mattco.reeva.runtime.JSValue
import me.mattco.reeva.runtime.annotations.ECMAImpl
import me.mattco.reeva.runtime.primitives.JSUndefined
import me.mattco.reeva.utils.Errors
import me.mattco.reeva.utils.ecmaAssert

open class DeclarativeEnvRecord(outerEnv: EnvRecord?) : EnvRecord(outerEnv) {
    override val isStrict = outerEnv?.isStrict ?: false

    internal val bindings = mutableMapOf<String, Binding>()

    @ECMAImpl("8.1.1.1.1")
    override fun hasBinding(name: String) = name in bindings

    @ECMAImpl("8.1.1.1.2")
    override fun createMutableBinding(name: String, canBeDeleted: Boolean) {
        ecmaAssert(!hasBinding(name))

        bindings[name] = Binding(
            immutable = false,
            deletable = canBeDeleted,
            initialized = false,
        )
    }

    @ECMAImpl("8.1.1.1.3")
    override fun createImmutableBinding(name: String, strict: Boolean) {
        ecmaAssert(!hasBinding(name))

        bindings[name] = Binding(
            immutable = true,
            deletable = false,
            initialized = false,
            strict = strict,
        )
    }

    @ECMAImpl("8.1.1.1.4")
    override fun initializeBinding(name: String, value: JSValue) {
        ecmaAssert(hasBinding(name))

        val binding = bindings[name]!!
        ecmaAssert(!binding.initialized)

        binding.value = value
        binding.initialized = true
    }

    @ECMAImpl("8.1.1.1.5")
    override fun setMutableBinding(name: String, value: JSValue, throwOnFailure: Boolean) {
        if (!hasBinding(name)) {
            if (throwOnFailure)
                Errors.StrictModeMutableSet(name).throwReferenceError()
            createMutableBinding(name, canBeDeleted = true)
            initializeBinding(name, value)
            return
        }

        var shouldThrow = throwOnFailure

        val binding = bindings[name]!!
        if (binding.strict)
            shouldThrow = true

        if (!binding.initialized)
            Errors.AssignmentBeforeInitialization(name).throwReferenceError()

        if (!binding.immutable) {
            binding.value = value
        } else if (shouldThrow) {
            Errors.AssignmentToConstant(name).throwTypeError()
        }
    }

    @ECMAImpl("8.1.1.1.6")
    override fun getBindingValue(name: String, throwOnNotFound: Boolean): JSValue {
        ecmaAssert(hasBinding(name))

        val binding = bindings[name]!!
        if (!binding.initialized)
            Errors.AssignmentBeforeInitialization(name).throwReferenceError()
        return binding.value
    }

    @ECMAImpl("8.1.1.1.7")
    override fun deleteBinding(name: String): Boolean {
        ecmaAssert(hasBinding(name))

        val binding = bindings[name]!!
        if (!binding.deletable)
            return false
        bindings.remove(name)
        return true
    }

    @ECMAImpl("8.1.1.1.8")
    override fun hasThisBinding() = false

    @ECMAImpl("8.1.1.1.9")
    override fun hasSuperBinding() = false

    @ECMAImpl("8.1.1.1.10")
    override fun withBaseObject() = JSUndefined

    companion object {
        @JvmStatic @ECMAImpl("8.1.2.2", "NewDeclarativeEnvironment")
        fun create(old: EnvRecord?): DeclarativeEnvRecord {
            return DeclarativeEnvRecord(old)
        }
    }
}
