package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.StatementListNode.Companion.parseStatementList
import me.mattco.jsthing.lexer.TokenType

class BlockNode(val statements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseBlock(suffixes: Set<Suffix>): ASTResult<BlockNode> {
            if (tokenType != TokenType.OpenCurly)
                return ASTResult.None

            saveState()
            consume()
            val statementList = parseStatementList(suffixes)
            if (statementList.hasError)
                return error(statementList.error)

            if (tokenType != TokenType.CloseCurly) {
                loadState()
                return ASTResult.None
            }

            consume()
            discardState()
            return if (statementList.hasResult) {
                result(BlockNode(statementList.result.statements))
            } else {
                result(BlockNode(emptyList()))
            }
        }
    }
}
