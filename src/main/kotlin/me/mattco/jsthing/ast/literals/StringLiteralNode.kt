package me.mattco.jsthing.ast.literals

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.lexer.TokenType

class StringLiteralNode(val string: String) : ASTNode() {
    companion object {
        fun ASTState.parseStringLiteral(suffixes: Set<Suffix>): ASTResult<StringLiteralNode> {
            if (tokenType == TokenType.UnterminatedStringLiteral)
                return error("Unterminated string literal")
            if (tokenType != TokenType.StringLiteral)
                return ASTResult.None
            return result(StringLiteralNode(consume().asString()))
        }
    }
}
