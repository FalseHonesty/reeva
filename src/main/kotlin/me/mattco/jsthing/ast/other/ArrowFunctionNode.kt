package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class ArrowFunctionNode : ASTNode() {
    companion object {
        fun ASTState.parseArrowFunction(suffixes: Set<Suffix>): ASTResult<ArrowFunctionNode> {
            TODO()
        }
    }
}
