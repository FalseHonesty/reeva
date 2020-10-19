package me.mattco.jsthing.ast.expressions.unary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.LeftHandSideExpressionNode.Companion.parseLeftHandSideExpression
import me.mattco.jsthing.ast.expressions.unary.UnaryExpressionNode.Companion.parseUnaryExpression
import me.mattco.jsthing.lexer.TokenType

class UpdateExpressionNode(val target: ASTNode, val isIncrement: Boolean, val isPostfix: Boolean) : ASTNode() {
    companion object {
        fun ASTState.parseUpdateExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val node = parseLeftHandSideExpression(suffixes)
            if (node.hasError || isDone) {
                discardState()
                return node
            }

            if (node.hasResult) {
                if ('\n' in token.trivia) {
                    discardState()
                    return node
                }

                if (matchAny(TokenType.PlusPlus, TokenType.MinusMinus)) {
                    discardState()
                    return result(UpdateExpressionNode(node.result, consume().type == TokenType.PlusPlus, true))
                }
            }

            if (!matchAny(TokenType.PlusPlus, TokenType.MinusMinus)) {
                loadState()
                return ASTResult.None
            }

            val isIncrement = consume().type == TokenType.PlusPlus

            val node2 = parseUnaryExpression(suffixes)
            handleNonResult(node2) { return it }

            return result(UpdateExpressionNode(node2.result, isIncrement, isPostfix = false))
        }
    }
}
