package me.mattco.jsthing.ast.identifiers

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.identifiers.IdentifierNode.Companion.parseIdentifier
import me.mattco.jsthing.ast.semantics.SSAssignmentTargetType
import me.mattco.jsthing.ast.semantics.SSStringValue
import me.mattco.jsthing.lexer.TokenType


class IdentifierReferenceNode(val identifierName: String) : ASTNode(), SSStringValue, SSAssignmentTargetType {
    override fun stringValue() = identifierName

    override fun assignmentTargetType(): SSAssignmentTargetType.AssignmentTargetType {
        // TODO: Strict mode check
        return SSAssignmentTargetType.AssignmentTargetType.Simple
    }

    companion object {
        fun ASTState.parseIdentifierReference(suffixes: Set<Suffix>): ASTResult<IdentifierReferenceNode> {
            forward(parseIdentifier(suffixes)) {
                IdentifierReferenceNode(it.identifierName)
            }?.also { return it }

            if (tokenType == TokenType.Yield && Suffix.Yield !in suffixes)
                return result(IdentifierReferenceNode("yield"))

            if (tokenType == TokenType.Await && Suffix.Await !in suffixes) {
                return if (goalSymbol == ASTState.GoalSymbol.Module) {
                    error("'await' is an invalid identifier in a module")
                } else result(IdentifierReferenceNode("await"))
            }

            return ASTResult.None
        }
    }
}
