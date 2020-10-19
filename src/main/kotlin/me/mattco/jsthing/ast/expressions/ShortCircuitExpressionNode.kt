package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.CoalesceExpressionNode.Companion.parseCoalesceExpression
import me.mattco.jsthing.ast.expressions.binary.LogicalORExpressionNode.Companion.parseLogicalORExpression

class ShortCircuitExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parseShortCircuitExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseLogicalORExpression(suffixes)) { it }?.also { return it }
            forward(parseCoalesceExpression(suffixes)) { it }?.also { return it }
            return ASTResult.None
        }
    }
}
