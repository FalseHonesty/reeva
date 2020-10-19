package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.LogicalANDExpressionNode.Companion.parseLogicalANDExpression
import me.mattco.jsthing.lexer.TokenType

class LogicalORExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseLogicalORExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseLogicalANDExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.DoublePipe) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseLogicalORExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(LogicalORExpressionNode(lhs.result, rhs.result))
        }
    }
}
