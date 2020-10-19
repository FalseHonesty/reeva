package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.lexer.TokenType

object EmptyStatementNode : ASTNode() {
    fun ASTState.parseEmptyStatement(): ASTResult<EmptyStatementNode> {
        if (tokenType == TokenType.Semicolon) {
            consume()
            return result(EmptyStatementNode)
        }
        return ASTResult.None
    }
}
