package me.mattco.jsthing.ast.literals.objects

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState

// { test() {} }

class PropertyDefinitionNode(type: Type) : ASTNode() {
    enum class Type {
        Normal,
        // TODO: Object destructuring
        // Initialized,
        Method,
        Spread
    }
    companion object {
        fun ASTState.parsePropertyDefinition(suffixes: Set<Suffix>): ASTResult<PropertyDefinitionNode> {
            TODO()
        }
    }
}
