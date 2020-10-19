package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AssignmentExpressionNode.Companion.parseAssignmentExpression
import me.mattco.jsthing.lexer.TokenType

// Note that this name deviates from the spec because I think this is
// a much better name. It is not clear from the name "ExpressionNode"
// that the inner expression are separated by comma operators, and only
// the last one should be returned. However, its parse method is still
// called parseExpression to make it easier to use in other places
// throughout the AST
class CommaExpressionNode(val expressions: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            val expressions = mutableListOf<ASTNode>()

            fun returnVal() = when (expressions.size) {
                0 -> ASTResult.None
                1 -> result(expressions[0])
                else -> result(CommaExpressionNode(expressions))
            }

            var expr = parseAssignmentExpression(suffixes)
            handleNonResult(expr) { return it }

            expressions.add(expr.result)

            while (tokenType == TokenType.Comma) {
                saveState()
                consume()
                expr = parseAssignmentExpression(suffixes)
                if (expr.hasError) {
                    discardState()
                    return expr
                }
                if (expr.isNone) {
                    loadState()
                    return returnVal()
                }
                expressions.add(expr.result)
            }

            return returnVal()
        }
    }
}
