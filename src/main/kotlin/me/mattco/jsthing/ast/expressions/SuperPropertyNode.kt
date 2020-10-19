package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.CommaExpressionNode.Companion.parseExpression
import me.mattco.jsthing.ast.identifiers.IdentifierNode
import me.mattco.jsthing.lexer.TokenType

class SuperPropertyNode(val target: ASTNode, val computed: Boolean) : ASTNode() {
    companion object {
        fun ASTState.parseSuperProperty(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            if (tokenType != TokenType.Super)
                return ASTResult.None

            saveState()
            consume()
            if (tokenType == TokenType.Period) {
                consume()
                if (tokenType != TokenType.Identifier) {
                    loadState()
                    return ASTResult.None
                }
                discardState()
                return result(SuperPropertyNode(IdentifierNode(consume().value), computed = false))
            } else if (tokenType == TokenType.OpenBracket) {
                consume()
                val expr = parseExpression(suffixes)
                handleNonResult(expr) { return it }
                discardState()
                return result(SuperPropertyNode(expr.result, computed = true))
            }

            loadState()
            return ASTResult.None
        }
    }
}
