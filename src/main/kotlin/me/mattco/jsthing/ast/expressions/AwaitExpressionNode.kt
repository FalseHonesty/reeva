package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.unary.UnaryExpressionNode.Companion.parseUnaryExpression
import me.mattco.jsthing.lexer.TokenType

class AwaitExpressionNode(val node: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseAwaitExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            if (Suffix.Await !in suffixes)
                return ASTResult.None

            if (tokenType != TokenType.Await)
                return ASTResult.None

            saveState()
            consume()
            val node = parseUnaryExpression(suffixes)
            handleNonResult(node) { return it }

            discardState()
            return result(AwaitExpressionNode(node.result))
        }
    }
}
