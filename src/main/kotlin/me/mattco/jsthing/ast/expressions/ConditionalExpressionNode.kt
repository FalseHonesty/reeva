package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AssignmentExpressionNode.Companion.parseAssignmentExpression
import me.mattco.jsthing.ast.expressions.ShortCircuitExpressionNode.Companion.parseShortCircuitExpression
import me.mattco.jsthing.lexer.TokenType

class ConditionalExpressionNode(val predicate: ASTNode, val ifTrue: ASTNode, val ifFalse: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseConditionExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val lhs = parseShortCircuitExpression(suffixes)
            handleNonResult(lhs) { return it }

            if (tokenType != TokenType.Question) {
                loadState()
                return lhs
            }
            consume()

            val middle = parseAssignmentExpression(suffixes + Suffix.In)
            handleNonResult(middle) { return it }

            if (tokenType != TokenType.Colon) {
                discardState()
                return ASTResult.None
            }
            consume()

            val rhs = parseAssignmentExpression(suffixes)
            handleNonResult(rhs) { return it }

            discardState()
            return result(ConditionalExpressionNode(lhs.result, middle.result, rhs.result))
        }
    }
}
