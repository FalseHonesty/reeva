package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.identifiers.IdentifierReferenceNode.Companion.parseIdentifierReference
import me.mattco.jsthing.ast.literals.LiteralNode.Companion.parseLiteral
import me.mattco.jsthing.ast.literals.ThisNode
import me.mattco.jsthing.ast.literals.arrays.ArrayLiteralNode.Companion.parseArrayLiteral
import me.mattco.jsthing.ast.literals.objects.ObjectLiteralNode.Companion.parseObjectLiteral
import me.mattco.jsthing.lexer.TokenType

class PrimaryExpressionNode : ASTNode() {
    companion object {
        fun ASTState.parsePrimaryExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            if (tokenType == TokenType.This) {
                consume()
                return result(ThisNode)
            }

            forward(parseIdentifierReference(suffixes)) { it }?.also { return it }
            forward(parseLiteral()) { it }?.also { return it }
            forward(parseArrayLiteral(suffixes)) { it }?.also { return it }
            forward(parseObjectLiteral(suffixes)) { it }?.also { return it }

            TODO()
        }
    }
}
