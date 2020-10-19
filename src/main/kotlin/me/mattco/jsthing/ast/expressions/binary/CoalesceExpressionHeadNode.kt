package me.mattco.jsthing.ast.expressions.binary

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.binary.BitwiseORExpressionNode.Companion.parseBitwiseORExpression

class CoalesceExpressionHeadNode : ASTNode() {
    companion object {
        fun ASTState.parseCoalesceExpressionHead(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseCoalesceExpressionHead(suffixes)) { it }?.also { return it }
            forward(parseBitwiseORExpression(suffixes)) { it }?.also { return it }
            return ASTResult.None
        }
    }
}
