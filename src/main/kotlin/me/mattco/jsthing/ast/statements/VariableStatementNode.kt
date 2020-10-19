package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.VariableDeclarationListNode.Companion.parseVariableDeclarationList
import me.mattco.jsthing.lexer.TokenType

class VariableStatementNode(val declarations: List<VariableDeclarationNode>) : ASTNode() {
    companion object {
        fun ASTState.parseVariableStatement(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            if (tokenType != TokenType.Var)
                return ASTResult.None

            saveState()
            consume()
            val list = parseVariableDeclarationList(suffixes + Suffix.In)
            handleNonResult(list) { return it }

            // The spec makes this semicolon mandatory, but I'm not sure how that's possible
            // considering I can very easily declare variables without a semicolon...
            if (tokenType == TokenType.Semicolon)
                consume()

            return result(VariableStatementNode(list.result.declarations))
        }
    }
}
