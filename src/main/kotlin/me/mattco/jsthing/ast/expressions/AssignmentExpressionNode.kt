package me.mattco.jsthing.ast.expressions

import me.mattco.jsthing.ast.ASTNode
import me.mattco.jsthing.ast.ASTResult
import me.mattco.jsthing.ast.ASTState
import me.mattco.jsthing.ast.expressions.ConditionalExpressionNode.Companion.parseConditionExpression
import me.mattco.jsthing.ast.expressions.LeftHandSideExpressionNode.Companion.parseLeftHandSideExpression
import me.mattco.jsthing.ast.expressions.YieldExpressionNode.Companion.parseYieldExpression
import me.mattco.jsthing.ast.other.ArrowFunctionNode.Companion.parseArrowFunction
import me.mattco.jsthing.ast.other.AsyncArrowFunctionNode.Companion.parseAsyncArrowFunction
import me.mattco.jsthing.lexer.TokenType

class AssignmentExpressionNode(val lhs: ASTNode, val rhs: ASTNode, val op: Operator) : ASTNode() {
    enum class Operator {
        Equals,
        Multiply,
        Divide,
        Mod,
        Plus,
        Minus,
        ShiftLeft,
        ShiftRight,
        UnsignedShiftRight,
        BitwiseAnd,
        BitwiseOr,
        BitwiseXor,
        Power,
        And,
        Or,
        Nullish
    }

    companion object {
        fun ASTState.parseAssignmentExpression(suffixes: Set<Suffix>): ASTResult<ASTNode> {
            forward(parseConditionExpression(suffixes)) { it }?.let { return it }
            forward(parseYieldExpression(suffixes)) { it }?.let { return it }
            forward(parseArrowFunction(suffixes)) { it }?.let { return it }
            forward(parseAsyncArrowFunction(suffixes)) { it }?.let { return it }

            saveState()

            val lhs = parseLeftHandSideExpression(suffixes)
            if (!lhs.hasResult) {
                loadState()
                return lhs
            }

            val op = when (tokenType) {
                TokenType.Equals -> Operator.Equals
                TokenType.AsteriskEquals -> Operator.Multiply
                TokenType.SlashEquals -> Operator.Divide
                TokenType.PercentEquals -> Operator.Mod
                TokenType.PlusEquals -> Operator.Plus
                TokenType.MinusEquals -> Operator.Minus
                TokenType.ShiftLeftEquals -> Operator.ShiftLeft
                TokenType.ShiftRightEquals -> Operator.ShiftRight
                TokenType.UnsignedShiftRightEquals -> Operator.UnsignedShiftRight
                TokenType.AmpersandEquals -> Operator.BitwiseAnd
                TokenType.CaretEquals -> Operator.BitwiseXor
                TokenType.PipeEquals -> Operator.BitwiseOr
                TokenType.DoubleAsteriskEquals -> Operator.Power
                TokenType.DoubleAmpersandEquals -> Operator.And
                TokenType.DoublePipeEquals -> Operator.Or
                TokenType.DoubleQuestionEquals -> Operator.Nullish
                else -> {
                    loadState()
                    return ASTResult.None
                }
            }

            consume()

            val rhs = parseAssignmentExpression(suffixes)
            if (!rhs.hasResult) {
                loadState()
                return rhs
            }

            discardState()
            return result(AssignmentExpressionNode(lhs.result, rhs.result, op))
        }
    }
}
