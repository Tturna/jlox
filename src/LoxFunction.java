package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Statement.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    LoxFunction(Statement.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    // Yap: How does the "this" keyword work and wtf are all these environments?
    //
    // When a class is parsed, all of its methods go into the class node.
    // When the class node is interpreted, a LoxFunction object is created for each method.
    // All the methods get the global environment as their closure.
    // A LoxClass object is created and it includes all the methods.
    //
    // When a method is called, the method is first looked up from the class instance.
    // This is done by LoxInstance.get() which calls LoxClass.findMethod(), because
    // the methods are a part of the class, not the instance. LoxInstance.get() then calls
    // this bind() method.
    //
    // This method creates a new environment where "this" is defined. It needs to be in a new
    // environment because otherwise "this" would be defined in the scope outside the class body.
    // If the class is declared in global scope, "this" would leak into it because the function closure
    // points to the global environment. This method returns a new function with the closure now
    // pointing to the new environment (that points to the global one).
    //
    // After that, the call can actually start executing. The method below, "call", will be
    // executed. It creates another environment for the function body and defines all the
    // parameters there. It would be kinda funky to define "this" in that particular env
    // because it would mean it only works in that one function body when in reality it
    // should work in all method bodies inside the class declaration. Theoretically it
    // would be possible though.
    //
    // I guess the environment created in bind() is like the class body scope. With that in mind,
    // it feels a bit weird to do it like this because this new environment is created for each
    // method and all the environments just define "this" which points to the same instance.
    // Surely it would make more sense to create one environment for the class instance and bind
    // all methods to it.
    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");

            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");

        return null;
    }
}
