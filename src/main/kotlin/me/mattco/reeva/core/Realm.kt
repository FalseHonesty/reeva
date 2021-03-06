package me.mattco.reeva.core

import me.mattco.reeva.ast.ScriptNode
import me.mattco.reeva.core.environment.EnvRecord
import me.mattco.reeva.core.environment.GlobalEnvRecord
import me.mattco.reeva.core.modules.resolver.ModuleResolver
import me.mattco.reeva.jvmcompat.JSClassProto
import me.mattco.reeva.jvmcompat.JSPackageObject
import me.mattco.reeva.jvmcompat.JSPackageProto
import me.mattco.reeva.parser.Parser
import me.mattco.reeva.runtime.arrays.JSArrayCtor
import me.mattco.reeva.runtime.arrays.JSArrayProto
import me.mattco.reeva.runtime.builtins.*
import me.mattco.reeva.runtime.builtins.promises.JSPromiseCtor
import me.mattco.reeva.runtime.builtins.promises.JSPromiseProto
import me.mattco.reeva.runtime.builtins.regexp.JSRegExpCtor
import me.mattco.reeva.runtime.builtins.regexp.JSRegExpProto
import me.mattco.reeva.runtime.builtins.regexp.JSRegExpStringIteratorProto
import me.mattco.reeva.runtime.errors.*
import me.mattco.reeva.runtime.functions.JSFunctionCtor
import me.mattco.reeva.runtime.functions.JSFunctionProto
import me.mattco.reeva.runtime.global.JSConsole
import me.mattco.reeva.runtime.global.JSConsoleProto
import me.mattco.reeva.runtime.iterators.*
import me.mattco.reeva.runtime.memory.*
import me.mattco.reeva.runtime.objects.*
import me.mattco.reeva.runtime.objects.JSObject.Companion.initialize
import me.mattco.reeva.runtime.primitives.JSSymbol
import me.mattco.reeva.runtime.wrappers.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("ObjectPropertyName")
class Realm(var moduleResolver: ModuleResolver? = null) {
    internal val isGloballyInitialized: Boolean
        get() = ::globalObject.isInitialized && ::globalEnv.isInitialized

    lateinit var globalObject: JSObject
        private set
    lateinit var globalEnv: GlobalEnvRecord
        private set

    // Special objects that have to be handled manually
    lateinit var objectProto: JSObjectProto private set
    lateinit var functionProto: JSFunctionProto private set
    lateinit var symbolProto: JSSymbolProto private set
    lateinit var symbolCtor: JSSymbolCtor private set

    val numberProto by lazy { JSNumberProto.create(this) }
    val bigIntProto by lazy { JSBigIntProto.create(this) }
    val booleanProto by lazy { JSBooleanProto.create(this) }
    val stringProto by lazy { JSStringProto.create(this) }
    val regExpProto by lazy { JSRegExpProto.create(this) }
    val arrayProto by lazy { JSArrayProto.create(this) }
    val setProto by lazy { JSSetProto.create(this) }
    val mapProto by lazy { JSMapProto.create(this) }
    val promiseProto by lazy { JSPromiseProto.create(this) }
    val dateProto by lazy { JSDateProto.create(this) }
    val iteratorProto by lazy { JSIteratorProto.create(this) }
    val arrayIteratorProto by lazy { JSArrayIteratorProto.create(this) }
    val setIteratorProto by lazy { JSSetIteratorProto.create(this) }
    val mapIteratorProto by lazy { JSMapIteratorProto.create(this) }
    val objectPropertyIteratorProto by lazy { JSObjectPropertyIteratorProto.create(this) }
    val listIteratorProto by lazy { JSListIteratorProto.create(this) }
    val regExpStringIteratorProto by lazy { JSRegExpStringIteratorProto.create(this) }
    val consoleProto by lazy { JSConsoleProto.create(this) }

