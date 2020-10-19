package me.mattco.jsthing.ast

import me.mattco.jsthing.utils.stringBuilder

abstract class ASTNode {
    val name: String
        get() = this::class.java.simpleName

    open fun earlyError(): ASTError? = null

    open fun dump(indent: Int = 0): String {
        return makeIndent(indent) + name
    }

    fun StringBuilder.appendName() = append(name)

    enum class Suffix {
        Yield,
        Await,
        In,
        Return,
        Tagged,
    }

    companion object {
        const val INDENT = "  "

        fun makeIndent(indent: Int) = stringBuilder {
            repeat(indent) {
                append(INDENT)
            }
        }

        fun StringBuilder.appendIndent(indent: Int) = append(makeIndent(indent))
    }
}
