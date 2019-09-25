/**
 * Scanner calls Lex() till end of file and displays to the console the Lexemes it finds.
 *
 * @author Harry Bos
 */
import java.io.IOException;

public class Main extends Lexer{

	private static void scanner(String filename) throws IOException {
		Lexer i = new Lexer(filename);
		Lexeme token;

		token = i.Lex();

		while(token.t != Type.ENDofINPUT){
			token.display();
			token = i.Lex();
		}
	}


	public static void main(String[] args) throws IOException {
		if(args.length != 1){
			return;
		}
		scanner(args[0]);
	}
}

