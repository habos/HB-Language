/**
 *  * Lex() function and helper functions take a character and determine the type using Lexemes.
 *   *
 *    *@author Harry Bos
 *     */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;


public class Lexer extends Lexeme {
	public final String filename;
	public final PushbackReader input;
	public int lineCount;

	public Lexer(){
		this.filename = null;
		this.input = null;
		this.lineCount = 0;
	}

	public Lexer(String filename) throws FileNotFoundException {
		this.filename = filename;
		input = new PushbackReader(new FileReader(filename));
		this.lineCount = 0;
	}

	public Lexeme Lex() throws IOException {
		char ch;
		if(checkEnd()){
			return new Lexeme(Type.ENDofINPUT);
		}
		skipWhiteSpace();
		ch = (char)this.input.read();
		char c = ' ';
		switch (ch) {
			case '(':
				return new Lexeme(Type.OPAREN);
			case ')':
				return new Lexeme(Type.CPAREN);
			case ',':
				return new Lexeme(Type.COMMA);
			case '+':
				return new Lexeme(Type.PLUS); //what about ++ and += ?
			case '*':
				return new Lexeme(Type.MULTIPLY);
			case '-':
				return new Lexeme(Type.MINUS);
			case '/':
				return new Lexeme(Type.DIVISION);
			case '<':
				return new Lexeme(Type.GREATER_THAN);
			case '>':
				return new Lexeme(Type.LESS_THAN);
			case '=':
				c = (char)this.input.read();
				if(c == '=') {
					return new Lexeme(Type.EQUAL);
				} else {
					this.input.unread((int) c);
					return new Lexeme(Type.ASSIGN);
				}
			case ';':
				return new Lexeme(Type.SEMICOLON);
			case '&':
				return new Lexeme(Type.AND);
			case '|':
				return new Lexeme(Type.OR);
			case '!':
				c = (char)this.input.read();
				if(c == '=') {
					return new Lexeme(Type.NOT_EQUAL);
				} else {
					this.input.unread((int) c);
					return new Lexeme(Type.NOT);
				}
			case '%':
				return new Lexeme(Type.MODULO);
			case '[':
				return new Lexeme(Type.OBRACKET);
			case ']':
				return new Lexeme(Type.CBRACKET);
			case '{':
				return new Lexeme(Type.OBRACE);
			case '}':
				return new Lexeme(Type.CBRACE);
			case '.':
				return new Lexeme(Type.DOT);
			case '@':
				return new Lexeme(Type.AT);
			default:
				if (Character.isDigit(ch)) {
					this.input.unread((int) ch);
					return lexNumber();
				} else if (Character.isLetter(ch)) {
					this.input.unread((int) ch);
					return lexVariableOrKeyword();
				} else if (ch == '\"') {
					return lexString();
				} else
					if(checkEnd()){
						return new Lexeme(Type.ENDofINPUT);
					}
				return new Lexeme(Type.UNKNOWN, Character.toString(ch), lineCount, null);
		}
	}

	private void skipWhiteSpace() throws IOException {
		char ch = (char) this.input.read();

		while (Character.isWhitespace(ch) || ch == '#') {
			if(ch == '#') {
				ch = ' ';
				while (ch != '#') {
					ch = (char) this.input.read();
				}
			}
			if(ch == '\n'){
				lineCount++;
			}
			ch = (char) this.input.read();
		}
		this.input.unread((int) ch);
	}

	private Lexeme lexVariableOrKeyword() throws IOException {
		char ch;
		String token = "";

		ch = (char) this.input.read();

		while (Character.isLetter(ch) || Character.isDigit(ch)) {
			token = token + ch;
			ch = (char) this.input.read();
		}
		this.input.unread((int) ch);

		if (token.equals("if")) {
			return new Lexeme(Type.IF);
		} else if (token.equals("else")) {
			return new Lexeme(Type.ELSE);
		} else if (token.equals("while")) {
			return new Lexeme(Type.WHILE);
		} else if (token.equals("for")) {
			return new Lexeme(Type.FOR);
		} else if (token.equals("struct")) {
			return new Lexeme(Type.STRUCT);
		} else if (token.equals("var")) {
			return new Lexeme(Type.VAR);
		} else if (token.equals("function")) {
			return new Lexeme(Type.FUNCTION);
		} else if (token.equals("include")){
			return new Lexeme(Type.INCLUDE);
		} else {
			return new Lexeme(Type.VARIABLE, token, null, null);
		}
	}

	private Lexeme lexNumber() throws IOException {
		char ch = (char) this.input.read();
		boolean isDouble = false;
		String token = "";

		while (Character.isDigit(ch) || ch == '.') {
			if (ch == '.') {
				isDouble = true;
			}
			token = token + ch;
			ch = (char) this.input.read();
		}



		this.input.unread((int) ch);

		if(Character.isLetter(ch)){
			token = token + lexVariableOrKeyword().c;
			return new Lexeme(Type.UNKNOWN, token, lineCount, null);
		}

		if (isDouble) {
			double num = Double.parseDouble(token);
			return new Lexeme(Type.NUMBER, null, null, num);
		} else {
			int num = Integer.parseInt(token);
			return new Lexeme(Type.NUMBER, null, num, null);
		}
	}

	private Lexeme lexString() throws IOException {
		char ch = (char) this.input.read();
		String token = "";

		while (ch != '\"') {
			token = token + ch;
			ch = (char) this.input.read();
		}

		return new Lexeme(Type.STRING, token, null, null);
	}

	private boolean checkEnd() throws IOException {
		int r = this.input.read();
		if(r == -1){
			System.out.println(Type.ENDofINPUT.toString());
			return true;
		}
		else {
			this.input.unread(r);
			return false;
		}
	}
}

