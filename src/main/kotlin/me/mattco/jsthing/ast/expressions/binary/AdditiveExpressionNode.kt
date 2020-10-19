package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.MultiplicativeExpressionNode.Companion.parseMultiplicativeExpression
import me.mattco.jsthing.lexer.TokenType

class AdditiveExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val isSubtraction: Boolean) : ASTNode() {
    companion object {
        fun ASTState.parseAdditiveExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseMultiplicativeExpression(suffixes)
            handleNonResult(lhs) { return it }

            val isSubtraction = when (tokenType) {
                TokenType.Plus -> false
                TokenType.Minus -> true
                else -> {
                    discardState()
                    return lhs
                }
            }
            consume()

            val rhs = parseAdditiveExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(AdditiveExpressionNode(lhs.result, rhs.result, isSubtraction))
        }
    }
}
