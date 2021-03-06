package me.mattco.reeva.lexer

import me.mattco.reeva.utils.allIndexed
import me.mattco.reeva.utils.isHexDigit

class Lexer(private val source: String) : Iterable<Token> {
    private var lineNum = 0
    private var columnNum = 0
    private var cursor = 0
    private var lastToken = Token(TokenType.Eof, "", "", SourceLocation(-1, -1), SourceLocation(-1, -1))
    private val templateStates = mutableListOf<TemplateState>()
    private var regexIsInCharClass = false

    override fun iterator() = object : Iterator<Token> {
        private val lexer = this@Lexer
        private var lastToken = lexer.nextToken()

        override fun hasNext() = lastToken.type != TokenType.Eof

        override fun next(): Token {
            val toReturn = lastToken
            lastToken = lexer.nextToken()
            return toReturn
        }
    }

    private val isDone: Boolean
        get() = cursor >= source.length

    private val char: Char
        get() = if (isDone) 0.toChar() else source[cursor]

    fun nextToken(): Token {
        val triviaStartCursor = cursor
        val triviaStartLocation = SourceLocation(lineNum, columnNum)
        val inTemplate = templateStates.isNotEmpty()

        if (!inTemplate || templateStates.last().inExpr) {
            // Consume whitespace and comments
            while (!isDone) {
                if (char.isWhitespace()) {
                    do {
                        consume()
                    } while (!isDone && char.isWhitespace())
                } else if (isCommentStart()) {
                    consume()
                    do {
                        consume()
                    } while (!isDone && char != '\n')
                } else if (match('/', '*')) {
                    consume()
                    do {
                        consume()
                    } while (!isDone && !match('*', '/'))

                    if (!isDone)
                        consume(2)
                } else break
            }
        }

        val valueStartCursor = cursor
        val valueStartLocation = SourceLocation(lineNum, columnNum)
        var tokenType = TokenType.Invalid

        if (isDone) {
            tokenType = TokenType.Eof
        } else if (lastToken.type == TokenType.RegexLiteral && !isDone && char.isLetter() && '\n' !in source.substring(triviaStartCursor, valueStartCursor)) {
            tokenType = TokenType.RegexFlags
            while (!isDone && char.isLetter())
                consume()
        } else if (char == '`') {
            consume()

            if (!inTemplate) {
                tokenType = TokenType.TemplateLiteralStart
                templateStates.add(TemplateState())
            } else {
                tokenType = if (templateStates.last().inExpr) {
                    templateStates.add(TemplateState())
                    TokenType.TemplateLiteralStart
                } else {
                    templateStates.removeLast()
                    TokenType.TemplateLiteralEnd
                }
            }
        } else if (inTemplate && templateStates.last().let { it.inExpr && it.openBracketCount == 0 } && char == '}') {
            consume()
            tokenType = TokenType.TemplateLiteralExprEnd
            templateStates.last().inExpr = false
        } else if (inTemplate && !templateStates.last().inExpr) {
            when {
                isDone -> {
                    tokenType = TokenType.UnterminatedTemplateLiteral
                    templateStates.removeLast()
                }
                match('$', '{') -> {
                    tokenType = TokenType.TemplateLiteralExprStart
                    consume(2)
                    templateStates.last().inExpr = true
                }
                else -> {
                    while (!match('$', '{') && char != '`' && !isDone) {
                        if (match('\\', '$') || match('\\', '`'))
                            consume()
                        consume()
                    }
                    tokenType = TokenType.TemplateLiteralString
                }
            }
        } else if (isIdentStart()) {
            do {
                consumeIdentChar()
            } while (isIdentMiddle())

            val value = source.substring(valueStartCursor, cursor)
            tokenType = TokenType.keywords.firstOrNull { it.meta == value } ?: TokenType.Identifier
        } else if (isNumberLiteralStart()) {
            tokenType = TokenType.NumericLiteral
            if (char == '0') {
                consume()
                when {
                    char == '.' -> {
                        consume()
                        while (char.isDigit())
                            consume()
                        if (char == 'e' || char == 'E')
                            consumeExponent()
                    }
                    char == 'e' || char == 'E' -> consumeExponent()
                    char == 'o' || char == 'O' -> {
                        consume()
                        while (char in '0'..'7')
                            consume()
                        if (char == 'n') {
                            consume()
                            tokenType = TokenType.BigIntLiteral
                        }
                    }
                    char == 'b' || char == 'B' -> {
                        consume()
                        while (char == '0' || char == '1')
                            consume()
                        if (char == 'n') {
                            consume()
                            tokenType = TokenType.BigIntLiteral
                        }
                    }
                    char == 'x' || char == 'X' -> {
                        consume()
                        while (char.isHexDigit())
                            consume()
                        if (char == 'n') {
                            consume()
                            tokenType = TokenType.BigIntLiteral
                        }
                    }
                    char == 'n' -> {
                        consume()
                        tokenType = TokenType.BigIntLiteral
                    }
                    char.isDigit() -> {
                        // Octal without 'O' prefix
                        // TODO: Prevent in strict mode
                        do {
                            consume()
                        } while (char in '0'..'7')
                    }
                }
            } else {
                while (char.isDigit() || char == '_')
                    consume()
                if (char == 'n') {
                    consume()
                    tokenType = TokenType.BigIntLiteral
                } else {
                    if (char == '.') {
                        consume()
                        while (char.isDigit() || char == '_')
                            consume()
                    }
                    if (char == 'e' || char == 'E')
                        consumeExponent()
                }
            }
        } else if (char == '"' || char == '\'') {
            val stopChar = char
            consume()
            while (char != stopChar && char != '\n' && !isDone) {
                if (char == '\\')
                    consume()
                consume()
            }
            if (char != stopChar) {
                tokenType == TokenType.UnterminatedStringLiteral
            } else {
                consume()
                tokenType = TokenType.StringLiteral
            }
        } else if (char == '/' && lastToken.type !in slashMeansDivision) {
            consume()
            tokenType = TokenType.RegexLiteral

            while (!isDone) {
                if (char == '[') {
                    regexIsInCharClass = true
                } else if (char == ']') {
                    regexIsInCharClass = false
                } else if (!regexIsInCharClass && char == '/') {
                    break
                }

                if (match('\\', '/') || match('\\', '[') || match('\\', '\\') || (regexIsInCharClass && match('\\', ']')))
                    consume()
                consume()
            }

            if (isDone) {
                tokenType = TokenType.UnterminatedRegexLiteral
            } else {
                consume()
            }
        } else {
            var matched = false

            // Only four length token
            if (match(">>>=")) {
                consume(4)
                tokenType = TokenType.UnsignedShiftRightEquals
                matched = true
            }

            if (!matched) {
                matched = TokenType.tripleCharTokens.firstOrNull {
                    it.meta?.let(::match) == true
                }?.also {
                    tokenType = it
                    consume(3)
                } != null
            }

            if (!matched) {
                matched = TokenType.doubleCharTokens.firstOrNull {
                    it.meta?.let(::match) == true
                }?.also {
                    tokenType = it
                    consume(2)
                } != null
            }

            if (!matched) {
                matched = TokenType.singleCharTokens.firstOrNull {
                    it.meta?.let(::match) == true
                }?.also {
                    tokenType = it
                    consume()
                } != null
            }

            if (!matched) {
                consume()
                tokenType = TokenType.Invalid
            }
        }

        if (templateStates.isNotEmpty() && templateStates.last().inExpr) {
            if (tokenType == TokenType.OpenCurly) {
                templateStates.last().openBracketCount++
            } else if (tokenType == TokenType.CloseCurly) {
                templateStates.last().openBracketCount--
            }
        }

        lastToken = Token(
            tokenType,
            source.substring(triviaStartCursor, valueStartCursor),
            source.substring(valueStartCursor, cursor),
            triviaStartLocation,
            valueStartLocation,
        )

        return lastToken
    }

