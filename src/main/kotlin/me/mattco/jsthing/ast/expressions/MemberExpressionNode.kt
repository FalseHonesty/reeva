package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.CommaExpressionNode.Companion.parseExpression
import me.mattco.jsthing.ast.expressions.MetaPropertyNode.Companion.parseMetaProperty
import me.mattco.jsthing.ast.expressions.PrimaryExpressionNode.Companion.parsePrimaryExpression
import me.mattco.jsthing.ast.expressions.SuperPropertyNode.Companion.parseSuperProperty
import me.mattco.jsthing.ast.identifiers.IdentifierNode
import me.mattco.jsthing.ast.other.ArgumentsNode.Companion.parseArguments
import me.mattco.jsthing.lexer.TokenType

class MemberExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val type: Type) : ASTNode() {
    enum class Type {
        Computed,
        NonComputed,
        New,
        Tagged,
    }

    companion object {
        fun ASTState.parseMemberExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            saveState()

            val primaryExpr = parsePrimaryExpression(suffixes)
            if (primaryExpr.hasError) {
                discardState()
                return primaryExpr
            }

            if (primaryExpr.hasResult) {
                var memberExpr: MemberExpressionNode? = null

                while (true) {
                    saveState()

                    when (tokenType) {
                        TokenType.OpenBracket -> {
                            consume()
                            val expr = parseExpression(suffixes + Suffix.In)
                            if (expr.hasError) {
                                discardState()
                                return expr
                            }
                            if (expr.isNone || tokenType != TokenType.CloseBracket) {
                                loadState()
                                return primaryExpr
                            }
                            consume()
                            memberExpr = if (memberExpr == null) {
                                MemberExpressionNode(primaryExpr.result, expr.result, Type.Computed)
                            } else {
                                MemberExpressionNode(memberExpr, expr.result, Type.Computed)
                            }
                        }
                        TokenType.Period -> {
                            consume()
                            if (tokenType != TokenType.Identifier) {
                                loadState()
                                return primaryExpr
                            }
                            val identifier = IdentifierNode(consume().value)

                            memberExpr = if (memberExpr == null) {
                                MemberExpressionNode(primaryExpr.result, identifier, Type.NonComputed)
                            } else {
                                MemberExpressionNode(memberExpr, identifier, Type.NonComputed)
                            }
                        }
                        // TODO: Template Literals
                        else -> {
                            discardState()
                            break
                        }
                    }

                    discardState()
                }

                discardState()

                return if (memberExpr != null) {
                    result(memberExpr)
                } else {
                    primaryExpr
                }
            }

            forward(parseSuperProperty(suffixes)) { it }?.let { return it }
            forward(parseMetaProperty()) { it }?.let { return it }

            if (tokenType != TokenType.New)
                return ASTResult.None

            consume()
            val expr = parseMemberExpression(suffixes)
            handleNonResult(expr) { return it }

            val args = parseArguments(suffixes)
            handleNonResult(args) { return it }

            discardState()
            return result(MemberExpressionNode(expr.result, args.result, Type.New))
        }
    }
}
