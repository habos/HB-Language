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
		Lexeme print = new Lexeme(Type.VARIABLE, "println", null, null);
		insert(print, null, env);
	}

}

