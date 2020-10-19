package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.statements.BlockNode.Companion.parseBlock

class BlockStatementNode(val statements: List<ASTNode>) : ASTNode() {
    companion object {
        fun ASTState.parseBlockStatement(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            val block = parseBlock(suffixes)
            handleNonResult(block) { return it }
            return result(BlockStatementNode(block.result.statements))
        }
    }
}