    val dataViewProto by lazy { JSDataViewProto.create(this) }
    val arrayBufferProto by lazy { JSArrayBufferProto.create(this) }
    val typedArrayProto by lazy { JSTypedArrayProto.create(this) }
    val int8ArrayProto by lazy { JSInt8ArrayProto.create(this) }
    val uint8ArrayProto by lazy { JSUint8ArrayProto.create(this) }
    val uint8CArrayProto by lazy { JSUint8CArrayProto.create(this) }
    val int16ArrayProto by lazy { JSInt16ArrayProto.create(this) }
    val uint16ArrayProto by lazy { JSUint16ArrayProto.create(this) }
    val int32ArrayProto by lazy { JSInt32ArrayProto.create(this) }
    val uint32ArrayProto by lazy { JSUint32ArrayProto.create(this) }
    val float32ArrayProto by lazy { JSFloat32ArrayProto.create(this) }
    val float64ArrayProto by lazy { JSFloat64ArrayProto.create(this) }
    val bigInt64ArrayProto by lazy { JSBigInt64ArrayProto.create(this) }
    val bigUint64ArrayProto by lazy { JSBigUint64ArrayProto.create(this) }

    val errorProto by lazy { JSErrorProto.create(this) }
    val evalErrorProto by lazy { JSEvalErrorProto.create(this) }
    val internalErrorProto by lazy { JSInternalErrorProto.create(this) }
    val typeErrorProto by lazy { JSTypeErrorProto.create(this) }
    val rangeErrorProto by lazy { JSRangeErrorProto.create(this) }
    val referenceErrorProto by lazy { JSReferenceErrorProto.create(this) }
    val syntaxErrorProto by lazy { JSSyntaxErrorProto.create(this) }
    val uriErrorProto by lazy { JSURIErrorProto.create(this) }

    val objectCtor by lazy { JSObjectCtor.create(this) }
    val numberCtor by lazy { JSNumberCtor.create(this) }
    val bigIntCtor by lazy { JSBigIntCtor.create(this) }
    val booleanCtor by lazy { JSBooleanCtor.create(this) }
    val stringCtor by lazy { JSStringCtor.create(this) }
    val regExpCtor by lazy { JSRegExpCtor.create(this) }
    val functionCtor by lazy { JSFunctionCtor.create(this) }
    val arrayCtor by lazy { JSArrayCtor.create(this) }
    val setCtor by lazy { JSSetCtor.create(this) }
    val mapCtor by lazy { JSMapCtor.create(this) }
    val proxyCtor by lazy { JSProxyCtor.create(this) }
    val promiseCtor by lazy { JSPromiseCtor.create(this) }
    val dateCtor by lazy { JSDateCtor.create(this) }

    val dataViewCtor by lazy { JSDataViewCtor.create(this) }
    val arrayBufferCtor by lazy { JSArrayBufferCtor.create(this) }
    val typedArrayCtor by lazy { JSTypedArrayCtor.create(this) }
    val int8ArrayCtor by lazy { JSInt8ArrayCtor.create(this) }
    val uint8ArrayCtor by lazy { JSUint8ArrayCtor.create(this) }
    val uint8CArrayCtor by lazy { JSUint8CArrayCtor.create(this) }
    val int16ArrayCtor by lazy { JSInt16ArrayCtor.create(this) }
    val uint16ArrayCtor by lazy { JSUint16ArrayCtor.create(this) }
    val int32ArrayCtor by lazy { JSInt32ArrayCtor.create(this) }
    val uint32ArrayCtor by lazy { JSUint32ArrayCtor.create(this) }
    val float32ArrayCtor by lazy { JSFloat32ArrayCtor.create(this) }
    val float64ArrayCtor by lazy { JSFloat64ArrayCtor.create(this) }
    val bigInt64ArrayCtor by lazy { JSBigInt64ArrayCtor.create(this) }
    val bigUint64ArrayCtor by lazy { JSBigUint64ArrayCtor.create(this) }

    val errorCtor by lazy { JSErrorCtor.create(this) }
    val evalErrorCtor by lazy { JSEvalErrorCtor.create(this) }
    val internalErrorCtor by lazy { JSInternalErrorCtor.create(this) }
    val typeErrorCtor by lazy { JSTypeErrorCtor.create(this) }
    val rangeErrorCtor by lazy { JSRangeErrorCtor.create(this) }
    val referenceErrorCtor by lazy { JSReferenceErrorCtor.create(this) }
    val syntaxErrorCtor by lazy { JSSyntaxErrorCtor.create(this) }
    val uriErrorCtor by lazy { JSURIErrorCtor.create(this) }

