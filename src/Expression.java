package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expression {
    interface Visitor<R> {
        R visitAssignExpression(Assign expression);
        R visitBinaryExpression(Binary expression);
        R visitCallExpression(Call expression);
        R visitGetExpression(Get expression);
        R visitGroupingExpression(Grouping expression);
        R visitLiteralExpression(Literal expression);
        R visitLogicalExpression(Logical expression);
        R visitSetExpression(Set expression);
        R visitThisExpression(This expression);
        R visitUnaryExpression(Unary expression);
        R visitVariableExpression(Variable expression);
    }
  static class Assign extends Expression {
    Assign(Token name, Expression value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExpression(this);
    }

    final Token name;
    final Expression value;
  }
  static class Binary extends Expression {
    Binary(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitBinaryExpression(this);
    }

    final Expression left;
    final Token operator;
    final Expression right;
  }
  static class Call extends Expression {
    Call(Expression callee, Token closingParenthesis, List<Expression> arguments) {
      this.callee = callee;
      this.closingParenthesis = closingParenthesis;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitCallExpression(this);
    }

    final Expression callee;
    final Token closingParenthesis;
    final List<Expression> arguments;
  }
  static class Get extends Expression {
    Get(Expression object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitGetExpression(this);
    }

    final Expression object;
    final Token name;
  }
  static class Grouping extends Expression {
    Grouping(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroupingExpression(this);
    }

    final Expression expression;
  }
  static class Literal extends Expression {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }

    final Object value;
  }
  static class Logical extends Expression {
    Logical(Expression left, Token operator, Expression right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitLogicalExpression(this);
    }

    final Expression left;
    final Token operator;
    final Expression right;
  }
  static class Set extends Expression {
    Set(Expression object, Token name, Expression value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitSetExpression(this);
    }

    final Expression object;
    final Token name;
    final Expression value;
  }
  static class This extends Expression {
    This(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitThisExpression(this);
    }

    final Token keyword;
  }
  static class Unary extends Expression {
    Unary(Token operator, Expression right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpression(this);
    }

    final Token operator;
    final Expression right;
  }
  static class Variable extends Expression {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitVariableExpression(this);
    }

    final Token name;
  }

    abstract <R> R accept(Visitor<R> visitor);
}
