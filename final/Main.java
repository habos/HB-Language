import java.io.IOException;

public class Main extends Evaluator{
    public static void main(String[] args) throws IOException {

        Parser pars = new Parser(args[0]);
        Lexeme env = pars.e.create();
        pars.e.insertBuiltIns(env);
        pars.curLex = pars.l.Lex();
        Lexeme p = pars.program(env);
        

        Evaluator eval = new Evaluator();
        eval.argCount = args.length;
        eval.args = args;
        Lexeme env2 = eval.e.create();
        eval.eval(p, env2);
    }
}

