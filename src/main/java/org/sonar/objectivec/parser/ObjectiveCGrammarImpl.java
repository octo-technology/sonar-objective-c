/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 François Helg, Cyril Picat and OCTO Technology
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.objectivec.parser;

import static com.sonar.sslr.api.GenericTokenType.EOF;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.api.GenericTokenType.LITERAL;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.next;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Predicate.not;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.and;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.o2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.one2n;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.opt;
import static com.sonar.sslr.impl.matcher.GrammarFunctions.Standard.or;
import static org.sonar.objectivec.api.ObjectiveCKeyword.BREAK;
import static org.sonar.objectivec.api.ObjectiveCKeyword.CASE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.CATCH;
import static org.sonar.objectivec.api.ObjectiveCKeyword.CONTINUE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.DEBUGGER;
import static org.sonar.objectivec.api.ObjectiveCKeyword.DEFAULT;
import static org.sonar.objectivec.api.ObjectiveCKeyword.DELETE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.DO;
import static org.sonar.objectivec.api.ObjectiveCKeyword.ELSE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.FALSE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.FINALLY;
import static org.sonar.objectivec.api.ObjectiveCKeyword.FOR;
import static org.sonar.objectivec.api.ObjectiveCKeyword.FUNCTION;
import static org.sonar.objectivec.api.ObjectiveCKeyword.IF;
import static org.sonar.objectivec.api.ObjectiveCKeyword.IN;
import static org.sonar.objectivec.api.ObjectiveCKeyword.INSTANCEOF;
import static org.sonar.objectivec.api.ObjectiveCKeyword.NEW;
import static org.sonar.objectivec.api.ObjectiveCKeyword.NULL;
import static org.sonar.objectivec.api.ObjectiveCKeyword.RETURN;
import static org.sonar.objectivec.api.ObjectiveCKeyword.SWITCH;
import static org.sonar.objectivec.api.ObjectiveCKeyword.THIS;
import static org.sonar.objectivec.api.ObjectiveCKeyword.THROW;
import static org.sonar.objectivec.api.ObjectiveCKeyword.TRUE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.TRY;
import static org.sonar.objectivec.api.ObjectiveCKeyword.TYPEOF;
import static org.sonar.objectivec.api.ObjectiveCKeyword.VAR;
import static org.sonar.objectivec.api.ObjectiveCKeyword.VOID;
import static org.sonar.objectivec.api.ObjectiveCKeyword.WHILE;
import static org.sonar.objectivec.api.ObjectiveCKeyword.WITH;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.AND;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.ANDAND;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.AND_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.BANG;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.COLON;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.COMMA;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.DEC;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.DIV;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.DIV_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.DOT;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.EQUAL;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.EQUAL2;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.GE;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.GT;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.INC;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.LBRACKET;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.LCURLYBRACE;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.LE;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.LPARENTHESIS;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.LT;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.MINUS;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.MINUS_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.MOD;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.MOD_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.NOTEQUAL;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.NOTEQUAL2;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.OR;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.OROR;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.OR_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.PLUS;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.PLUS_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.QUERY;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.RBRACKET;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.RCURLYBRACE;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.RPARENTHESIS;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SEMI;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SL;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SL_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SR;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SR2;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SR_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.SR_EQU2;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.STAR;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.STAR_EQU;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.TILDA;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.XOR;
import static org.sonar.objectivec.api.ObjectiveCPunctuator.XOR_EQU;
import static org.sonar.objectivec.api.ObjectiveCTokenType.NUMERIC_LITERAL;
import static org.sonar.objectivec.api.ObjectiveCTokenType.REGULAR_EXPRESSION_LITERAL;

import org.sonar.objectivec.api.ObjectiveCGrammar;

public class ObjectiveCGrammarImpl extends ObjectiveCGrammar {

