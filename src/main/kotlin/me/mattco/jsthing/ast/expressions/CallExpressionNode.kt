package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class CallExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parseCallExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            TODO()
        }
    }
}
