package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.CallExpressionNode.Companion.parseCallExpression
import me.mattco.jsthing.ast.expressions.NewExpressionNode.Companion.parseNewExpression
import me.mattco.jsthing.ast.expressions.OptionalExpressionNode.Companion.parseOptionalExpression

class LeftHandSideExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parseLeftHandSideExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseNewExpression(suffixes)) { it }?.also { return it }
            forward(parseCallExpression(suffixes)) { it }?.also { return it }
            forward(parseOptionalExpression(suffixes)) { it }?.also { return it }
            return ASTResult.None
        }
    }
}
