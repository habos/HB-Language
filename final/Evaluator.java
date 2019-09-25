import java.io.FileNotFoundException;
import java.io.IOException;

public class Evaluator extends Parser{

    public Environment e = new Environment();
    public int argCount;
    public String[] args;

    public Lexeme eval(Lexeme tree, Lexeme env){
        if(tree == null){
            return null;
        }
        switch (tree.t){

            //Unarys
            case NUMBER: return tree;
            case STRING: return tree;
            case FILE: return tree;
            case VARIABLE: return e.lookup(tree, env);
            case LAMBDA: return evalLambda(tree, env);
            case UMINUS: return evalUminus(tree, env);
            case OPAREN: return eval(tree.right, env);

            //Operators
            case PLUS: return evalPlus(tree, env);
            case MINUS: return evalMinus(tree, env);
            case MULTIPLY: return evalMultiply(tree, env);
            case DIVISION: return evalDivision(tree, env);
            case MODULO: return evalModulo(tree, env);
            case GREATER_THAN: return evalGreater(tree, env);
            case LESS_THAN: return evalLess(tree, env);
            case EQUAL: return evalEqual(tree, env);
            case NOT_EQUAL: return evalNotEqual(tree, env);
            case AND: return evalAnd(tree, env);
            case OR: return evalOr(tree, env);
            case DOT: return evalDot(tree, env);

            //Definitions and calls
            case ASSIGN: return evalAssign(tree, env);
            case FUNCTION: return evalFuncDef(tree, env);
            case CALL:
                try {
                    return evalFuncCall(tree, env);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return null;
                }
            case VAR: return evalVarDef(tree, env);

            //Loops and If Statements
            case WHILE: return evalWhile(tree, env);
            case IF: return evalIf(tree, env);

            //Program and stats
            case PROGRAM: return evalProgram(tree, env);
            case STAT: return evalStat(tree, env);
            case OBRACE: return evalBlock(tree, env);

            //Default
            default: System.out.println("Bad expression"); System.exit(0); return null;
        }
    }

    //Operator helper functions
    private Lexeme newIntegerLexeme(int i){
        return new Lexeme(Type.NUMBER, null, i, null);
    }

    private Lexeme newDoubleLexeme(double d){
        return new Lexeme(Type.NUMBER, null, null, d);
    }

    private Lexeme newStringLexeme(String c){
        return new Lexeme(Type.STRING, c, null, null);
    }

    private Lexeme newBooleanLexeme(Boolean b){
        if(b){
            return new Lexeme(Type.BOOLEAN, "true", null, null);
        }else{
            return new Lexeme(Type.BOOLEAN, "false", null, null);
        }
    }

    private Lexeme evalUminus(Lexeme t, Lexeme env){
        if(t.right.i != null){
            return new Lexeme(Type.NUMBER, null, -t.right.i, null);
        } else {
            return new Lexeme(Type.NUMBER, null, null, -t.right.d);
        }
    }