	public ObjectiveCGrammarImpl() {
    eos.is(or(
        opt(SEMI),
        next(RCURLYBRACE),
        next(EOF)));
    eosNoLb.is(or(
        opt(SEMI),
        next(RCURLYBRACE),
        next(EOF)));

    identifierName.is(IDENTIFIER);

    literal.is(or(
        nullLiteral,
        booleanLiteral,
        NUMERIC_LITERAL,
        stringLiteral,
        regularExpressionLiteral));
    nullLiteral.is(NULL);
    booleanLiteral.is(or(
        TRUE,
        FALSE));
    stringLiteral.is(LITERAL);
    regularExpressionLiteral.is(REGULAR_EXPRESSION_LITERAL);

    expressions();
    statements();
    functionsAndPrograms();
  }

  /**
   * A.3 Expressions
   */
  private void expressions() {
    primaryExpression.is(or(
        THIS,
        IDENTIFIER,
        literal,
        arrayLiteral,
        objectLiteral,
        and(LPARENTHESIS, expression, RPARENTHESIS)));
    arrayLiteral.is(or(
        and(LBRACKET, opt(elision), RBRACKET),
        and(LBRACKET, elementList, RBRACKET),
        and(LBRACKET, elementList, COMMA, opt(elision), RBRACKET)));
    elementList.is(and(opt(elision), assignmentExpression), o2n(COMMA, opt(elision), assignmentExpression));
    elision.is(one2n(COMMA));
    objectLiteral.is(or(
        and(LCURLYBRACE, RCURLYBRACE),
        and(LCURLYBRACE, propertyNameAndValueList, RCURLYBRACE),
        and(LCURLYBRACE, propertyNameAndValueList, COMMA, RCURLYBRACE)));
    propertyNameAndValueList.is(propertyAssignment, o2n(COMMA, propertyAssignment));
    propertyAssignment.is(or(
        and(propertyName, COLON, assignmentExpression),
        and("get", propertyName, LPARENTHESIS, RPARENTHESIS, LCURLYBRACE, functionBody, RCURLYBRACE),
        and("set", propertyName, LPARENTHESIS, propertySetParameterList, RPARENTHESIS, LCURLYBRACE, functionBody, RCURLYBRACE)));
    propertyName.is(or(
        identifierName,
        stringLiteral,
        NUMERIC_LITERAL));
    propertySetParameterList.is(IDENTIFIER);
    memberExpression.is(
        or(
            primaryExpression,
            functionExpression,
            and(NEW, memberExpression, arguments)),
        o2n(or(
            and(LBRACKET, expression, RBRACKET),
            and(DOT, identifierName))));
    newExpression.is(or(
        memberExpression,
        and(NEW, newExpression)));
    callExpression.is(
        and(memberExpression, arguments),
        o2n(or(
            arguments,
            and(LBRACKET, expression, RBRACKET),
            and(DOT, identifierName))));
    arguments.is(or(
        and(LPARENTHESIS, argumentList, RPARENTHESIS),
        and(LPARENTHESIS, RPARENTHESIS)));
    argumentList.is(assignmentExpression, o2n(COMMA, assignmentExpression));
    leftHandSideExpression.is(or(
        callExpression,
        newExpression));
    postfixExpression.is(leftHandSideExpression, opt(/* no line terminator here */or(INC, DEC)));
    unaryExpression.is(or(
        postfixExpression,
        and(DELETE, unaryExpression),
        and(VOID, unaryExpression),
        and(TYPEOF, unaryExpression),
        and(INC, unaryExpression),
        and(DEC, unaryExpression),
        and(PLUS, unaryExpression),
        and(MINUS, unaryExpression),
        and(TILDA, unaryExpression),
        and(BANG, unaryExpression)));
    multiplicativeExpression.is(unaryExpression, opt(or(STAR, DIV, MOD), multiplicativeExpression)).skipIfOneChild();
    additiveExpression.is(multiplicativeExpression, opt(or(PLUS, MINUS), additiveExpression)).skipIfOneChild();
    shiftExpression.is(additiveExpression, opt(or(SL, SR, SR2), shiftExpression)).skipIfOneChild();

    relationalExpression.is(shiftExpression, opt(or(LT, GT, LE, GE, INSTANCEOF, IN), relationalExpression)).skipIfOneChild();
    relationalExpressionNoIn.is(shiftExpression, opt(or(LT, GT, LE, GE, INSTANCEOF), relationalExpression)).skipIfOneChild();

    equalityExpression.is(relationalExpression, opt(or(EQUAL, NOTEQUAL, EQUAL2, NOTEQUAL2), equalityExpression)).skipIfOneChild();
    equalityExpressionNoIn.is(relationalExpressionNoIn, opt(or(EQUAL, NOTEQUAL, EQUAL2, NOTEQUAL2), equalityExpressionNoIn)).skipIfOneChild();

    bitwiseAndExpression.is(equalityExpression, opt(AND, bitwiseAndExpression)).skipIfOneChild();
    bitwiseAndExpressionNoIn.is(equalityExpressionNoIn, opt(AND, bitwiseAndExpressionNoIn)).skipIfOneChild();

    bitwiseXorExpression.is(bitwiseAndExpression, opt(XOR, bitwiseXorExpression)).skipIfOneChild();
    bitwiseXorExpressionNoIn.is(bitwiseAndExpressionNoIn, opt(XOR, bitwiseXorExpressionNoIn)).skipIfOneChild();

    bitwiseOrExpression.is(bitwiseXorExpression, opt(OR, bitwiseOrExpression)).skipIfOneChild();
    bitwiseOrExpressionNoIn.is(bitwiseXorExpressionNoIn, opt(OR, bitwiseOrExpressionNoIn)).skipIfOneChild();

    logicalAndExpression.is(bitwiseOrExpression, opt(ANDAND, logicalAndExpression)).skipIfOneChild();
    logicalAndExpressionNoIn.is(bitwiseOrExpressionNoIn, opt(ANDAND, logicalAndExpressionNoIn)).skipIfOneChild();

    logicalOrExpression.is(logicalAndExpression, opt(OROR, logicalOrExpression)).skipIfOneChild();
    logicalOrExpressionNoIn.is(logicalAndExpressionNoIn, opt(OROR, logicalOrExpressionNoIn)).skipIfOneChild();

    conditionalExpression.is(logicalOrExpression, opt(QUERY, assignmentExpression, COLON, assignmentExpression)).skipIfOneChild();
    conditionalExpressionNoIn.is(logicalOrExpressionNoIn, opt(QUERY, assignmentExpression, COLON, assignmentExpressionNoIn)).skipIfOneChild();

    assignmentExpression.is(or(
        and(leftHandSideExpression, EQU, assignmentExpression),
        and(leftHandSideExpression, assignmentOperator, assignmentExpression),
        conditionalExpression)).skipIfOneChild();
    assignmentExpressionNoIn.is(or(
        and(leftHandSideExpression, EQU, assignmentExpressionNoIn),
        and(leftHandSideExpression, assignmentOperator, assignmentExpressionNoIn),
        conditionalExpressionNoIn)).skipIfOneChild();

    assignmentOperator.is(or(
        STAR_EQU,
        DIV_EQU,
        MOD_EQU,
        PLUS_EQU,
        MINUS_EQU,
        SL_EQU,
        SR_EQU,
        SR_EQU2,
        AND_EQU,
        XOR_EQU,
        OR_EQU));

    expression.is(assignmentExpression, o2n(COMMA, assignmentExpression));
    expressionNoIn.is(assignmentExpressionNoIn, o2n(COMMA, assignmentExpressionNoIn));
  }

