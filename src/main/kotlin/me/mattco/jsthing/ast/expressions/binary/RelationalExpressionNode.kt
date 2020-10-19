package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.ShiftExpressionNode.Companion.parseShiftExpression
import me.mattco.jsthing.lexer.TokenType

class RelationalExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        LessThan,
        GreaterThan,
        LessThanEquals,
        GreaterThanEquals,
        Instanceof,
        In,
    }

    companion object {
        fun ASTState.parseRelationalExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseShiftExpression(suffixes)
            handleNonResult(lhs) { return it }

            val op = when (tokenType) {
                TokenType.LessThan -> Operator.LessThan
                TokenType.GreaterThan -> Operator.GreaterThan
                TokenType.LessThanEquals -> Operator.LessThanEquals
                TokenType.GreaterThanEquals -> Operator.GreaterThanEquals
                TokenType.Instanceof -> Operator.Instanceof
                else -> {
                    if (tokenType == TokenType.In && Suffix.In in suffixes) {
                        Operator.In
                    } else {
                        discardState()
                        return lhs
                    }
                }
            }
            consume()

            val rhs = parseRelationalExpression(suffixes)
            handleNonResult(rhs) { return it }

            return result(RelationalExpressionNode(lhs.result, rhs.result, op))
        }
    }
}
