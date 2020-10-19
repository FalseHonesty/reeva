package me.mattco.jsthing.ast

open class ASTResult<out T>(private val _result: T?, private val _error: ASTError?) {
    companion object None : ASTResult<Nothing>(null, null)

    val hasResult = _result != null
    val hasError = _error != null
    val isNone by lazy { this is None }

    val result by lazy {
        if (!hasResult)
            throw IllegalStateException("Illegal attempt to get result from an errored ASTResult")
        _result!!
    }

    val error by lazy {
        if (!hasError)
            throw IllegalStateException("Illegal attempt to get error from a non-errored ASTResult")
        _error!!
    }
}