  /**
   * A.4 Statement
   */
  private void statements() {
    statement.is(or(
        block,
        variableStatement,
        emptyStatement,
        labelledStatement,
        expressionStatement,
        ifStatement,
        iterationStatement,
        continueStatement,
        breakStatement,
        returnStatement,
        withStatement,
        switchStatement,
        throwStatement,
        tryStatement,
        debuggerStatement));
    block.is(LCURLYBRACE, opt(statementList), RCURLYBRACE);
    statementList.is(one2n(or(statement, permissive(functionDeclaration))));
    variableStatement.is(VAR, variableDeclarationList, eos);
    variableDeclarationList.is(variableDeclaration, o2n(COMMA, variableDeclaration));
    variableDeclarationListNoIn.is(variableDeclarationNoIn, o2n(COMMA, variableDeclarationNoIn));
    variableDeclaration.is(IDENTIFIER, opt(initialiser));
    variableDeclarationNoIn.is(IDENTIFIER, opt(initialiserNoIn));
    initialiser.is(EQU, assignmentExpression);
    initialiserNoIn.is(EQU, assignmentExpressionNoIn);
    emptyStatement.is(SEMI);
    expressionStatement.is(not(or(LCURLYBRACE, FUNCTION)), expression, eos);
    condition.is(expression);
    ifStatement.is(or(
        and(IF, LPARENTHESIS, condition, RPARENTHESIS, statement, opt(ELSE, statement)),
        and(IF, LPARENTHESIS, condition, RPARENTHESIS, statement)));
    iterationStatement.is(or(
        doWhileStatement,
        whileStatement,
        forInStatement,
        forStatement));
    doWhileStatement.is(DO, statement, WHILE, LPARENTHESIS, condition, RPARENTHESIS, eos);
    whileStatement.is(WHILE, LPARENTHESIS, condition, RPARENTHESIS, statement);
    forInStatement.is(or(
        and(FOR, LPARENTHESIS, leftHandSideExpression, IN, expression, RPARENTHESIS, statement),
        and(FOR, LPARENTHESIS, VAR, variableDeclarationListNoIn, IN, expression, RPARENTHESIS, statement)));
    forStatement.is(or(
        and(FOR, LPARENTHESIS, opt(expressionNoIn), SEMI, opt(condition), SEMI, opt(expression), RPARENTHESIS, statement),
        and(FOR, LPARENTHESIS, VAR, variableDeclarationListNoIn, SEMI, opt(condition), SEMI, opt(expression), RPARENTHESIS, statement)));
    continueStatement.is(or(
        and(CONTINUE, /* TODO no line terminator here */IDENTIFIER, eos),
        and(CONTINUE, eosNoLb)));
    breakStatement.is(or(
        and(BREAK, /* TODO no line terminator here */IDENTIFIER, eos),
        and(BREAK, eosNoLb)));
    returnStatement.is(or(
        and(RETURN, /* TODO no line terminator here */expression, eos),
        and(RETURN, eosNoLb)));
    withStatement.is(WITH, LPARENTHESIS, expression, RPARENTHESIS, statement);
    switchStatement.is(SWITCH, LPARENTHESIS, expression, RPARENTHESIS, caseBlock);
    caseBlock.is(or(
        and(LCURLYBRACE, opt(caseClauses), RCURLYBRACE),
        and(LCURLYBRACE, opt(caseClauses), defaultClause, opt(caseClauses), RCURLYBRACE)));
    caseClauses.is(one2n(caseClause));
    caseClause.is(CASE, expression, COLON, opt(statementList));
    defaultClause.is(DEFAULT, COLON, opt(statementList));
    labelledStatement.is(IDENTIFIER, COLON, statement);
    throwStatement.is(THROW, /* TODO no line terminator here */expression, eos);
    tryStatement.is(TRY, block, or(and(catch_, opt(finally_)), finally_));
    catch_.is(CATCH, LPARENTHESIS, IDENTIFIER, RPARENTHESIS, block);
    finally_.is(FINALLY, block);
    debuggerStatement.is(DEBUGGER, eos);
  }

  /**
   * A.5 Functions and Programs
   */
  private void functionsAndPrograms() {
    functionDeclaration.is(FUNCTION, IDENTIFIER, LPARENTHESIS, opt(formalParameterList), RPARENTHESIS, LCURLYBRACE, functionBody, RCURLYBRACE);
    functionExpression.is(FUNCTION, opt(IDENTIFIER), LPARENTHESIS, opt(formalParameterList), RPARENTHESIS, LCURLYBRACE, functionBody, RCURLYBRACE);
    formalParameterList.is(IDENTIFIER, o2n(COMMA, IDENTIFIER));
    functionBody.is(opt(sourceElements));
    program.is(opt(sourceElements), EOF);
    sourceElements.is(one2n(sourceElement));
    sourceElement.is(or(
        statement,
        functionDeclaration));
  }

  /**
   * Declares some constructs, which ES5 grammar does not support, but script engines support.
   * For example prototype.js version 1.7 has a function declaration in a block, which is invalid under both ES3 and ES5.
   */
  private static Object permissive(Object object) {
    return object;
  }
	
}
