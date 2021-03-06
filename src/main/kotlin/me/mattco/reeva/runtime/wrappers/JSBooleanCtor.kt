package me.mattco.reeva.runtime.wrappers

import me.mattco.reeva.runtime.Operations
import me.mattco.reeva.core.Realm
import me.mattco.reeva.runtime.JSValue
import me.mattco.reeva.runtime.SlotName
import me.mattco.reeva.runtime.functions.JSNativeFunction
import me.mattco.reeva.runtime.primitives.JSUndefined
import me.mattco.reeva.utils.JSArguments
import me.mattco.reeva.utils.argument
import me.mattco.reeva.utils.toValue

class JSBooleanCtor private constructor(realm: Realm) : JSNativeFunction(realm, "Boolean", 1) {
    init {
        isConstructable = true
    }

    override fun evaluate(arguments: JSArguments): JSValue {
        val bool = Operations.toBoolean(arguments.argument(0)).toValue()
        val newTarget = super.newTarget
        if (newTarget == JSUndefined)
            return bool
        if (newTarget == realm.booleanCtor)
            return JSBooleanObject.create(realm, Operations.toBoolean(arguments.argument(0)).toValue())
        return Operations.ordinaryCreateFromConstructor(newTarget, realm.booleanProto, listOf(SlotName.BooleanData)).also {
            it.setSlot(SlotName.BooleanData, bool)
        }
    }

    companion object {
        fun create(realm: Realm) = JSBooleanCtor(realm).initialize()
    }
}
