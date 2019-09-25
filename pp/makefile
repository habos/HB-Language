JFLAGS = -g -Xlint
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Lexeme.java \
	Lexer.java \
	Main.java \
	Environment.java \
	Parser.java

default: parser

parser: $(CLASSES:.java=.class)

.PHONY: parser

clean:
	        $(RM) *.class
