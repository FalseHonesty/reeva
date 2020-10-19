package me.mattco.jsthing.ast

import me.mattco.jsthing.lexer.SourceLocation
import me.mattco.jsthing.lexer.Token
import me.mattco.jsthing.lexer.TokenType

class ASTState(tokens: List<Token>) {
    private val tokens = tokens.toMutableList().also {
        it.add(Token(TokenType.Eof, "", "", SourceLocation(-1, -1), SourceLocation(-1, -1)))
    }

    private var state = State(0, GoalSymbol.Script)
    private val stateStack = mutableListOf(state)

    val goalSymbol: GoalSymbol
        get() = state.goalSymbol

    val cursor: Int
        get() = state.cursor

    val token: Token
        get() = tokens[cursor]

    val tokenType: TokenType
        get() = token.type

    val isDone: Boolean
        get() = tokenType == TokenType.Eof

    fun saveState() {
        stateStack.add(state.copy())
    }

    fun loadState() {
        state = stateStack.removeLast()
    }

    fun discardState() {
        stateStack.removeLast()
    }

    fun consume() = token.also { state.cursor++ }

    fun consume(type: TokenType): Token {
        if (type != tokenType)
            TODO()
        return consume()
    }

    fun has(n: Int) = cursor + n < tokens.size

    fun peek(n: Int) = tokens[cursor + n]

    fun matchAny(vararg types: TokenType) = tokenType in types

    fun <T> expected(expected: String, found: String = tokenType.name) =
        ASTResult<T>(null, ASTError("Expected: $expected, found: $found"))

    fun <T> unexpected(unexpected: String) =
        ASTResult<T>(null, ASTError("Unexpected $unexpected"))

    fun <T> result(value: T) = ASTResult(value, null)

    fun <T> pass() = ASTResult<T>(null, ASTError(""))

    fun <T> error(error: String) = error<T>(ASTError(error))
    fun <T> error(error: ASTError) = ASTResult<T>(null, error)

    inline fun <reified U, reified T> forward(target: ASTResult<U>, successMapper: (U) -> T): ASTResult<T>? {
        if (target.isNone)
            return null
        if (target.hasError)
            return error(target.error)
        return result(successMapper(target.result))
    }

    inline fun <T> handleNonResult(target: ASTResult<T>, block: (ASTResult<T>) -> Unit) {
        if (target.isNone) {
            loadState()
            block(ASTResult.None)
        } else if (target.hasError) {
            discardState()
            block(target)
        }
    }

    enum class GoalSymbol {
        Module,
        Script,
    }

    private data class State(var cursor: Int = 0, var goalSymbol: GoalSymbol)

    private data class SyntaxError(
        val lineNumber: Int,
        val columnNumber: Int,
        val message: String
    )

    companion object {
        private val reservedWords = listOf(
            "await",
            "break",
            "case",
            "catch",
            "class",
            "const",
            "continue",
            "debugger",
            "default",
            "delete",
            "do",
            "else",
            "enum",
            "export",
            "extends",
            "false",
            "finally",
            "for",
            "function",
            "if",
            "import",
            "in",
            "instanceof",
            "new",
            "null",
            "return",
            "super",
            "switch",
            "this",
            "throw",
            "true",
            "try",
            "typeof",
            "var",
            "void",
            "while",
            "with",
            "yield",
        )

        fun isReserved(identifier: String) = identifier in reservedWords
    }
}
