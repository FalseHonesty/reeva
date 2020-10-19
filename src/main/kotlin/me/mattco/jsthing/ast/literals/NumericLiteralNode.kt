package me.mattco.jsthing.ast.literals

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.lexer.TokenType

class NumericLiteralNode(val number: Double) : ASTNode() {
    companion object {
        fun ASTState.parseNumericLiteral(): ASTResult<NumericLiteralNode> {
            // TODO: More complete lexing/parsing
            if (tokenType == TokenType.NumericLiteral)
                return result(NumericLiteralNode(consume().asDouble()))
            return ASTResult.None
        }
    }
}
