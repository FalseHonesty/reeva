package me.mattco.jsthing.ast.literals.arrays

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.literals.arrays.ElementListNode.Companion.parseElementList
import me.mattco.jsthing.lexer.TokenType

class ArrayLiteralNode(val elements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseArrayLiteral(suffixes: Set<Suffix>): ASTResult<ArrayLiteralNode> {
            if (tokenType != TokenType.OpenBracket)
                return ASTResult.None

            consume()
            val elementsList = parseElementList(suffixes)
            if (elementsList.hasError)
                return error(elementsList.error)

            if (tokenType != TokenType.CloseBracket)
                return error("Unterminated array literal")

            consume()

            // parseElementsList() result will never be None
            return result(ArrayLiteralNode(elementsList.result.elements))
        }
    }
}
