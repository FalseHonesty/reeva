package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class YieldExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parseYieldExpression(suffixes: Set<Suffix>): ASTResult<YieldExpressionNode> {
            TODO()
        }
    }
}
