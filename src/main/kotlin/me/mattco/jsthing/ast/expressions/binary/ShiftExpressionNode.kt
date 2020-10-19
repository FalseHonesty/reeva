package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.AdditiveExpressionNode.Companion.parseAdditiveExpression
import me.mattco.jsthing.ast.expressions.binary.RelationalExpressionNode.Companion.parseRelationalExpression
import me.mattco.jsthing.lexer.TokenType

class ShiftExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        ShiftLeft,
        ShiftRight,
        UnsignedShiftRight
    }

    companion object {
        fun ASTState.parseShiftExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseAdditiveExpression(suffixes)
            if (!lhs.hasResult) {
                loadState()
                return lhs
            }

            val op = when (tokenType) {
                TokenType.ShiftLeft -> Operator.ShiftLeft
                TokenType.ShiftRight -> Operator.ShiftRight
                TokenType.UnsignedShiftRight -> Operator.UnsignedShiftRight
                else -> {
                    discardState()
                    return lhs
                }
            }

            consume()

            val rhs = parseShiftExpression(suffixes)
            if (!rhs.hasResult) {
                loadState()
                return rhs
            }

            return result(ShiftExpressionNode(lhs.result, rhs.result, op))
        }
    }
}
