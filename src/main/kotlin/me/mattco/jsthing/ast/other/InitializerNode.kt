package me.mattco.jsthing.ast.other

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AssignmentExpressionNode.Companion.parseAssignmentExpression
import me.mattco.jsthing.lexer.TokenType

class InitializerNode(val node: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseInitializer(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            if (tokenType != TokenType.Equals)
                return ASTResult.None

            saveState()
            val expr = parseAssignmentExpression(suffixes)
            handleNonResult(expr) { return it }

            discardState()
            return result(InitializerNode(expr.result))
        }
    }
}
