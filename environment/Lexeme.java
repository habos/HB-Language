public class Lexeme {

	public enum Type{
		ENV, TAB, ID, VAL, FUNCTION, VAR, AT, INCLUDE, NOT_EQUAL, ASSIGN, STRUCT, DOT, OPAREN, CPAREN, UNKNOWN, IF, FOR, WHILE, ELSE, VARIABLE, STRING, NUMBER, OBRACKET, CBRACKET, SEMICOLON, COMMA, ENDofINPUT, PLUS, MINUS, MULTIPLY, DIVISION, GREATER_THAN, LESS_THAN, MODULO, EQUAL, OBRACE, CBRACE, NOT, OR, AND;
	}

	public final Type t;
	public final String c;
	public final Integer i;
	public final Double d;
	public Lexeme right;
	public Lexeme left;

	public Lexeme(){
		this.t = null;
		this.c = null;
		this.d = null;
		this.i = null;
		this.right = null;
		this.left = null;
	}

	public Lexeme(Type t){
		this.t = t;
		this.c = null;
		this.d = null;
		this.i = null;
		this.right = null;
		this.left = null;
	}

	public Lexeme(Type t, String c, Integer i, Double d) {
		this.t = t;
		this.c = c;
		this.i = i;
		this.d = d;
		this.right = null;
		this.left = null;
	}

	public Lexeme(Type t, Lexeme l, Lexeme r){
		this.t = t;
		this.c = null;
		this.i = null;
		this.d = null;
		this.right = r;
		this.left = l;
	}

	public void display(){
		if(this.t == Type.UNKNOWN){
			System.out.println("ERROR " + this.t.toString() + " input " + this.c + " on line " + this.i.toString());
		}
		else if(this.i != null){
			System.out.println(this.t.toString() + " " + this.i.toString());
			return;
		}
		else if (this.d != null){
			System.out.println(this.t.toString() + " " + this.d.toString());
			return;
		}
		else if (this.t == Type.STRING || this.t == Type.VARIABLE){
			System.out.println(this.t.toString() + " " + this.c);
			return;
		}
		else{
			System.out.println(this.t.toString());
		}
	}

	public void displayEnv(Lexeme env){
		int envCount = 0;
		System.out.println("Environment");
		while(env != null) {
			envCount++;
			Lexeme table = car(env);
			Lexeme vars = car(table);
			Lexeme vals = cdr(table);
			System.out.println("Table " + envCount + ":");
			while (vars != null) {
				System.out.println(car(vars).displayHelp() + " = " + car(vals).displayHelp());
				vars = cdr(vars);
				vals = cdr(vals);
			}
			env = cdr(env);
		}
	}

	public String displayHelp(){
		if(this.t == Type.NUMBER){
			if(this.i != null) {
				return this.i.toString();
			}
			else{
				return this.d.toString();
			}
		}
		else{
			return this.c;
		}
	}

	public Lexeme car(Lexeme lexeme){
		return lexeme.left;
	}

	public Lexeme cdr(Lexeme lexeme){
		return lexeme.right;
	}

	public void setCar(Lexeme target, Lexeme val){
		target.left = val;
	}

	public void setCdr(Lexeme target, Lexeme val){
		target.right = val;
	}

	public boolean compareVars(Lexeme first, Lexeme second) {
		if (first.t == Type.VARIABLE && second.t == Type.VARIABLE) {
			if (first.c.equals(second.c)){
				return true;
			}
		}
		return false;
	}
}