    val mathObj by lazy { JSMathObject.create(this) }
    val reflectObj by lazy { JSReflectObject.create(this) }
    val jsonObj by lazy { JSONObject.create(this) }
    val consoleObj by lazy { JSConsole.create(this) }

    val packageProto by lazy { JSPackageProto.create(this) }
    val classProto by lazy { JSClassProto.create(this) }
    val packageObj by lazy { JSPackageObject.create(this) }

    val emptyShape = Shape(this)
    val newObjectShape = Shape(this)

    fun setGlobalObject(obj: JSObject) {
        globalObject = obj
        globalEnv = GlobalEnvRecord.create(globalObject)
    }

    fun initObjects() {
        objectProto = JSObjectProto.create(this)
        functionProto = JSFunctionProto.create(this)
        functionProto.initialize()
        objectProto.initialize()

        // Must be created after well-known symbols
        symbolCtor = JSSymbolCtor.create(this)
        symbolProto = JSSymbolProto.create(this)

        // These can't be in the init method of the objects due to circularity
        objectCtor.defineOwnProperty("prototype", objectProto, Descriptor.HAS_BASIC)
        functionCtor.defineOwnProperty("prototype", functionProto, Descriptor.HAS_BASIC)
        numberCtor.defineOwnProperty("prototype", numberProto, Descriptor.HAS_BASIC)
        bigIntCtor.defineOwnProperty("prototype", bigIntProto, Descriptor.HAS_BASIC)
        stringCtor.defineOwnProperty("prototype", stringProto, Descriptor.HAS_BASIC)
        regExpCtor.defineOwnProperty("prototype", regExpProto, Descriptor.HAS_BASIC)
        booleanCtor.defineOwnProperty("prototype", booleanProto, Descriptor.HAS_BASIC)
        symbolCtor.defineOwnProperty("prototype", symbolProto, Descriptor.HAS_BASIC)
        arrayCtor.defineOwnProperty("prototype", arrayProto, Descriptor.HAS_BASIC)
        setCtor.defineOwnProperty("prototype", setProto, Descriptor.HAS_BASIC)
        mapCtor.defineOwnProperty("prototype", mapProto, Descriptor.HAS_BASIC)
        dateCtor.defineOwnProperty("prototype", dateProto, Descriptor.HAS_BASIC)

        dataViewCtor.defineOwnProperty("prototype", dataViewProto, Descriptor.HAS_BASIC)
        arrayBufferCtor.defineOwnProperty("prototype", arrayBufferProto, Descriptor.HAS_BASIC)
        typedArrayCtor.defineOwnProperty("prototype", typedArrayProto, Descriptor.HAS_BASIC)
        int8ArrayCtor.defineOwnProperty("prototype", int8ArrayProto, Descriptor.HAS_BASIC)
        uint8ArrayCtor.defineOwnProperty("prototype", uint8ArrayProto, Descriptor.HAS_BASIC)
        uint8CArrayCtor.defineOwnProperty("prototype", uint8CArrayProto, Descriptor.HAS_BASIC)
        int16ArrayCtor.defineOwnProperty("prototype", int16ArrayProto, Descriptor.HAS_BASIC)
        uint16ArrayCtor.defineOwnProperty("prototype", uint16ArrayProto, Descriptor.HAS_BASIC)
        int32ArrayCtor.defineOwnProperty("prototype", int32ArrayProto, Descriptor.HAS_BASIC)
        uint32ArrayCtor.defineOwnProperty("prototype", uint32ArrayProto, Descriptor.HAS_BASIC)
        float32ArrayCtor.defineOwnProperty("prototype", float32ArrayProto, Descriptor.HAS_BASIC)
        float64ArrayCtor.defineOwnProperty("prototype", float64ArrayProto, Descriptor.HAS_BASIC)
        bigInt64ArrayCtor.defineOwnProperty("prototype", bigInt64ArrayProto, Descriptor.HAS_BASIC)
        bigUint64ArrayCtor.defineOwnProperty("prototype", bigUint64ArrayProto, Descriptor.HAS_BASIC)

        errorCtor.defineOwnProperty("prototype", errorProto, Descriptor.HAS_BASIC)
        evalErrorCtor.defineOwnProperty("prototype", evalErrorProto, Descriptor.HAS_BASIC)
        internalErrorCtor.defineOwnProperty("prototype", internalErrorProto, Descriptor.HAS_BASIC)
        typeErrorCtor.defineOwnProperty("prototype", typeErrorProto, Descriptor.HAS_BASIC)
        rangeErrorCtor.defineOwnProperty("prototype", rangeErrorProto, Descriptor.HAS_BASIC)
        referenceErrorCtor.defineOwnProperty("prototype", referenceErrorProto, Descriptor.HAS_BASIC)
        syntaxErrorCtor.defineOwnProperty("prototype", syntaxErrorProto, Descriptor.HAS_BASIC)
        uriErrorCtor.defineOwnProperty("prototype", uriErrorProto, Descriptor.HAS_BASIC)

        functionProto.defineOwnProperty("constructor", functionCtor, Descriptor.CONFIGURABLE or Descriptor.WRITABLE)

        newObjectShape.setPrototypeWithoutTransition(objectProto)
    }

