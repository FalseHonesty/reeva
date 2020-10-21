package me.mattco.renva.ast.expressions

import me.mattco.renva.ast.ASTNode.Companion.appendIndent
import me.mattco.renva.ast.ExpressionNode
import me.mattco.renva.ast.NodeBase
import me.mattco.renva.utils.stringBuilder

class UnaryExpressionNode(val node: ExpressionNode, val op: Operator) : NodeBase(listOf(node)), ExpressionNode {
    override fun dump(indent: Int) = stringBuilder {
        appendIndent(indent)
        appendName()
        append(" (operator=")
        append(op.name)
        append(")\n")
        append(node.dump(indent + 1))
    }

    enum class Operator {
        Delete,
        Void,
        Typeof,
        Plus,
        Minus,
        BitwiseNot,
        Not
    }
}

class UpdateExpressionNode(val target: ExpressionNode, val isIncrement: Boolean, val isPostfix: Boolean) : NodeBase(listOf(target)), ExpressionNode {
    override fun dump(indent: Int) = stringBuilder {
        appendIndent(indent)
        appendName()
        append(" (isIncrement=")
        append(isIncrement)
        append(", isPostfix=")
        append(isPostfix)
        append(")\n")
        append(target.dump(indent + 1))
    }
}