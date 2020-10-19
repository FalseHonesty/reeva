package me.mattco.jsthing.ast.identifiers

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.identifiers.IdentifierNode.Companion.parseIdentifier
import me.mattco.jsthing.ast.semantics.SSBoundNames
import me.mattco.jsthing.ast.semantics.SSStringValue
import me.mattco.jsthing.lexer.TokenType

class BindingIdentifierNode(val identifierName: String) : ASTNode(), SSStringValue, SSBoundNames {
    override fun stringValue() = identifierName

    override fun boundNames() = listOf(identifierName)

    companion object {
        fun ASTState.parseBindingIdentifier(suffixes: Set<Suffix>): ASTResult<BindingIdentifierNode> {
            forward(parseIdentifier(suffixes)) {
                BindingIdentifierNode(it.identifierName)
            }?.also { return it }

            if (tokenType == TokenType.Yield) {
                return if (Suffix.Yield in suffixes) {
                    expected("identifier", "'yield'")
                } else result(BindingIdentifierNode("yield"))
            }

            if (tokenType == TokenType.Await) {
                return when {
                    goalSymbol == ASTState.GoalSymbol.Module -> error("'await' is an invalid identifier in a module")
                    Suffix.Await in suffixes -> expected("identifier", "'await'")
                    else -> result(BindingIdentifierNode("await"))
                }
            }

            return ASTResult.None
        }
    }
}