    data class ScriptRecord(
        val realm: Realm,
        var env: EnvRecord?,
        val scriptOrModule: ScriptNode,
        val errors: List<Parser.SyntaxError> = emptyList()
    )

    internal companion object {
        val EMPTY_REALM = Realm()

        val globalSymbolRegistry = ConcurrentHashMap<String, JSSymbol>()

        // To get access to the symbol via their name without reflection
        val wellknownSymbols = mutableMapOf<String, JSSymbol>()

        lateinit var `@@asyncIterator`: JSSymbol private set
        lateinit var `@@hasInstance`: JSSymbol private set
        lateinit var `@@isConcatSpreadable`: JSSymbol private set
        lateinit var `@@iterator`: JSSymbol private set
        lateinit var `@@match`: JSSymbol private set
        lateinit var `@@matchAll`: JSSymbol private set
        lateinit var `@@replace`: JSSymbol private set
        lateinit var `@@search`: JSSymbol private set
        lateinit var `@@species`: JSSymbol private set
        lateinit var `@@split`: JSSymbol private set
        lateinit var `@@toPrimitive`: JSSymbol private set
        lateinit var `@@toStringTag`: JSSymbol private set
        lateinit var `@@unscopables`: JSSymbol private set

        fun setupSymbols() {
            `@@asyncIterator` = JSSymbol("Symbol.asyncIterator").also { wellknownSymbols["@@asyncIterator"] = it }
            `@@hasInstance` = JSSymbol("Symbol.hasInstance").also { wellknownSymbols["@@hasInstance"] = it }
            `@@isConcatSpreadable` = JSSymbol("Symbol.isConcatSpreadable").also { wellknownSymbols["@@isConcatSpreadable"] = it }
            `@@iterator` = JSSymbol("Symbol.iterator").also { wellknownSymbols["@@iterator"] = it }
            `@@match` = JSSymbol("Symbol.match").also { wellknownSymbols["@@match"] = it }
            `@@matchAll` = JSSymbol("Symbol.matchAll").also { wellknownSymbols["@@matchAll"] = it }
            `@@replace` = JSSymbol("Symbol.replace").also { wellknownSymbols["@@replace"] = it }
            `@@search` = JSSymbol("Symbol.search").also { wellknownSymbols["@@search"] = it }
            `@@species` = JSSymbol("Symbol.species").also { wellknownSymbols["@@species"] = it }
            `@@split` = JSSymbol("Symbol.split").also { wellknownSymbols["@@split"] = it }
            `@@toPrimitive` = JSSymbol("Symbol.toPrimitive").also { wellknownSymbols["@@toPrimitive"] = it }
            `@@toStringTag` = JSSymbol("Symbol.toStringTag").also { wellknownSymbols["@@toStringTag"] = it }
            `@@unscopables` = JSSymbol("Symbol.unscopables").also { wellknownSymbols["@@unscopables"] = it }
        }
    }
}
