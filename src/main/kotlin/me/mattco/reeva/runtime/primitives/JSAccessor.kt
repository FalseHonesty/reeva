package me.mattco.reeva.runtime.primitives

import me.mattco.reeva.runtime.JSValue
import me.mattco.reeva.runtime.functions.JSFunction

class JSAccessor(
    var getter: JSFunction?,
    var setter: JSFunction?,
) : JSValue() {
    fun callGetter(thisValue: JSValue) = getter?.call(thisValue, emptyList()) ?: JSUndefined

    fun callSetter(thisValue: JSValue, value: JSValue) = setter?.call(thisValue, listOf(value)) ?: JSUndefined
}
