package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.BitwiseANDExpressionNode.Companion.parseBitwiseANDExpression
import me.mattco.jsthing.lexer.TokenType

class BitwiseXORExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseBitwiseXORExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseBitwiseANDExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.Caret) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseBitwiseXORExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(BitwiseXORExpressionNode(lhs.result, rhs.result))
        }
    }
}
