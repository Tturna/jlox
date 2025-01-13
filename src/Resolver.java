package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
    private final Interpreter interpreter;

    // The boolean determines whether the variable's initializer has been resolved.
    // I guess this is a variable map then?
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private void resolve(Expression expression) {
        expression.accept(this);
    }

    private void resolve(Statement statement) {
        statement.accept(this);
    }

    void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    private void resolveFunction(Statement.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }

        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveLocal(Expression expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();

        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "The current scope already has a variable with this name.");
        }

        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    @Override
    public Void visitBlockStatement(Statement.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitClassStatement(Statement.Class stmt) {
        declare(stmt.name);
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitExprStatement(Statement.Expr stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStatement(Statement.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStatement(Statement.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStatement(Statement.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }

        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitVarStatement(Statement.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitWhileStatement(Statement.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitAssignExpression(Expression.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpression(Expression.Call expr) {
        resolve(expr.callee);

        for (Expression argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGroupingExpression(Expression.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpression(Expression.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpression(Expression.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpression(Expression.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpression(Expression.Variable expr) {
        // How this works:
        // 1. When we encounter a "var" keyword, visitVarStatement is called
        // 2. It calls declare()
        // 3. declare() adds the variable to the scope map with a boolean value of FALSE
        //    to represent that the variable's initializer is not resolved yet
        // 4. visitVarStatement calls resolve() for the initializer if it's not null
        // 5. resolve() calls visitVariableExpression() (this method)
        // 6. we check if the initializer is an expression whose lexeme matches
        //    the variable name that was just declared
        // 7. if so, we report an error
        //
        // Note: I think this would not report an error if you wrapped the variable name
        // in another expression like "(varname)". That might be caught somewhere else though.
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }
}
