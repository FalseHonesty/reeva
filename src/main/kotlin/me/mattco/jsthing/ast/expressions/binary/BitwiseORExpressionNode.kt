package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.BitwiseXORExpressionNode.Companion.parseBitwiseXORExpression
import me.mattco.jsthing.lexer.TokenType

class BitwiseORExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseBitwiseORExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseBitwiseXORExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.Pipe) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseBitwiseORExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(BitwiseORExpressionNode(lhs.result, rhs.result))
        }
    }
}
