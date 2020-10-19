package me.mattco.jsthing.ast.identifiers

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.identifiers.IdentifierNode.Companion.parseIdentifier
import me.mattco.jsthing.ast.semantics.SSStringValue
import me.mattco.jsthing.lexer.TokenType

class LabelIdentifierNode(val identifierName: String) : ASTNode(), SSStringValue {
    override fun stringValue() = identifierName

    companion object {
        fun ASTState.parseLabelIdentifier(suffixes: Set<Suffix>): ASTResult<LabelIdentifierNode> {
            forward(parseIdentifier(suffixes)) {
                LabelIdentifierNode(it.identifierName)
            }?.let { return it }

            if (tokenType == TokenType.Yield && Suffix.Yield !in suffixes)
                return result(LabelIdentifierNode("yield"))

            if (tokenType == TokenType.Await && Suffix.Await !in suffixes) {
                return if (goalSymbol == ASTState.GoalSymbol.Module) {
                    error("'await' is an invalid identifier in a module")
                } else result(LabelIdentifierNode("await"))
            }

            return ASTResult.None
        }
    }
}
