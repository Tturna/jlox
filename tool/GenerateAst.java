// package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

// AST = Abstract Syntax Tree
// This script generates code that defines valid language structures like expressions and statements.
// These expressions and statements are used by the parser to form a syntax tree from tokens.
public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <out dir>");
            System.exit(64);
        }

        String outDir = args[0];
        defineAst(outDir, "Expression", Arrays.asList(
            "Assign   : Token name, Expression value",
            "Binary   : Expression left, Token operator, Expression right",
            "Grouping : Expression expression",
            "Literal  : Object value",
            "Logical  : Expression left, Token operator, Expression right",
            "Unary    : Token operator, Expression right",
            "Variable : Token name"
        ));

        defineAst(outDir, "Statement", Arrays.asList(
            "Block   : List<Statement> statements",
            "Expr    : Expression expression",
            "If      : Expression condition, Statement thenBranch, Statement elseBranch",
            "Print   : Expression expression",
            "Var     : Token name, Expression initializer",
            "While   : Expression condition, Statement body"
        ));
    }

    // Generates a class (like "Expression") that has a subclass for each expression type
    // defined above (like "Binary" and "Literal").
    private static void defineAst(String outDir, String baseName, List<String> types) throws IOException {
        String path = outDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim(); 
            defineType(writer, baseName, className, fields);
        }

        // the base accept() method for the visitor pattern
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
    }

    private static void defineType( PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("  static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        // visitor pattern
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("        return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }
}
