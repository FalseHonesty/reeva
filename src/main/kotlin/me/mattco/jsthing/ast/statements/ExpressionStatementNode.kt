package me.mattco.jsthing.ast.statements

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.lexer.TokenType

class ExpressionStatementNode(val node: ASTNode): ASTNode() {
    companion object {
        fun ASTState.parseExpressionStatement(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            when (tokenType) {
                TokenType.OpenCurly,
                TokenType.Function,
                TokenType.Class -> return ASTResult.None
                TokenType.Async -> {
                    peek(1).also {
                        if (it.type == TokenType.Function && '\n' !in it.trivia)
                            return ASTResult.None
                    }
                }
                TokenType.Let -> {
                    if (peek(1).type == TokenType.OpenBracket)
                        return ASTResult.None
                }
                else -> {}
            }

            val expr = parseExpressionStatement(suffixes + Suffix.In)
            handleNonResult(expr) { return it }
            return result(ExpressionStatementNode(expr.result))
        }
    }
}
