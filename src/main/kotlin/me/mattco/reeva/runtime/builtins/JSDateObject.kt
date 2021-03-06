package me.mattco.reeva.runtime.builtins

import me.mattco.reeva.core.Realm
import me.mattco.reeva.runtime.SlotName
import me.mattco.reeva.runtime.objects.JSObject
import java.time.ZonedDateTime

// null dateValue indicates an invalid date (usually when passing NaN into the date ctor)
class JSDateObject private constructor(realm: Realm, dateValue: ZonedDateTime?) : JSObject(realm, realm.dateProto) {
    var dateValue by slot(SlotName.DateValue, dateValue)

    companion object {
        fun create(realm: Realm, dateValue: ZonedDateTime?) = JSDateObject(realm, dateValue).initialize()
    }
}
