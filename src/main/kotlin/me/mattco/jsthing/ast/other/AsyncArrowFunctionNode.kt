package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class AsyncArrowFunctionNode : ASTNode() {
    companion object {
        fun ASTState.parseAsyncArrowFunction(suffixes: Set<Suffix>): ASTResult<AsyncArrowFunctionNode> {
            TODO()
        }
    }
}
