package me.mattco.reeva.runtime.values.arrays

import me.mattco.reeva.runtime.Realm
import me.mattco.reeva.runtime.annotations.JSMethod
import me.mattco.reeva.runtime.contexts.ExecutionContext
import me.mattco.reeva.runtime.values.JSValue
import me.mattco.reeva.runtime.values.objects.JSObject
import me.mattco.reeva.runtime.values.primitives.JSUndefined

class JSArrayProto private constructor(realm: Realm) : JSObject(realm, realm.objectProto) {
    companion object {
        fun create(realm: Realm) = JSArrayProto(realm).also { it.init() }
    }
}
