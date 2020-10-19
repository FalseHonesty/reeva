package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.BitwiseORExpressionNode.Companion.parseBitwiseORExpression
import me.mattco.jsthing.ast.expressions.binary.CoalesceExpressionHeadNode.Companion.parseCoalesceExpressionHead
import me.mattco.jsthing.lexer.TokenType

class CoalesceExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseCoalesceExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseCoalesceExpressionHead(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.DoubleQuestion) {
                loadState()
                return ASTResult.None
            }

            consume()

            val rhs = parseBitwiseORExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(CoalesceExpressionNode(lhs.result, rhs.result))
        }
    }
}
