import java.io.FileNotFoundException;
import java.io.IOException;

public class Recognizer extends Lexer{
	public Lexeme curLex;
	public Lexer l;

	public Recognizer(String filename) throws FileNotFoundException {
		this.l = new Lexer(filename);
	}

	public Recognizer(){
		this.curLex = null;
	}

	private boolean check(Lexeme.Type type){
		return this.curLex.t == type;
	}

	private void advance() throws IOException {
		this.curLex = this.l.Lex();
	}

	private void match(Lexeme.Type type) throws IOException{
		matchNoAdvance(type);
		advance();
	}

	private void matchNoAdvance(Lexeme.Type type){
		if(this.curLex.t == Type.ENDofINPUT){
			System.out.println("valid");
			System.exit(0);
		}
		if(!check(type)){
			System.out.print("Incorrect use of ");
			this.curLex.display();
			System.out.println(" on line " + (this.l.lineCount+1));
			System.out.println("invalid");
			System.exit(0);
		}
	}

	public void unary() throws IOException{
		if(check(Type.VARIABLE)) {
			match(Type.VARIABLE);
			if(check(Type.OPAREN)){
				functionCall();
			}
		} else if(check(Type.NUMBER)){
			match(Type.NUMBER);
		} else if(check(Type.STRING)){
			match(Type.STRING);
		} else if(opPending()){
			op();
		} else if(arrayPending()){
			array();
		}else {
			match(Type.OPAREN);
			expressions();
			match(Type.CPAREN);
		}
	}

	public void program() throws IOException{
		statements();
	}

	public void block() throws IOException{
		match(Type.OBRACE);
		statements();
		match(Type.CBRACE);
	}

	public void statements() throws IOException{
		stat();
		if(statementsPending()){
			statements();
		}
	}

	public void header() throws IOException{
		match(Type.AT);
		match(Type.INCLUDE);
		match(Type.VARIABLE);
		match(Type.SEMICOLON);
	}

	public void op() throws IOException{
		if(check(Type.PLUS)){
			match(Type.PLUS);
		} else if(check(Type.MINUS)){
			match(Type.MINUS);
		} else if(check(Type.MULTIPLY)){
			match(Type.MULTIPLY);
		} else if(check(Type.DIVISION)){
			match(Type.DIVISION);
		} else if(check(Type.MODULO)){
			match(Type.MODULO);
		} else if(check(Type.GREATER_THAN)){
			match(Type.GREATER_THAN);
		} else if(check(Type.LESS_THAN)){
			match(Type.LESS_THAN);
		} else if(check(Type.EQUAL)){
			match(Type.EQUAL);
		} else if(check(Type.NOT)){
			match(Type.NOT);
		} else if(check(Type.AND)){
			match(Type.AND);
		} else if(check(Type.OR)){
			match(Type.OR);
		} else if(check(Type.DOT)) {
			match(Type.DOT);
		} else if(check(Type.NOT_EQUAL)){
			match(Type.NOT_EQUAL);
		} else{
			match(Type.ASSIGN);
		}
	}

	public void expressions() throws IOException{
		unary();
		if(opPending()){
			op();
			expressions();
		}
	}

	public void stat() throws IOException{
		if(forLoopPending()){
			forLoop();
		} else if(whileLoopPending()){
			whileLoop();
		} else if(ifStatPending()) {
			ifStat();
		} else if(optElsePending()){
			optElse();
		} else if(expressionsPending()){
			expressions();
			match(Type.SEMICOLON);
		} else if(functionDefPending()){
			functionDef();
		} else if(varDefPending()) {
			varDef();
		} else if(headerPending()) {
			header();
		} else
			structDef();
	}

	public void whileLoop() throws IOException{
		match(Type.WHILE);
		match(Type.OPAREN);
		expressions();
		match(Type.CPAREN);
		block();
	}

	public void forLoop() throws IOException{
		match(Type.FOR);
		match(Type.OPAREN);
		stat();
		stat();
		stat();
		match(Type.CPAREN);
		block();
	}

	public void ifStat() throws IOException{
		match(Type.IF);
		match(Type.OPAREN);
		expressions();
		match(Type.CPAREN);
		block();
		optElse();
	}

	public void optElse() throws IOException{
		if(check(Type.ELSE)){
			if(blockPending()){
				block();
			}else{
				ifStat();
			}
		}
	}

	public void array() throws IOException{
		match(Type.OBRACKET);
		list();
		match(Type.CBRACKET);
	}

	public void list() throws IOException{
		unary();
		if(check(Type.COMMA)){
			match(Type.COMMA);
			list();
		}
	}

	public void optList() throws IOException{
		if(listPending()){
			list();
		}
	}

	public void varList() throws IOException{
		match(Type.VARIABLE);
		if(check(Type.COMMA)){
			match(Type.COMMA);
			varList();
		}
	}

	public void optVarList() throws IOException{
		if(varListPending()){
			varList();
		}
	}

	public void varDef() throws IOException{
		match(Type.VAR);
		match(Type.VARIABLE);
		if(check(Type.SEMICOLON)){
			match(Type.SEMICOLON);
		} else {
			match(Type.ASSIGN);
			expressions();
			match(Type.SEMICOLON);
		}
	}

	public void functionDef() throws IOException{
		match(Type.FUNCTION);
		match(Type.VARIABLE);
		match(Type.OPAREN);
		optVarList();
		match(Type.CPAREN);
		block();
	}

	public void structDef() throws IOException{
		match(Type.STRUCT);
		block();
		match(Type.VARIABLE);
		match(Type.SEMICOLON);
	}

	public void functionCall() throws IOException{
		match(Type.OPAREN);
		optList();
		match(Type.CPAREN);
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
		return check(Type.VARIABLE) || check(Type.NUMBER) || check(Type.OPAREN) || check(Type.STRING) || arrayPending() || functionCallPending();
	}

	private boolean functionDefPending(){
		return check(Type.FUNCTION);
	}

	private boolean varDefPending(){
		return check(Type.VAR);
	}

	private boolean blockPending(){
		return check(Type.OBRACKET);
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
}

