import java.io.IOException;

public class Main extends Recognizer{

	private static void scanner(String filename) throws IOException {
		Lexer i = new Lexer(filename);
		Lexeme token;

		token = i.Lex();
		while(token.t != Type.ENDofINPUT){
			token.display();
			token = i.Lex();
		}
	}

	private static void recognize(String filename) throws IOException{
		Recognizer r = new Recognizer(filename);
		r.curLex = r.l.Lex();
		r.program();
		if(r.curLex.t == Type.ENDofINPUT){
			System.out.println("valid");
		}
		else{
			System.out.print("Incorrect use of ");
			r.curLex.display();
			System.out.println(" on line " + (r.l.lineCount+1));
			System.out.println("invalid");
		}
	}


	public static void main(String[] args) throws IOException {
		if(args.length != 1){
			return;
		}
		recognize(args[0]);
	}
}

