package me.mattco.jsthing.ast.literals.objects

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.literals.objects.PropertyDefinitionNode.Companion.parsePropertyDefinition
import me.mattco.jsthing.lexer.TokenType

class PropertyDefinitionListNode(val properties: List<PropertyDefinitionNode>) : ASTNode() {
    companion object {
        fun ASTState.parsePropertyDefinitionList(suffixes: Set<Suffix>): ASTResult<PropertyDefinitionListNode> {
            val properties = mutableListOf<PropertyDefinitionNode>()

            fun returnVal() = if (properties.isEmpty()) {
                ASTResult.None
            } else result(PropertyDefinitionListNode(properties))

            var property = parsePropertyDefinition(suffixes)
            if (property.hasError)
                return error(property.error)
            if (property.isNone)
                return ASTResult.None

            properties.add(property.result)

            while (tokenType == TokenType.Comma) {
                saveState()
                consume()
                property = parsePropertyDefinition(suffixes)
                if (property.hasError) {
                    discardState()
                    return error(property.error)
                }
                if (property.isNone) {
                    loadState()
                    return returnVal()
                }
                properties.add(property.result)
                discardState()
            }

            return returnVal()
        }
    }
}
