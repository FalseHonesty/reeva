package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

class DeclarationNode : ASTNode() {
    companion object {
        fun ASTState.parseDeclaration(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            TODO()
        }
    }
}
