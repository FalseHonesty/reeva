package me.mattco.reeva.lexer

import me.mattco.reeva.utils.hexValue
import me.mattco.reeva.utils.isHexDigit

data class Token(
    val type: TokenType,
    val trivia: String,
    val value: String,
    val triviaStart: SourceLocation,
    val valueStart: SourceLocation
) {
    val string = trivia + value

    val isIdentifierName = type == TokenType.Identifier
        || type == TokenType.Await
        || type == TokenType.BooleanLiteral
        || type == TokenType.Break
        || type == TokenType.Case
        || type == TokenType.Catch
        || type == TokenType.Class
        || type == TokenType.Const
        || type == TokenType.Continue
        || type == TokenType.Default
        || type == TokenType.Delete
        || type == TokenType.Do
        || type == TokenType.Else
        || type == TokenType.Enum
        || type == TokenType.Export
        || type == TokenType.Extends
        || type == TokenType.Finally
        || type == TokenType.For
        || type == TokenType.Function
        || type == TokenType.If
        || type == TokenType.Import
        || type == TokenType.In
        || type == TokenType.Instanceof
        || type == TokenType.Interface
        || type == TokenType.Let
        || type == TokenType.New
        || type == TokenType.NullLiteral
        || type == TokenType.Of
        || type == TokenType.Return
        || type == TokenType.Super
        || type == TokenType.Switch
        || type == TokenType.This
        || type == TokenType.Throw
        || type == TokenType.Try
        || type == TokenType.Typeof
        || type == TokenType.Var
        || type == TokenType.Void
        || type == TokenType.While
        || type == TokenType.Yield

    fun doubleValue(): Double {
        if (type != TokenType.NumericLiteral)
            throw IllegalStateException("doubleValue called on non-NumericLiteral")

        if (value[0] == '0' && value.length >= 2) {
            if (value[1] == 'x' || value[1] == 'X')
                return value.substring(2).toLong(16).toDouble()
            if (value[1] == 'o' || value[1] == 'O')
                return value.substring(2).toLong(8).toDouble()
            if (value[1] == 'b' || value[1] == 'B')
                return value.substring(2).toLong(2).toDouble()
            if (value[1].isDigit()) {
                // TODO: Syntax error in strict mode
                return value.substring(1).toLong(8).toDouble()
            }
        }
        return value.filterNot { it == '_' }.toDouble()
    }

    fun booleanValue(): Boolean {
        if (type != TokenType.BooleanLiteral)
            throw IllegalStateException("booleanValue called on non-BooleanLiteral")
        return value == "true"
    }

    fun stringValue(): String {
        return if (type == TokenType.TemplateLiteralString)
            parseContent(0, value.length)
        else parseContent(1, value.length - 1)
    }

    fun identifierValue(): String {
        return parseContent(0, value.length)
    }

    private fun parseContent(start: Int, end: Int): String {
        return StringBuilder().apply {
            var i = start
            while (i < end) {
                if (value[i] == '\\' && i + 1 < end) {
                    i++
                    when (value[i]) {
                        'n' -> append('\n')
                        't' -> append('\t')
                        'r' -> append('\r')
                        'b' -> append('\b')
                        'f' -> append('\u000c')
                        'v' -> append('\u000b')
                        '0' -> append('\u0000')
                        '\\' -> append('\\')
                        '"' -> append('"')
                        '\'' -> append('\'')
                        'x' -> {
                            if (i + 2 >= value.length - 1)
                                TODO()

                            val first = value[++i]
                            val second = value[++i]

                            if (!first.isHexDigit() || !second.isHexDigit())
                                TODO()

                            appendCodePoint(first.hexValue() * 16 + second.hexValue())
                        }
                        'u' -> {
                            if (i + 1 >= value.length - 1)
                                TODO()

                            val firstCh = value[++i]
                            if (firstCh == '{') {
                                var codePoint = 0
                                i++

                                val lastIndex = i + 5
                                while (i <= lastIndex) {
                                    if (i > value.lastIndex)
                                        TODO()
                                    val ch = value[i]
                                    if (ch == '}') {
                                        if (value[i - 1] == '}')
                                            TODO()
                                        appendCodePoint(codePoint)
                                        break
                                    }
                                    codePoint = (codePoint shl 4) or ch.hexValue()
                                    i++

                                    if (i < lastIndex)
                                        appendCodePoint(codePoint)
                                }

                            } else {
                                if (i + 3 > value.lastIndex)
                                    TODO()
                                if (!firstCh.isHexDigit())
                                    TODO()

                                var codePoint = firstCh.hexValue()

                                for (j in 0..2) {
                                    val ch = value[++i]
                                    if (!ch.isHexDigit())
                                        TODO()
                                    codePoint = (codePoint shl 4) or ch.hexValue()
                                }

                                appendCodePoint(codePoint)
                            }
                        }
                        else -> {
                            if (type == TokenType.TemplateLiteralString && value[i] == '`') {
                                append('`')
                            } else {
                                TODO()
                            }
                        }
                    }
                } else {
                    append(value[i])
                }
                i++
            }
        }.toString()
    }
}