    private Lexeme evalPlus(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Add if they are numbers
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newIntegerLexeme(left.i + right.i);
            } else if(left.i != null && right.d != null){
                return newDoubleLexeme(left.i + right.d);
            } else if(left.d != null && right.i != null){
                return newDoubleLexeme(left.d + right.i);
            } else{
                return newDoubleLexeme(left.d + right.d);
            }
            //Concat if they are strings
        } else if(left.t == Type.STRING && right.t == Type.STRING){
            return newStringLexeme(left.c.concat(right.c));
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '+'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalMinus(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Add if they are numbers
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newIntegerLexeme(left.i - right.i);
            } else if(left.i != null && right.d != null){
                return newDoubleLexeme(left.i - right.d);
            } else if(left.d != null && right.i != null){
                return newDoubleLexeme(left.d - right.i);
            } else{
                return newDoubleLexeme(left.d - right.d);
            }
            //Remove all of the strings that match the right string in the left string
        } else if(left.t == Type.STRING && right.t == Type.STRING){
            return newStringLexeme(left.c.replaceAll(right.c, ""));
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '-'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalMultiply(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Multiply if they are numbers
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newIntegerLexeme(left.i * right.i);
            } else if(left.i != null && right.d != null){
                return newDoubleLexeme(left.i * right.d);
            } else if(left.d != null && right.i != null){
                return newDoubleLexeme(left.d * right.i);
            } else{
                return newDoubleLexeme(left.d * right.d);
            }
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '*'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalDivision(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        //Divide if they are numbers
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newIntegerLexeme(left.i / right.i);
            } else if(left.i != null && right.d != null){
                return newDoubleLexeme(left.i / right.d);
            } else if(left.d != null && right.i != null){
                return newDoubleLexeme(left.d / right.i);
            } else{
                return newDoubleLexeme(left.d / right.d);
            }
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '/'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalModulo(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);

        //Check if the right number is zero
        if(right.i == 0 || right.d == 0){
            System.out.println("Cannot modulo by zero");
            System.exit(0);
            return null;
        }
        //Divide if they are numbers
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newIntegerLexeme(left.i % right.i);
            } else {
                System.out.println("Cannot modulo doubles");
                System.exit(0);
                return null;
            }
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '%'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalGreater(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expression
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newBooleanLexeme(left.i > right.i);
            } else if(left.i != null && right.d != null){
                return newBooleanLexeme(left.i > right.d);
            } else if(left.d != null && right.i != null){
                return newBooleanLexeme(left.d > right.i);
            } else{
                return newBooleanLexeme(left.d > right.d);
            }
            //lexographically compare strings
        } else if(left.t == Type.STRING && right.t == Type.STRING){
            return newBooleanLexeme(left.c.compareTo(right.c) > 0);
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '>'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalLess(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expression
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newBooleanLexeme(left.i < right.i);
            } else if(left.i != null && right.d != null){
                return newBooleanLexeme(left.i < right.d);
            } else if(left.d != null && right.i != null){
                return newBooleanLexeme(left.d < right.i);
            } else{
                return newBooleanLexeme(left.d < right.d);
            }
            //lexographically compare strings
        } else if(left.t == Type.STRING && right.t == Type.STRING){
            return newBooleanLexeme(left.c.compareTo(right.c) < 0);
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '<'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalEqual(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expression
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newBooleanLexeme(left.i == right.i);
            } else if(left.i != null && right.d != null){
                return newBooleanLexeme((double)left.i == right.d);
            } else if(left.d != null && right.i != null){
                return newBooleanLexeme(left.d == (double)right.i);
            } else{
                return newBooleanLexeme(left.d == right.d);
            }
            //lexographically compare strings
        } else if(left.t == Type.STRING && right.t == Type.STRING){
            return newBooleanLexeme(left.c.equals(right.c));
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '=='");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalNotEqual(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expression
        if(left.t == Type.NUMBER && right.t == Type.NUMBER){
            if(left.i != null && right.i != null){
                return newBooleanLexeme(left.i != right.i);
            } else if(left.i != null && right.d != null){
                return newBooleanLexeme((double)left.i != right.d);
            } else if(left.d != null && right.i != null){
                return newBooleanLexeme(left.d != (double)right.i);
            } else{
                return newBooleanLexeme(left.d != right.d);
            }
            //lexographically compare strings
        } else if(left.t == Type.STRING && right.t != Type.STRING){
            return newBooleanLexeme(!(left.c.equals(right.c)));
            //Otherwise it just aint happening
        } else{
            System.out.println("Incorrect use of operation '!='");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalAnd(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expressions
        if(left.t == Type.BOOLEAN && right.t == Type.BOOLEAN){
            if(left.c.equals("true") && right.c.equals("true")){
                return newBooleanLexeme(true);
            } else{
                return newBooleanLexeme(false);
            }
        } else {
            System.out.println("Incorrect use of operation '&'");
            System.exit(0);
            return null;
        }
    }

    private Lexeme evalOr(Lexeme t, Lexeme env){
        //Evaluate the left and right hand sides
        Lexeme left = eval(t.left, env);
        Lexeme right = eval(t.right, env);
        //Check boolean expressions
        if(left.t == Type.BOOLEAN && right.t == Type.BOOLEAN){
            if(left.c.equals("true") || right.c.equals("true")){
                return newBooleanLexeme(true);
            } else{
                return newBooleanLexeme(false);
            }
        } else {
            System.out.println("Incorrect use of operation '|'");
            System.exit(0);
            return null;
        }
    }

    //No idea how NOT should work

    private Lexeme evalAssign(Lexeme t, Lexeme env){
        //Evaluate the right hand side
        Lexeme val = eval(t.right, env);
        if(t.left.t == Type.DOT){
            Lexeme obj = eval(t.left.left, env);
            e.update(t.left.right, val, obj);
        } else {
            e.update(t.left, val, env);
        }
        return val;
    }

    private Lexeme evalDot(Lexeme t, Lexeme env){
        Lexeme obj = eval(t.left, env);
        return eval(t.right, obj);
    }

    private Lexeme evalBlock(Lexeme t, Lexeme env){
        Lexeme result = t.right;
        while(t != null){
            result = eval(t.left, env);
            t = t.right;
        }
        return result;
    }

    private Lexeme evalVarDef(Lexeme t, Lexeme env){
        Lexeme value = eval(t.left, env);
        e.insert(t.right, value, env);
        return value;
    }

    private Lexeme evalFuncDef(Lexeme t, Lexeme env){
        Lexeme closure = cons(Type.CLOSURE, env, t);
        e.insert(t.left, closure, env);
        return closure;
    }

    private Lexeme evalLambda(Lexeme t, Lexeme env){
        Lexeme closure = cons(Type.CLOSURE, env, t);
        return closure;
    }

    private Lexeme evalFuncCall(Lexeme t, Lexeme env) throws IOException {
        Lexeme args = t.right;
        Lexeme eargs = evalArgs(args, env);
        //Look for builtin functions
        if(t.left.c.equals("println")){
            println(eargs);
            return null;
        }else if(t.left.c.equals("print")) {
            print(eargs);
            return null;
        }else if(t.left.c.equals("end")) {
            end();
            return null;
        }else if(t.left.c.equals("openFile")) {
            return openFile(eargs);
        }else if(t.left.c.equals("read")) {
            return read(eargs);
        }else if(t.left.c.equals("atEnd")) {
            return atEnd(eargs);
        }else if(t.left.c.equals("closeFile")) {
            return closeFile(eargs);
        }else if(t.left.c.equals("setArray")) {
            return setArray(eargs);
        }else if(t.left.c.equals("getArray")) {
            return getArray(eargs);
        }else if(t.left.c.equals("newArray")) {
            return newArray(eargs);
        }else if(t.left.c.equals("getArgCount")) {
            return getArgCount();
        }else if(t.left.c.equals("getArg")) {
            return getArg(eargs);
        }else{
            Lexeme closure = eval(t.left, env);
            Lexeme params = closure.right.right.left;
            Lexeme body = closure.right.right.right;
            Lexeme senv = closure.left;
            Lexeme xenv = e.extend(params, eargs, senv);
            e.insert(new Lexeme(Type.VARIABLE, "this", null, null), xenv, xenv);
            return eval(body, xenv);
        }
    }

    private Lexeme evalArgs(Lexeme args, Lexeme env){
        if(args == null){
            return null;
        } else {
            return cons(Type.LIST, eval(args.left, env), evalArgs(args.right, env));
        }
    }

    private Lexeme evalIf(Lexeme t, Lexeme env){
        Lexeme bool = eval(t.left.left,env);
        if(bool.t != Type.BOOLEAN){
            System.out.println("Incorrect expression in if statement");
            System.exit(0);
            return null;
        }
        if(bool.c.equals("true")){
            return eval(t.left.right, env);
        }else if(t.right != null){
            return evalElse(t.right, env);
        } else {
            return null;
        }
    }

    private Lexeme evalElse(Lexeme t, Lexeme env){
        if(t.right.t == Type.IF){
            return evalIf(t.right, env);
        } else{
            return eval(t.right, env);
        }
    }

    private Lexeme evalWhile(Lexeme t, Lexeme env){
        Lexeme bool = eval(t.left, env);
        if(bool.t != Type.BOOLEAN){
            System.out.println("Incorrect expression in while loop");
            System.exit(0);
            return null;
        }
        while(bool.c.equals("true")){
            eval(t.right, env);
            bool = eval(t.left, env);
        }
        return null;
    }

    private Lexeme evalProgram(Lexeme t, Lexeme env){
        return eval(t.right, env);
    }

    private Lexeme evalStat(Lexeme t, Lexeme env){
        if(t == null){
            return null;
        }
        eval(t.left, env);
        return evalStat(t.right, env);
    }

    //BUILTIN FUNCTIONS

    private void println(Lexeme t){
        while(t != null){
            t.left.println();
            t = t.right;
        }
    }

    private void print(Lexeme t){
        while(t != null){
            t.left.print();
            t = t.right;
        }
    }

    private Lexeme openFile(Lexeme t) throws FileNotFoundException {
        Lexeme fp = new Lexeme(Type.FILE, t.left.c);
        return fp;
    }

    private Lexeme read(Lexeme t) throws IOException {
        Lexeme fp = t.left;
        char x = (char)(fp.reader.read());
        String token = "";
        while (Character.isWhitespace(x)){
            x = (char)(fp.reader.read());
        }
        while (Character.isDigit(x)){
            token = token + x;
            x = (char)(fp.reader.read());
        }
        fp.reader.unread((int)x);
        int i = Integer.parseInt(token);
        return newIntegerLexeme(i);
    }

    private Lexeme atEnd(Lexeme t) throws IOException {
        Lexeme fp = t.left;
        char x = (char)fp.reader.read();
        while (Character.isWhitespace(x)){
            x = (char)(fp.reader.read());
        }
        if(((int)x) == -1 || ((int)x) == 65535){
            return newBooleanLexeme(false);
        } else {
            fp.reader.unread((int)x);
            return newBooleanLexeme(true);
        }
    }

    private Lexeme closeFile(Lexeme t) throws IOException {
        Lexeme fp = t.left;
        fp.reader.close();
        return newBooleanLexeme(true);
    }

    private Lexeme newArray(Lexeme t){
        if(getListLength(t) != 1){
            System.out.println("Incorrect number of arguments for newArray");
            System.exit(0);
        }
        if(t.left.i == null){
            System.out.println("Incorrect type passed to newArray.");
            System.exit(0);
        }
        Lexeme arr = new Lexeme(Type.ARRAY, t.left.i);
        return arr;
    }

    private Lexeme getArray(Lexeme t){
        if(getListLength(t) != 2){
            System.out.println("Incorrect number of arguments for getArray");
            System.exit(0);
        }
        Lexeme array = t.left;
        Lexeme index = t.right.left;
        return array.arr[index.i];
    }

    private Lexeme setArray(Lexeme t){
        if(getListLength(t) != 3){
            System.out.println("Incorrect number of arguments for setArray");
            System.exit(0);
        }
        Lexeme array = t.left;
        Lexeme index = t.right.left;
        Lexeme value = t.right.right.left;
        array.arr[index.i] = value;
        return value;
    }

    private int getListLength(Lexeme t) {
        int i = 0;
        while (t != null){
            i++;
            t = t.right;
        }
        return i;
    }

    private Lexeme getArgCount(){
        return new Lexeme(Type.NUMBER, null, argCount, null);
    }

    private Lexeme getArg(Lexeme t){
        return new Lexeme(Type.STRING, args[t.left.i], null, null);
    }

    private void end(){
        System.exit(0);
    }


}

