package me.mattco.jsthing.ast.expressions.unary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AwaitExpressionNode.Companion.parseAwaitExpression
import me.mattco.jsthing.ast.expressions.unary.UpdateExpressionNode.Companion.parseUpdateExpression
import me.mattco.jsthing.lexer.TokenType

class UnaryExpressionNode(val node: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        Delete,
        Void,
        Typeof,
        Plus,
        Minus,
        BitwiseNot,
        Not
    }

    companion object {
        fun ASTState.parseUnaryExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseUpdateExpression(suffixes)) { it }?.let { return it }

            val op = when (tokenType) {
                TokenType.Delete -> Operator.Delete
                TokenType.Void -> Operator.Void
                TokenType.Typeof -> Operator.Typeof
                TokenType.Plus -> Operator.Plus
                TokenType.Minus -> Operator.Minus
                TokenType.Tilde -> Operator.BitwiseNot
                TokenType.Exclamation -> Operator.Not
                else -> return parseAwaitExpression(suffixes + Suffix.Await)
            }

            saveState()
            consume()

            val node = parseUnaryExpression(suffixes)
            handleNonResult(node) { return it }

            discardState()
            return result(UnaryExpressionNode(node.result, op))
        }
    }
}
