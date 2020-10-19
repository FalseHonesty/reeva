package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.MemberExpressionNode.Companion.parseMemberExpression
import me.mattco.jsthing.lexer.TokenType

class NewExpressionNode(val target: ASTNode) : ASTNode() {
    companion object {
        fun ASTState.parseNewExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val member = parseMemberExpression(suffixes)
            handleNonResult(member) { return it }

            if (tokenType != TokenType.New) {
                discardState()
                return member
            }

            consume()

            val newExpr = parseNewExpression(suffixes)
            handleNonResult(newExpr) { return it }

            discardState()
            return result(NewExpressionNode(newExpr.result))
        }
    }
}
