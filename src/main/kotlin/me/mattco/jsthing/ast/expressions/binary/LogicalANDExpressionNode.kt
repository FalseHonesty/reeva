package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.BitwiseORExpressionNode.Companion.parseBitwiseORExpression
import me.mattco.jsthing.lexer.TokenType

class LogicalANDExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseLogicalANDExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseBitwiseORExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.DoublePipe) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseLogicalANDExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(LogicalANDExpressionNode(lhs.result, rhs.result))
        }
    }
}
