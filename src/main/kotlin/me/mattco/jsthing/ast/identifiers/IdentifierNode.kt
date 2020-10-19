package me.mattco.jsthing.ast.identifiers

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.semantics.SSStringValue
import me.mattco.jsthing.lexer.TokenType

class IdentifierNode(val identifierName: String) : ASTNode(), SSStringValue {
    override fun stringValue() = identifierName

    companion object {
        fun ASTState.parseIdentifier(suffixes: Set<Suffix>): ASTResult<IdentifierNode> {
            return when {
                tokenType != TokenType.Identifier -> expected("Identifier")
                ASTState.isReserved(token.value) -> unexpected("reserved word")
                else -> result(IdentifierNode(consume().value))
            }
        }
    }
}
