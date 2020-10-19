package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.VariableDeclarationNode.Companion.parseVariableDeclaration
import me.mattco.jsthing.lexer.TokenType

class VariableDeclarationListNode(val declarations: List<VariableDeclarationNode>) : ASTNode() {
    companion object {
        fun ASTState.parseVariableDeclarationList(suffixes: Set<Suffix>): ASTResult<VariableDeclarationListNode> {
            val declarations = mutableListOf<VariableDeclarationNode>()

            var declaration = parseVariableDeclaration(suffixes)
            if (declaration.hasError)
                return error(declaration.error)
            if (declaration.isNone)
                return ASTResult.None

            declarations.add(declaration.result)
            saveState()

            while (tokenType == TokenType.Comma) {
                consume()

                declaration = parseVariableDeclaration(suffixes)
                if (declaration.hasError) {
                    discardState()
                    return error(declaration.error)
                }
                if (declaration.isNone) {
                    loadState()
                    return result(VariableDeclarationListNode(declarations))
                }
                declarations.add(declaration.result)
            }

            discardState()
            return result(VariableDeclarationListNode(declarations))
        }
    }
}
