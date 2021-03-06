package me.mattco.reeva.ast

import me.mattco.reeva.ast.statements.StatementListNode

class ScriptNode(val statementList: StatementListNode) : NodeBase(listOf(statementList)) {
    override fun lexicallyDeclaredNames(): List<String> {
        return statementList.topLevelLexicallyDeclaredNames()
    }

    override fun lexicallyScopedDeclarations(): List<NodeBase> {
        return statementList.topLevelLexicallyScopedDeclarations()
    }

    override fun varDeclaredNames(): List<String> {
        return statementList.topLevelVarDeclaredNames()
    }

    override fun varScopedDeclarations(): List<NodeBase> {
        return statementList.topLevelVarScopedDeclarations()
    }
}
