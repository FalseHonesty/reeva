package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class OptionalExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parseOptionalExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            TODO()
        }
    }
}
