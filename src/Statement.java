package com.craftinginterpreters.lox;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {
        R visitExprStatement(Expr statement);
        R visitPrintStatement(Print statement);
        R visitVarStatement(Var statement);
    }
  static class Expr extends Statement {
    Expr(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitExprStatement(this);
    }

    final Expression expression;
  }
  static class Print extends Statement {
    Print(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitPrintStatement(this);
    }

    final Expression expression;
  }
  static class Var extends Statement {
    Var(Token name, Expression initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitVarStatement(this);
    }

    final Token name;
    final Expression initializer;
  }

    abstract <R> R accept(Visitor<R> visitor);
}
