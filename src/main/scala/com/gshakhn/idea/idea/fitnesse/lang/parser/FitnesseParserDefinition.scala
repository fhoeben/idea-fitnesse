package com.gshakhn.idea.idea.fitnesse.lang.parser

import com.gshakhn.idea.idea.fitnesse.decisiontable.{DecisionTable, DecisionOutput, DecisionInput}
import com.gshakhn.idea.idea.fitnesse.lang.lexer.{FitnesseLexer, FitnesseTokenType}
import com.gshakhn.idea.idea.fitnesse.lang.psi._
import com.gshakhn.idea.idea.fitnesse.querytable.{QueryOutput, QueryTable}
import com.gshakhn.idea.idea.fitnesse.scripttable.{ScriptRow, ScriptTable}
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.{ASTNode, ParserDefinition}
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.TokenSet

class FitnesseParserDefinition extends ParserDefinition {
  override def createLexer(project: Project) = new FitnesseLexer

  override def createParser(project: Project) = new FitnesseParser

  override def getFileNodeType = FitnesseElementType.FILE

  override def getWhitespaceTokens = TokenSet.create(FitnesseTokenType.WHITE_SPACE)

  override def getCommentTokens = TokenSet.EMPTY

  override def getStringLiteralElements = TokenSet.EMPTY

  override def createElement(astNode: ASTNode) = {
    astNode.getElementType match {
      case TableElementType.DECISION_TABLE => new DecisionTable(astNode)
      case TableElementType.QUERY_TABLE => new QueryTable(astNode)
      case TableElementType.SCRIPT_TABLE => new ScriptTable(astNode)
      case FitnesseElementType.ROW => if (astNode.getTreeParent.getElementType == TableElementType.SCRIPT_TABLE) new ScriptRow(astNode) else new Row(astNode)
      case FitnesseElementType.FIXTURE_CLASS => new FixtureClass(astNode)
      case FitnesseElementType.DECISION_INPUT => new DecisionInput(astNode)
      case FitnesseElementType.DECISION_OUTPUT => new DecisionOutput(astNode)
      case FitnesseElementType.QUERY_OUTPUT => new QueryOutput(astNode)
      case _:WikiLinkElementType => new WikiLink(astNode)
      case _ => new ASTWrapperPsiElement(astNode)
    }
  }

  override def createFile(fileViewProvider: FileViewProvider) = new FitnesseFile(fileViewProvider)

  override def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode) = SpaceRequirements.MAY
}
