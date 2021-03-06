package me.mattco.reeva.runtime.errors

import me.mattco.reeva.core.Realm
import me.mattco.reeva.runtime.objects.Descriptor
import me.mattco.reeva.runtime.objects.JSObject

class JSSyntaxErrorObject private constructor(realm: Realm, message: String? = null) : JSErrorObject(realm, message, realm.syntaxErrorProto) {
    companion object {
        @JvmStatic
        fun create(realm: Realm, message: String? = null) = JSSyntaxErrorObject(realm, message).initialize()
    }
}

class JSSyntaxErrorProto private constructor(realm: Realm) : JSObject(realm, realm.errorProto) {
    override fun init() {
        super.init()
        defineOwnProperty("constructor", realm.syntaxErrorCtor, Descriptor.CONFIGURABLE or Descriptor.WRITABLE)
    }

    companion object {
        fun create(realm: Realm) = JSSyntaxErrorProto(realm).initialize()
    }
}

class JSSyntaxErrorCtor private constructor(realm: Realm) : JSErrorCtor(realm, "SyntaxError") {
    override fun errorProto(): JSObject = realm.syntaxErrorProto

    companion object {
        fun create(realm: Realm) = JSSyntaxErrorCtor(realm).initialize()
    }
}
