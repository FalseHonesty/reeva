package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class ArgumentsNode : ASTNode() {
    companion object {
        fun ASTState.parseArguments(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            TODO()
        }
    }
}
