package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.StatementsListItemNode.Companion.parseStatementListItem

class StatementListNode(val statements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseStatementList(suffixes: Set<Suffix>): ASTResult<StatementListNode> {
            val statements = mutableListOf<ASTNode>()

            while (true) {
                val item = parseStatementListItem(suffixes)
                if (item.hasError)
                    return error(item.error)
                if (item.isNone)
                    break
                statements.add(item.result)
            }

            return result(StatementListNode(statements))
        }
    }
}
