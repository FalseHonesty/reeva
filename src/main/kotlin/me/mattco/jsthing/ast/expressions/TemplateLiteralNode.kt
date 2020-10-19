package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class TemplateLiteralNode : ASTNode() {
    companion object {
        fun ASTState.parseTemplateLiteral(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            TODO()
        }
    }
}
