package me.mattco.jsthing.ast.literals.objects

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.literals.objects.PropertyDefinitionListNode.Companion.parsePropertyDefinitionList
import me.mattco.jsthing.lexer.TokenType

class ObjectLiteralNode(val properties: List<PropertyDefinitionNode>) : ASTNode() {
    companion object {
        fun ASTState.parseObjectLiteral(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            return ASTResult.None
//            if (tokenType != TokenType.OpenCurly)
//                return ASTResult.None
//
//            saveState()
//            consume()
//
//            if (tokenType == TokenType.CloseCurly) {
//                consume()
//                discardState()
//                return result(ObjectLiteralNode(emptyList()))
//            }
//
//            val propertyList = parsePropertyDefinitionList(suffixes)
//            if (propertyList.hasError) {
//                discardState()
//                return propertyList
//            }
//            if (propertyList.isNone) {
//                loadState()
//                return ASTResult.None
//            }
//
//            if (tokenType == TokenType.CloseCurly) {
//                consume()
//                discardState()
//                return result(ObjectLiteralNode(propertyList.result.properties))
//            } else if (tokenType == TokenType.Comma) {
//                consume()
//                if (tokenType == TokenType.CloseCurly) {
//                    consume()
//                    discardState()
//                    return result(ObjectLiteralNode(propertyList.result.properties))
//                }
//            }
//
//            loadState()
//            return ASTResult.None
        }
    }
}
