package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.RelationalExpressionNode.Companion.parseRelationalExpression
import me.mattco.jsthing.lexer.TokenType

class EqualityExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        StrictEquality,
        StrictInequality,
        NonstrictEquality,
        NonstrictInequality,
    }

    companion object {
        fun ASTState.parseEqualityExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseRelationalExpression(suffixes)
            handleNonResult(lhs) { return it }

            val op = when (tokenType) {
                TokenType.DoubleEquals -> Operator.NonstrictEquality
                TokenType.ExclamationEquals -> Operator.NonstrictInequality
                TokenType.TripleEquals -> Operator.StrictEquality
                TokenType.ExclamationDoubleEquals -> Operator.StrictInequality
                else -> {
                    discardState()
                    return lhs
                }
            }
            consume()

            val rhs = parseRelationalExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(EqualityExpressionNode(lhs.result, rhs.result, op))
        }
    }
}
