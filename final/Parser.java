import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser extends Lexeme{
    public Lexeme curLex;
    public Lexer l;
    public Lexeme PRESENT;
    public Environment e = new Environment();
    private boolean isDot = false;


    public Parser(String filename) throws FileNotFoundException {
        this.l = new Lexer(filename);
    }

    public Parser(){
        this.curLex = null;
    }

    private boolean check(Lexeme.Type type){
        return this.curLex.t == type;
    }

    private void advance() throws IOException {
        this.curLex = this.l.Lex();
    }

    private Lexeme match(Lexeme.Type type) throws IOException{
        matchNoAdvance(type);
        Lexeme returnLex = this.curLex;
        advance();
        return returnLex;
    }

    private void matchNoAdvance(Lexeme.Type type){
        if(this.curLex.t == Type.ENDofINPUT){
            System.out.println("valid");
            System.exit(0);
        }
        if(!check(type)){
            System.out.print("Incorrect use of ");
            this.curLex.display();
            System.out.println(" on line " + this.l.lineCount);
            System.out.println("invalid");
            System.exit(0);
        }
    }

    public Lexeme unary(Lexeme env) throws IOException{
        Lexeme tree;
        if(check(Type.VARIABLE)) {
            if(!isDot) {
                e.lookup(this.curLex, env);
                tree = match(Type.VARIABLE);
                if (check(Type.OPAREN)) {
                    return functionCall(tree, env);
                }
            } else {
                this.isDot = false;
                tree = match(Type.VARIABLE);
                if (check(Type.OPAREN)) {
                    return functionCall(tree, env);
                }
            }
            return tree;
        } else if(check(Type.NUMBER)){
            return match(Type.NUMBER);
        }else if(check(Type.FILE)){
            return match(Type.FILE);
        } else if(check(Type.STRING)) {
            return match(Type.STRING);
        }else if(check(Type.BOOLEAN)) {
            return match(Type.BOOLEAN);
        }else if(check(Type.LAMBDA)){
            return lambda(env);
        } else if(arrayPending()){
            return array(env);
        }else if(check(Type.OPAREN)){
            match(Type.OPAREN);
            tree = expressions(env);
            match(Type.CPAREN);
            return cons(Type.OPAREN, null, tree);
        }else if(check(Type.MINUS)){
            match(Type.MINUS);
            tree = match(Type.NUMBER);
            return cons(Type.UMINUS, null, tree);
        } else {
            return op();
        }
    }

    public Lexeme program(Lexeme env) throws IOException{
        Lexeme prog;
        prog = statements(env);
        return cons(Type.PROGRAM, null, prog);
    }

    public Lexeme block(Lexeme env) throws IOException{
        Lexeme tree;
        match(Type.OBRACE);
        tree = statements(env);
        match(Type.CBRACE);
        return cons(Type.OBRACE, null, tree);
    }

    public Lexeme statements(Lexeme env) throws IOException{
        Lexeme s, tree;
        s = stat(env);
        if(statementsPending()){
            tree = statements(env);
        }
        else{
            tree = null;
        }
        return cons(Type.STAT, s, tree);
    }

    public Lexeme header() throws IOException{
        Lexeme var;
        match(Type.AT);
        match(Type.INCLUDE);
        var = match(Type.VARIABLE);
        match(Type.SEMICOLON);
        return cons(Type.HEADER, null, var);
    }

    public Lexeme op() throws IOException{
        if(check(Type.PLUS)){
            return match(Type.PLUS);
        } else if(check(Type.MINUS)){
            return match(Type.MINUS);
        } else if(check(Type.MULTIPLY)){
            return match(Type.MULTIPLY);
        } else if(check(Type.DIVISION)){
            return match(Type.DIVISION);
        } else if(check(Type.MODULO)){
            return match(Type.MODULO);
        } else if(check(Type.GREATER_THAN)){
            return match(Type.GREATER_THAN);
        } else if(check(Type.LESS_THAN)){
            return match(Type.LESS_THAN);
        } else if(check(Type.EQUAL)){
            return match(Type.EQUAL);
        } else if(check(Type.NOT)){
            return match(Type.NOT);
        } else if(check(Type.AND)){
            return match(Type.AND);
        } else if(check(Type.OR)){
            return match(Type.OR);
        } else if(check(Type.DOT)) {
            this.isDot = true;
            return match(Type.DOT);
        } else if(check(Type.NOT_EQUAL)){
            return match(Type.NOT_EQUAL);
        } else{
            return match(Type.ASSIGN);
        }
    }

    public Lexeme expressions(Lexeme env) throws IOException{
        Lexeme u, o, e;
        u = unary(env);
        if(opPending()){
            o = op();
            e = expressions(env);
            return cons(o.t, u, e);
        }
        else{
            return u;
        }
    }

    public Lexeme stat(Lexeme env) throws IOException{
        if(forLoopPending()){
            return forLoop(env);
        } else if(whileLoopPending()){
            return whileLoop(env);
        } else if(ifStatPending()) {
            return ifStat(env);
        } else if(optElsePending()){
            return optElse(env);
        } else if(expressionsPending()){
            Lexeme e = expressions(env);
            match(Type.SEMICOLON);
            return e;
        } else if(functionDefPending()){
            return functionDef(env);
        } else if(varDefPending()) {
            return varDef(env);
        } else if(headerPending()) {
            return header();
        } else
            return structDef(env);
    }

    public Lexeme whileLoop(Lexeme env) throws IOException{
        Lexeme loop, block;
        match(Type.WHILE);
        match(Type.OPAREN);
        loop = expressions(env);
        match(Type.CPAREN);
        block = block(env);
        return cons(Type.WHILE, loop, block);
    }

    public Lexeme forLoop(Lexeme env) throws IOException{
        Lexeme stat1, stat2, stat3, block;
        match(Type.FOR);
        match(Type.OPAREN);
        stat1 = stat(env);
        stat2 = stat(env);
        stat3 = stat(env);
        setCdr(stat1, stat2);
        setCdr(stat2, stat3);
        match(Type.CPAREN);
        block = block(env);
        return cons(Type.FOR, stat1, block);
    }

    public Lexeme ifStat(Lexeme env) throws IOException{
        Lexeme list, block;
        match(Type.IF);
        match(Type.OPAREN);
        list = expressions(env);
        match(Type.CPAREN);
        block = block(env);
        return cons(Type.IF, cons(Type.IFBLOCK, list, block), optElse(env));
    }

    public Lexeme optElse(Lexeme env) throws IOException{
        Lexeme ifVal, block;
        if(check(Type.ELSE)){
            match(Type.ELSE);
            if(blockPending()){
                block = block(env);
                return cons(Type.ELSE, null, block);
            }else if(ifStatPending()){
                ifVal = ifStat(env);
                return cons(Type.ELSE, null, ifVal);
            } else {
                return null;
            }
        }else{
            return null;
        }
    }

    public Lexeme array(Lexeme env) throws IOException{
        Lexeme tree;
        match(Type.OBRACKET);
        tree = list(env);
        match(Type.CBRACKET);
        return cons(Type.OBRACKET, null, tree);
    }

    public Lexeme list(Lexeme env) throws IOException{
        Lexeme exp, list;
        exp = expressions(env);
        if(check(Type.COMMA)){
            match(Type.COMMA);
            list = list(env);
        }
        else{
            list = null;
        }
        return cons(Type.LIST, exp, list);
    }

    public Lexeme optList(Lexeme env) throws IOException{
        if(listPending()){
            return list(env);
        }
        else{
            return null;
        }
    }

    public Lexeme varList() throws IOException {
        Lexeme v, l;
        v = match(Type.VARIABLE);
        if (check(Type.COMMA)){
            match(Type.COMMA);
            l = varList();
        }
        else{
            l = null;
        }
        return cons(Type.LIST, v, l);
    }

    public Lexeme optVarList() throws IOException{
        if(varListPending()){
            return varList();
        }
        else{
            return null;
        }
    }

    public Lexeme varDef(Lexeme env) throws IOException{
        Lexeme var, value;
        match(Type.VAR);
        var = match(Type.VARIABLE);
        if(check(Type.SEMICOLON)){
            match(Type.SEMICOLON);
            value = null;
        } else {
            match(Type.ASSIGN);
            value = expressions(env);
            match(Type.SEMICOLON);
        }
        e.insert(var, PRESENT, env);
        return cons(Type.VAR, value, var);
    }

    public Lexeme lambda(Lexeme env) throws IOException{
        Lexeme list, block;
        Lexeme xenv;

        match(Type.LAMBDA);
        match(Type.OPAREN);
        list = optVarList();
        match(Type.CPAREN);

        xenv = e.extend(list, list, env);

        block = block(xenv);

        return cons(Type.LAMBDA, null, cons(Type.FUNCLIST, list, block));
    }

    public Lexeme functionDef(Lexeme env) throws IOException{
        Lexeme var, list, block;
        Lexeme xenv;

        match(Type.FUNCTION);
        var = match(Type.VARIABLE);
        match(Type.OPAREN);
        list = optVarList();
        match(Type.CPAREN);

        e.insert(var, PRESENT, env);
        e.insert(new Lexeme(Type.VARIABLE, "this", null, null), PRESENT, env);

        xenv = e.extend(list, list, env);

        block = block(xenv);

        return cons(Type.FUNCTION, var, cons(Type.FUNCLIST, list, block));
    }

    public Lexeme structDef(Lexeme env) throws IOException{
        Lexeme var, block;
        match(Type.STRUCT);
        block = block(env);
        var = match(Type.VARIABLE);
        match(Type.SEMICOLON);
        return cons(Type.STRUCT, var, block);
    }

    public Lexeme functionCall(Lexeme var, Lexeme env) throws IOException{
        Lexeme list;
        match(Type.OPAREN);
        list = optList(env);
        match(Type.CPAREN);
        return cons(Type.CALL, var, list);
    }

    private boolean functionCallPending(){
        return check(Type.VARIABLE);
    }

    private boolean arrayPending(){
        return check(Type.OBRACKET);
    }

    private boolean statementsPending(){
        return statPending();
    }

    private boolean forLoopPending(){
        return check(Type.FOR);
    }

    private boolean whileLoopPending(){
        return check(Type.WHILE);
    }

    private boolean ifStatPending(){
        return check(Type.IF);
    }

    private boolean optElsePending(){
        return check(Type.ELSE);
    }

    private boolean expressionsPending(){
        return unaryPending();
    }

    private boolean unaryPending(){
        return check(Type.MINUS) || lambdaPending() || check(Type.FILE) || check(Type.BOOLEAN) || check(Type.VARIABLE) || check(Type.NUMBER) || check(Type.OPAREN) || check(Type.STRING) || arrayPending() || functionCallPending();
    }

    private boolean functionDefPending(){
        return check(Type.FUNCTION);
    }

    private boolean lambdaPending(){
        return check(Type.LAMBDA);
    }

    private boolean varDefPending(){
        return check(Type.VAR);
    }

    private boolean blockPending(){
        return check(Type.OBRACE);
    }

    private boolean listPending(){
        return unaryPending();
    }

    private boolean varListPending(){
        return check(Type.VARIABLE);
    }

    private boolean statPending(){
        return forLoopPending() || whileLoopPending() || ifStatPending() || optElsePending() || expressionsPending() || functionDefPending() || structDefPending() || varDefPending();
    }

    private boolean structDefPending(){
        return check(Type.STRUCT);
    }

    private boolean opPending(){
        return check(Type.PLUS) || check(Type.MINUS) || check(Type.MULTIPLY) || check(Type.DIVISION) || check(Type.MODULO) || check(Type.GREATER_THAN) || check(Type.LESS_THAN) || check(Type.EQUAL) || check(Type.NOT) || check(Type.AND) || check(Type.OR) || check(Type.ASSIGN) || check(Type.DOT) || check(Type.NOT_EQUAL);
    }

    private boolean headerPending(){
        return check(Type.AT);
    }

    public void pp(Lexeme tree){
        if(tree == null){
            return;
        }
        switch(tree.t){
            case NUMBER: {System.out.print(tree.displayHelp());break;}
            case VARIABLE: {System.out.print(tree.c);break;}
            case STRING: {System.out.print(tree.c);break;}
            case BOOLEAN: {System.out.print(tree.c);break;}
            case LAMBDA: {System.out.print("lambda"); break;}
            case OPAREN: {
                System.out.print("(");
                pp(tree.right);
                System.out.print(")");
                break;
            }
            case PLUS: {
                pp(tree.left);
                System.out.print(" + ");
                pp(tree.right);
                break;
            }
            case MINUS:{
                pp(tree.left);
                System.out.print(" - ");
                pp(tree.right);
                break;
            }
            case DIVISION:{
                pp(tree.left);
                System.out.print(" / ");
                pp(tree.right);
                break;
            }
            case MULTIPLY:{
                pp(tree.left);
                System.out.print(" * ");
                pp(tree.right);
                break;
            }
            case MODULO:{
                pp(tree.left);
                System.out.print(" % ");
                pp(tree.right);
                break;
            }
            case GREATER_THAN:{
                pp(tree.left);
                System.out.print(" > ");
                pp(tree.right);
                break;
            }
            case LESS_THAN:{
                pp(tree.left);
                System.out.print(" < ");
                pp(tree.right);
                break;
            }
            case EQUAL:{
                pp(tree.left);
                System.out.print(" == ");
                pp(tree.right);
                break;
            }
            case NOT_EQUAL:{
                pp(tree.left);
                System.out.print(" + ");
                pp(tree.right);
                break;
            }
            case ASSIGN:{
                pp(tree.left);
                System.out.print(" = ");
                pp(tree.right);
                break;
            }
            case AND:{
                pp(tree.left);
                System.out.print(" & ");
                pp(tree.right);
                break;
            }
            case OR:{
                pp(tree.left);
                System.out.print(" | ");
                pp(tree.right);
                break;
            }
            case DOT:{
                pp(tree.left);
                System.out.print(".");
                pp(tree.right);
                break;
            }
            case PROGRAM:{
                pp(tree.right);
                break;
            }
            case OBRACE:{
                System.out.println("{");
                pp(tree.right);
                System.out.println("}");
                break;
            }
            case STAT:{
                pp(tree.left);
                pp(tree.right);
                break;
            }
            case HEADER:{
                System.out.print("@include ");
                pp(tree.right);
                System.out.println(";");
                break;
            }
            case FUNCTION:{
                System.out.println("function ");
                pp(tree.left);
                System.out.print("(");
                pp(tree.right.left);
                System.out.print(")");
                pp(tree.right.right);
                break;
            }
            case CALL:{
                pp(tree.left);
                System.out.print("(");
                pp(tree.right);
                System.out.print(");");
                break;
            }
            case LIST:{
                pp(tree.left);
                if(tree.right != null) {
                    System.out.print(", ");
                }
                pp(tree.right);
                break;
            }
            case VAR:{
                System.out.print("var ");
                pp(tree.right);
                if(tree.left != null){
                    System.out.print(" = ");
                    pp(tree.left);
                }
                System.out.println(";");
                break;
            }
            case WHILE:{
                System.out.print("while(");
                pp(tree.left);
                System.out.print(")");
                pp(tree.right);
                break;
            }
            case FOR:{
                System.out.print("for(");
                pp(tree.left);
                System.out.print(")");
                pp(tree.right);
                break;
            }
            case IF:{
                System.out.print("if(");
                pp(tree.left.left);
                System.out.print(")");
                pp(tree.left.right);
                pp(tree.right);
                break;
            }
            case ELSE:{
                System.out.print("else");
                pp(tree.right);
                break;
            }
            case OBRACKET:{
                System.out.print("[");
                pp(tree.right);
                System.out.print("]");
                break;
            }
            default: {
                System.out.println("BAD PRINT");
                break;
            }
        }
    }
}

