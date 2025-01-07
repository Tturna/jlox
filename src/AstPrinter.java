package com.craftinginterpreters.lox;

/*
Crafting Interpreters
The end of Working with trees:
https://craftinginterpreters.com/representing-code.html#working-with-trees

"You can go ahead and delete this method (main). We won’t need it. Also, as we add new syntax tree types,
I won’t bother showing the necessary visit methods for them in AstPrinter.
If you want to (and you want the Java compiler to not yell at you), go ahead and add them yourself.
It will come in handy in the next chapter when we start parsing Lox code into syntax trees.
Or, if you don’t care to maintain AstPrinter, feel free to delete it. We won’t need it again."
*/

class AstPrinter implements Expression.Visitor<String> {
    String print(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expression... expresssions) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);

        for (Expression expr : expresssions) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");

        return builder.toString();
    }

    public static void main(String[] args) {
        Expression expr = new Expression.Binary(
            new Expression.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expression.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expression.Grouping(new Expression.Literal(45.67))
        );

        System.out.println(new AstPrinter().print(expr));
    }
}
