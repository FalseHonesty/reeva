package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.ExponentiationExpressionNode.Companion.parseExponentiationExpression
import me.mattco.jsthing.lexer.TokenType

class MultiplicativeExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        Multiply,
        Divide,
        Modulo,
    }

    companion object {
        fun ASTState.parseMultiplicativeExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseExponentiationExpression(suffixes)
            handleNonResult(lhs) { return it }

            val op = when (tokenType) {
                TokenType.Asterisk -> Operator.Multiply
                TokenType.Slash -> Operator.Divide
                TokenType.Percent -> Operator.Modulo
                else -> {
                    discardState()
                    return lhs
                }
            }
            consume()

            val rhs = parseMultiplicativeExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(MultiplicativeExpressionNode(lhs.result, rhs.result, op))
        }
    }
}