    private fun isIdentStart() = char.isLetter() || char == '_' || char == '$' || match('\\', 'u')

    private fun consumeIdentChar() {
        if (match('\\', 'u')) {
            consume(2)
            if (match('{')) {
                consume()
                for (i in 0 until 5) {
                    if (!has(1))
                        TODO()
                    if (char == '}') {
                        if (i == 0)
                            TODO()
                        consume()
                        return
                    }
                    if (!char.isHexDigit())
                        TODO()
                    consume()
                }
            } else {
                for (i in 0 until 4) {
                    if (!char.isHexDigit())
                        TODO()
                    consume()
                }
            }
        } else {
            consume()
        }
    }

    private fun isIdentMiddle() = isIdentStart() || char.isDigit()

    private fun isCommentStart() = match('/', '/') || match('<', '!', '-', '-') || match('-', '-', '>')

    private fun isNumberLiteralStart() = char.isDigit() || (cursor + 1 < source.length && char == '.' && peek(1).isDigit())

    private fun peek(n: Int): Char {
        if (cursor + n >= source.length)
            throw IllegalArgumentException("Attempt to peek out of string bounds")

        return source[cursor + n]
    }

    private fun has(n: Int): Boolean = cursor + n < source.length

    private fun match(vararg ch: Char) = ch.allIndexed { i, char ->
        cursor + i < source.length && source[cursor + i] == char
    }

    private fun match(string: String) = match(*string.toCharArray())

    private fun consume(times: Int = 1) = repeat(times) {
        if (isDone)
            throw IllegalStateException("Attempt to consume character from exhausted Lexer")

        if (char == '\n') {
            lineNum++
            columnNum = 0
        } else {
            columnNum++
        }

        cursor++
    }

    private fun consumeExponent() {
        consume()
        if (char == '-' || char == '+')
            consume()
        while (char.isDigit())
            consume()
    }

    data class TemplateState(var inExpr: Boolean = false, var openBracketCount: Int = 0)

    companion object {
        private val slashMeansDivision = listOf(
            TokenType.BigIntLiteral,
            TokenType.BooleanLiteral,
            TokenType.CloseBracket,
            TokenType.CloseCurly,
            TokenType.CloseParen,
            TokenType.Identifier,
            TokenType.NullLiteral,
            TokenType.NumericLiteral,
            TokenType.RegexLiteral,
            TokenType.StringLiteral,
            TokenType.TemplateLiteralEnd,
            TokenType.This,
        )

        val lineTerminators = listOf('\n', '\u000d', '\u2028', '\u2029')
    }
}
