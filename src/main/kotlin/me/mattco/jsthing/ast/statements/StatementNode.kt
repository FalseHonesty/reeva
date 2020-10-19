package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.BlockStatementNode.Companion.parseBlockStatement
import me.mattco.jsthing.ast.statements.EmptyStatementNode.parseEmptyStatement
import me.mattco.jsthing.ast.statements.ExpressionStatementNode.Companion.parseExpressionStatement
import me.mattco.jsthing.ast.statements.VariableStatementNode.Companion.parseVariableStatement

class StatementNode : ASTNode() {
    companion object {
        fun ASTState.parseStatement(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseBlockStatement(suffixes)) { it }?.also { return it }
            forward(parseVariableStatement(suffixes - Suffix.Return)) { it }?.also { return it }
            forward(parseEmptyStatement()) { it }?.also { return it }
            forward(parseExpressionStatement(suffixes - Suffix.Return)) { it }?.also { return it }
            return ASTResult.None
        }
    }
}
