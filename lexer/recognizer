JFLAGS = -g -Xlint
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Lexeme.java \
	Lexer.java \
	Main.java 

default: scanner

scanner: $(CLASSES:.java=.class)

.PHONY: scanner

test1: scanner
	./scanner test1
	
test2: scanner
	./scanner test2

test3: scanner
	./scanner test3

test4: scanner
	./scanner test4

test5: scanner
	./scanner test5

clean:
	        $(RM) *.class
