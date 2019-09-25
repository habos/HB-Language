import java.io.IOException;

public class Main extends Environment{
	/*
	 *     private static void scanner(String filename) throws IOException {
	 *             Lexer i = new Lexer(filename);
	 *                     Lexeme token;
	 *
	 *                             token = i.Lex();
	 *                                     while(token.t != Type.ENDofINPUT){
	 *                                                 token.display();
	 *                                                             token = i.Lex();
	 *                                                                     }
	 *                                                                         }
	 *
	 *                                                                             private static void recognize(String filename) throws IOException{
	 *                                                                                     Recognizer r = new Recognizer(filename);
	 *                                                                                             r.curLex = r.l.Lex();
	 *                                                                                                     r.program();
	 *                                                                                                             if(r.curLex.t == Type.ENDofINPUT){
	 *                                                                                                                         System.out.println("valid");
	 *                                                                                                                                 }
	 *                                                                                                                                         else{
	 *                                                                                                                                                     System.out.println("invalid");
	 *                                                                                                                                                             }
	 *                                                                                                                                                                 }
	 *                                                                                                                                                                 */
	private static void environment(){
		System.out.println("Creating environment");
		Environment e = new Environment();
		Lexeme env = e.create();
		e.displayEnv(env);
		System.out.println("Inserting values");
		Lexeme aVar = new Lexeme(Type.VARIABLE, "a", null, null);
		Lexeme aVal = new Lexeme(Type.NUMBER, null, 4, null);
		e.insert(aVar, aVal, env);
		Lexeme bVar = new Lexeme(Type.VARIABLE, "b", null, null);
		Lexeme bVal = new Lexeme(Type.STRING, "laurel", null, null);
		e.insert(bVar, bVal, env);
		e.displayEnv(env);
		System.out.println("Extending environment");
		Lexeme cVar = new Lexeme(Type.VARIABLE, "c", null, null);
		Lexeme cVal = new Lexeme(Type.STRING, "laurel is great", null, null);
		Lexeme vars = new Lexeme(Type.ID, cVar, null);
		Lexeme vals = new Lexeme(Type.VAL, cVal, null);
		env = e.extend(vars, vals, env);
		Lexeme dVar = new Lexeme(Type.VARIABLE, "d", null, null);
		Lexeme dVal = new Lexeme(Type.NUMBER,  null, null, 4.5);
		e.insert(dVar, dVal, env);
		e.displayEnv(env);
		System.out.println("Finding variable b:");
		Lexeme find = e.lookup(bVar, env);
		System.out.println("The value of b is: " + find.displayHelp());
		System.out.println("Finding variable e:");
		Lexeme eVar = new Lexeme(Type.VARIABLE, "e", null,null);
		find = e.lookup(eVar, env);

		System.out.println("Updating variable c");
		Lexeme newVal = new Lexeme(Type.STRING, "laurel is amazing", null, null);
		e.update(cVar, newVal, env);
		System.out.println(newVal.displayHelp());
		e.displayEnv(env);
	}


	public static void main(String[] args){
		environment();
	}
}

