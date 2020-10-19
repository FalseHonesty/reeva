package me.mattco.jsthing.parser

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.other.ScriptNode
import me.mattco.jsthing.lexer.Lexer
import me.mattco.jsthing.lexer.SourceLocation
import me.mattco.jsthing.lexer.Token
import me.mattco.jsthing.lexer.TokenType
import me.mattco.jsthing.parser.Terminal.*
import me.mattco.jsthing.utils.all
import kotlin.reflect.KProperty

class Parser(text: String) {
    
    private val syntaxErrors = mutableListOf<SyntaxError>()
    private lateinit var goalSymbol: GoalSymbol

    private var state = State(0)
    private val stateStack = mutableListOf(state)

    private var cursor: Int
        get() = state.cursor
        set(value) {
            state.cursor = value
        }

    private val char: Char
        get() = text[cursor]

    private val isDone: Boolean
        get() = cursor >= text.size

    fun parseScript(): ScriptNode? {
        goalSymbol = GoalSymbol.Script
        TODO()
    }

    fun parseModule() {
        TODO()
    }

    private fun saveState() {
        stateStack.add(state.copy())
    }

    private fun loadState() {
        state = stateStack.removeLast()
    }

    private fun discardState() {
        stateStack.removeLast()
    }

    private fun consume() = token.also { cursor++ }

    private fun consume(type: TokenType): Token {
        if (type != tokenType)
            syntaxError("Unexpected token ${token.value}")
        return consume()
    }

    private fun syntaxError(message: String = "TODO") {
        syntaxErrors.add(SyntaxError(
            token.valueStart.lineNumber + 1,
            token.valueStart.columnNumber + 1,
            message
        ))
    }

    private fun has(n: Int) = cursor + n < tokens.size

    private fun peek(n: Int) = tokens[cursor + n]

    private fun matchSequence(vararg types: TokenType) = has(types.size) && types.mapIndexed { i, t -> peek(i).type == t }.all()

    private fun matchAny(vararg types: TokenType) = !isDone && tokenType in types

    private fun <T> expected(expected: String, found: String = token.value) {
        syntaxError("Expected: $expected, found: $found")
    }

    private fun <T> unexpected(unexpected: String) {
        syntaxError("Unexpected $unexpected")
    }

    private data class State(var cursor: Int)

    private data class SyntaxError(
        val lineNumber: Int,
        val columnNumber: Int,
        val message: String
    )

    enum class GoalSymbol {
        Module,
        Script,
    }

    enum class Suffix {
        Yield,
        Await,
        In,
        Return,
        Tagged,
    }

    private fun parseScriptBody(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseStatementList(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseStatementListItem(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseStatement(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseDeclaration(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseMemberExpression(suffixes: Set<Suffix>): ASTNode? = parseNonterminals {
        case { nonterminal(PrimaryExpression) }

        case {
            nonterminal(MemberExpression)
            terminal(OpenBracket)
            nonterminal(Expression)
            terminal(CloseBracket)
        }

        case {
            nonterminal(MemberExpression)
            terminal(Period)
            nonterminal(IdentifierName)
        }

        case {
            nonterminal(MemberExpression)
            nonterminal(TemplateLiteral) {
                addSuffix(Suffix.Tagged)
//                removeSuffix(Suffix.In)
            }
        }

        case { nonterminal(SuperProperty) }
        case { nonterminal(MetaProperty) }

        case {
            terminal(New)
            nonterminal(MemberExpression)
            nonterminal(Arguments)
        }
    }

    private fun parsePrimaryExpression(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseExpression(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseSuperProperty(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseMetaProperty(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseArguments(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseIdentifierName(suffixes: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    private fun parseTemplateLiteral(suffixe: Set<Suffix>) = parseNonterminals {
        TODO()
    }

    // NONTERMINALS
    private val ScriptBody = Nonterminal(::parseScriptBody)
    private val StatementList = Nonterminal(::parseStatementList)
    private val StatementListItem = Nonterminal(::parseStatementListItem)
    private val Statement = Nonterminal(::parseStatement)
    private val Declaration = Nonterminal(::parseDeclaration)
    private val PrimaryExpression = Nonterminal(::parsePrimaryExpression)
    private val MemberExpression = Nonterminal(::parseMemberExpression)
    private val IdentifierName = Nonterminal(::parseIdentifierName)
    private val TemplateLiteral = Nonterminal(::parseTemplateLiteral)
    private val Expression = Nonterminal(::parseExpression)
    private val SuperProperty = Nonterminal(::parseSuperProperty)
    private val MetaProperty = Nonterminal(::parseMetaProperty)
    private val Arguments = Nonterminal(::parseArguments)

    private fun parseNonterminals(suffixes: Set<Suffix> = emptySet(), builder: NonterminalBuilder.() -> Unit): ASTNode? {
        val nonterminalBuilder = NonterminalBuilder(suffixes)
        nonterminalBuilder.apply(builder)
        return nonterminalBuilder.build()
    }

    private inner class NonterminalBuilder(private val suffixes: Set<Suffix>) {
        private val cases = mutableListOf<NonterminalCaseBuilder>()

        fun case(caseBuilder: NonterminalCaseBuilder.() -> Unit) {
            val builder = NonterminalCaseBuilder(suffixes)
            builder.apply(caseBuilder)
            cases.add(builder)
        }

        fun build(): ASTNode? {
            cases.forEach { case ->
                val match = case.match()
                if (match != null)
                    return match
            }

            return null
        }
    }

    private inner class NonterminalCaseBuilder(private val suffixes: Set<Suffix>) {
        val tokens = mutableListOf<Token>()

        fun nonterminal(nonterminal: Nonterminal, modifiers: (SuffixModifier.() -> Unit)? = null) {
            if (modifiers == null) {
                tokens.add(NonterminalToken(nonterminal))
                return
            }

            val suffixModifier = SuffixModifier()
            suffixModifier.apply(modifiers)
            tokens.add(NonterminalToken(nonterminal) { suffixModifier.getSuffixes(it) })
        }

        fun terminal(terminal: Terminal) {
            tokens.add(TerminalToken(terminal))
        }

        fun match(): ASTNode? {
            saveState()

            for (token in tokens) {
                if (token is TerminalToken) {

                }
            }
        }
    }

    private open class Token

    private class NonterminalToken(val nonterminal: Nonterminal, modifier: ((Set<Suffix>) -> Set<Suffix>)? = null) : Token()

    private class TerminalToken(val terminal: Terminal) : Token()

    private class Nonterminal(method: (Set<Suffix>) -> ASTNode?)

    private class SuffixModifier {
        private val toAdd = mutableSetOf<Suffix>()
        private val toRemove = mutableSetOf<Suffix>()

        fun addSuffix(suffix: Suffix) {
            toAdd.add(suffix)
        }

        fun removeSuffix(suffix: Suffix) {
            toRemove.add(suffix)
        }

        fun getSuffixes(suffixes: Set<Suffix>) = suffixes + toAdd - toRemove
    }

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
