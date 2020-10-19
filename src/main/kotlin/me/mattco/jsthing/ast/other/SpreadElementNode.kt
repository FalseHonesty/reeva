package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AssignmentExpressionNode

class SpreadElementNode(val expression: AssignmentExpressionNode) : ASTNode() {
    companion object {
        fun ASTState.parseSpreadElement(suffixes: Set<Suffix>): ASTResult<SpreadElementNode> {
            TODO()
        }
    }
}
