

import java.io.IOException;

public class Main extends Parser{
	public static void main(String[] args) throws IOException {
		Parser r = new Parser(args[0]);
		Lexeme env = r.e.create();
		r.e.insertBuiltIns(env);
		r.curLex = r.l.Lex();
		Lexeme p = r.program(env);
		if(r.curLex.t == Type.ENDofINPUT){
			System.out.println("valid");
		}
		else{
			System.out.println("invalid");
		}
		r.pp(p);
	}
}
