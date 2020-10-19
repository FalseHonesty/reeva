package me.mattco.jsthing.ast.literals

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.literals.NumericLiteralNode.Companion.parseNumericLiteral
import me.mattco.jsthing.lexer.TokenType

class LiteralNode(val node: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseLiteral(): ASTResult<LiteralNode> {
            if (tokenType == TokenType.NullLiteral) {
                consume()
                return result(LiteralNode(NullNode))
            }

            if (tokenType == TokenType.True || tokenType == TokenType.False)
                return result(LiteralNode(BooleanNode(consume().type == TokenType.True)))

            forward(parseNumericLiteral(), ::LiteralNode)?.also { return it }
            return ASTResult.None
        }
    }
}
