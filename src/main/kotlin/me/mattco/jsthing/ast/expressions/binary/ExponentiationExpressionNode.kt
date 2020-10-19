package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.unary.UnaryExpressionNode.Companion.parseUnaryExpression
import me.mattco.jsthing.lexer.TokenType

class ExponentiationExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseExponentiationExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseUnaryExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.DoubleAsterisk) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseExponentiationExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(ExponentiationExpressionNode(lhs.result, rhs.result))
        }
    }
}
