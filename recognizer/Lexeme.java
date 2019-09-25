/**
 *  * Lexeme class that holds the Type enum, constructors and a display function.
 *   *
 *    * @author Harry Bos
 *     */
public class Lexeme {

	public enum Type{
		FUNCTION, VAR, AT, INCLUDE, NOT_EQUAL, ASSIGN, STRUCT, DOT, OPAREN, CPAREN, UNKNOWN, IF, FOR, WHILE, ELSE, VARIABLE, STRING, NUMBER, OBRACKET, CBRACKET, SEMICOLON, COMMA, ENDofINPUT, PLUS, MINUS, MULTIPLY, DIVISION, GREATER_THAN, LESS_THAN, MODULO, EQUAL, OBRACE, CBRACE, NOT, OR, AND;
	}


	public final Type t;
	public final String c; // contents mainly for atom tokens
	public final Integer i;
	public final Double d;

	public Lexeme(){
		this.t = null;
		this.c = null;
		this.d = null;
		this.i = null;
	}

	public Lexeme(Type t){
		this.t = t;
		this.c = null;
		this.d = null;
		this.i = null;
	}

	public Lexeme(Type t, String c, Integer i, Double d) {
		this.t = t;
		this.c = c;
		this.i = i;
		this.d = d;
	}

	public void display(){
		if(this.t == Type.UNKNOWN){
			System.out.print("ERROR " + this.t.toString() + " input " + this.c + " on line " + this.i.toString());
		}
		else if(this.i != null){
			System.out.print(this.t.toString() + " " + this.i.toString());
			return;
		}
		else if (this.d != null){
			System.out.print(this.t.toString() + " " + this.d.toString());
			return;
		}
		else if (this.t == Type.STRING || this.t == Type.VARIABLE){
			System.out.print(this.t.toString() + " " + this.c);
			return;
		}
		else{
			System.out.print(this.t.toString());
		}
	}
}

