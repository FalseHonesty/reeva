package me.mattco.jsthing.ast.literals.arrays

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.AssignmentExpressionNode.Companion.parseAssignmentExpression
import me.mattco.jsthing.ast.other.SpreadElementNode.Companion.parseSpreadElement
import me.mattco.jsthing.lexer.TokenType

class ElementListNode(val elements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseElementList(suffixes: Set<Suffix>): ASTResult<ElementListNode> {
            val elements = mutableListOf<ASTNode>()

            while (true) {
                while (!isDone && tokenType == TokenType.Comma) {
                    consume()
                    elements.add(ElisionNode)
                }

                if (isDone)
                    return error("Unterminated array literal")

                val assignmentResult = parseAssignmentExpression(suffixes + Suffix.In)
                if (assignmentResult.hasError)
                    return error(assignmentResult.error)
                if (assignmentResult.hasResult) {
                    elements.add(assignmentResult.result)
                    continue
                }

                val spreadResult = parseSpreadElement(suffixes)
                if (spreadResult.hasError)
                    return error(assignmentResult.error)
                if (spreadResult.hasResult) {
                    elements.add(spreadResult.result)
                    continue
                }

                if (isDone)
                    return error("Unterminated array literal")

                if (tokenType != TokenType.Comma)
                    break
            }

            return result(ElementListNode(elements))
        }
    }
}
