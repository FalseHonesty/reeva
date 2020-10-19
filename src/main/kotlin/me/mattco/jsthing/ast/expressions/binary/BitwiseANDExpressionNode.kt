package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.EqualityExpressionNode.Companion.parseEqualityExpression
import me.mattco.jsthing.lexer.TokenType

class BitwiseANDExpressionNode(val lhs: ASTNode, val rhs: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseBitwiseANDExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseEqualityExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.Ampersand) {
                discardState()
                return lhs
            }
            consume()

            val rhs = parseBitwiseANDExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(BitwiseANDExpressionNode(lhs.result, rhs.result))
        }
    }
}
