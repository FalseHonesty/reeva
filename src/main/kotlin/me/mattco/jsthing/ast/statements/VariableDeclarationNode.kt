package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.identifiers.BindingIdentifierNode
import me.mattco.jsthing.ast.identifiers.BindingIdentifierNode.Companion.parseBindingIdentifier
import me.mattco.jsthing.ast.other.InitializerNode.Companion.parseInitializer

class VariableDeclarationNode(val identifier: BindingIdentifierNode, val initializer: ASTNode?) : ASTNode() {
    companion object {
        fun ASTState.parseVariableDeclaration(suffixes: Set<Suffix>): ASTResult<VariableDeclarationNode> {
            // TODO: BindingPattern
            val identifier = parseBindingIdentifier(suffixes - Suffix.In)
            if (identifier.hasError)
                return error(identifier.error)
            if (identifier.isNone)
                return ASTResult.None

            val initializer = parseInitializer(suffixes)
            if (initializer.hasError)
                return error(initializer.error)
            if (initializer.isNone)
                return result(VariableDeclarationNode(identifier.result, null))
            return result(VariableDeclarationNode(identifier.result, initializer.result))
        }
    }
}
