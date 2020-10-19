package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.StatementListNode.Companion.parseStatementList

class ScriptNode(val statements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseScript(): ASTResult<ScriptNode> {
            val statements = parseStatementList(emptySet())

            return when {
                statements.hasError -> error(statements.error)
                statements.isNone -> result(ScriptNode(emptyList()))
                else -> result(ScriptNode(statements.result.statements))
            }.let {
                if (!isDone) {
                    unexpected("token ${token.value}")
                } else it
            }
        }
    }
}
