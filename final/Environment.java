public class Environment extends Lexeme{

    public Lexeme create(){
        return cons(Type.ENV, cons(Type.TAB, null, null),null);
    }

    public Lexeme lookup(Lexeme variable, Lexeme env){
        while(env != null){
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while(vars != null){
                if(compareVars(variable,car(vars))){
                    return car(vals);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }
        System.out.println("Variable " + variable.c + " is undefined");
        System.exit(0);
        return null;
    }

    public Lexeme update(Lexeme variable, Lexeme value, Lexeme env){
        while(env != null){
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while(vars != null){
                if(compareVars(variable,car(vars))){
                    setCar(vals, value);
                    return car(vals);
                }
                vars = cdr(vars);
                vals = cdr(vals);
            }
            env = cdr(env);
        }
        System.out.println("Variable " + variable.c + " is undefined");
        System.exit(0);
        return null;
    }

    public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env){
        setCar(car(env), cons(Type.ID, variable, car(car(env))));
        setCdr(car(env), cons(Type.VAL, value, cdr(car(env))));
        return value;
    }

    public Lexeme extend(Lexeme variables, Lexeme values, Lexeme env){
        return cons(Type.ENV, cons(Type.TAB,variables,values),env);
    }

    public void insertBuiltIns(Lexeme env){
        Lexeme println = new Lexeme(Type.VARIABLE, "println", null, null);
        insert(println, null, env);
        Lexeme print = new Lexeme(Type.VARIABLE, "print", null, null);
        insert(print, null, env);
        Lexeme open = new Lexeme(Type.VARIABLE, "openFile", null, null);
        insert(open, null, env);
        Lexeme read = new Lexeme(Type.VARIABLE, "read", null, null);
        insert(read, null, env);
        Lexeme atEnd = new Lexeme(Type.VARIABLE, "atEnd", null, null);
        insert(atEnd, null, env);
        Lexeme close = new Lexeme(Type.VARIABLE, "closeFile", null, null);
        insert(close, null, env);
        Lexeme newArray = new Lexeme(Type.VARIABLE, "newArray", null, null);
        insert(newArray, null, env);
        Lexeme getArray = new Lexeme(Type.VARIABLE, "getArray", null, null);
        insert(getArray, null, env);
        Lexeme setArray = new Lexeme(Type.VARIABLE, "setArray", null, null);
        insert(setArray, null, env);
        Lexeme getArgCount = new Lexeme(Type.VARIABLE, "getArgCount", null, null);
        insert(getArgCount, null, env);
        Lexeme getArg = new Lexeme(Type.VARIABLE, "getArg", null, null);
        insert(getArg, null, env);
        Lexeme end = new Lexeme(Type.VARIABLE, "end", null, null);
        insert(end, null, env);
    }
}

