package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.DeclarationNode.Companion.parseDeclaration
import me.mattco.jsthing.ast.statements.StatementNode.Companion.parseStatement

class StatementsListItemNode : ASTNode() {
    companion object {
        fun ASTState.parseStatementListItem(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseStatement(suffixes)) { it }?.also { return it }
            forward(parseDeclaration(suffixes)) { it }?.also { return it }
            return ASTResult.None
        }
    }
}
