package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.lexer.TokenType

class MetaPropertyNode : ASTNode() {
    companion object {
        fun ASTState.parseMetaProperty(): ASTResult<ASTNode> {
            saveState()

            if (consume().type == TokenType.New) {
                if (consume().type == TokenType.Period) {
                    val last = consume()
                    if (last.type == TokenType.Identifier && last.value == "target")
                        return result(NewTargetNode)
                }
            }

            loadState()
            saveState()

            if (consume().type == TokenType.Import) {
                if (consume().type == TokenType.Period) {
                    val last = consume()
                    if (last.type == TokenType.Identifier && last.value == "meta")
                        return result(ImportMetaNode)
                }
            }

            loadState()
            return ASTResult.None
        }
    }
}
