package me.mattco.reeva.runtime.builtins.promises

import me.mattco.reeva.core.Realm
import me.mattco.reeva.core.ThrowException
import me.mattco.reeva.runtime.JSValue
import me.mattco.reeva.runtime.Operations
import me.mattco.reeva.runtime.errors.JSTypeErrorObject
import me.mattco.reeva.runtime.functions.JSNativeFunction
import me.mattco.reeva.runtime.objects.JSObject
import me.mattco.reeva.runtime.primitives.JSUndefined
import me.mattco.reeva.utils.JSArguments
import me.mattco.reeva.utils.argument

class JSResolveFunction private constructor(
    val promise: JSObject,
    var alreadyResolved: Operations.Wrapper<Boolean>,
    realm: Realm
) : JSNativeFunction(realm, "", 1) {
    override fun evaluate(arguments: JSArguments): JSValue {
        if (newTarget != JSUndefined)
            TODO("Not yet implemented")
        if (alreadyResolved.value)
            return JSUndefined
        alreadyResolved.value = true

        val resolution = arguments.argument(0)
        if (resolution.sameValue(promise)) {
            val selfResolutionError = JSTypeErrorObject.create(realm, "TODO: message (promise self resolution)")
            return Operations.rejectPromise(promise, selfResolutionError)
        }

        if (resolution !is JSObject)
            return Operations.fulfillPromise(promise, resolution)

        val thenAction = try {
            resolution.get("then")
        } catch (e: ThrowException) {
            return Operations.rejectPromise(promise, e.value)
        }

        if (!Operations.isCallable(thenAction))
            return Operations.fulfillPromise(promise, resolution)

        val job = Operations.newPromiseResolveThenableJob(promise, resolution, thenAction)
        Operations.hostEnqueuePromiseJob(job.job, job.realm)

        return JSUndefined
    }

    companion object {
        fun create(
            promise: JSObject,
            alreadyResolved: Operations.Wrapper<Boolean>,
            realm: Realm
        ) = JSResolveFunction(promise, alreadyResolved, realm).initialize()
    }
}
