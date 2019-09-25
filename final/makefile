JFLAGS = -g -Xlint
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Lexeme.java \
	Lexer.java \
	Environment.java \
	Parser.java \
	Evaluator.java \
	Main.java 

default: run

run: $(CLASSES:.java=.class)

.PHONY: run

error1: run
	cat error1

error1x: run
	./run error1x

error2: run
	cat error2

error2x: run
	./run error2x

error3: run
	cat error3

error3x: run
	./run error3x

error4: run
	cat error4

error4x: run
	./run error4x

error5: run
	cat error5

error5x: run
	./run error5x

arrays: run
	cat arrays

arraysx: run
	./run arraysx

conditionals: run
	cat conditionals

conditionalsx: run
	./run conditionalsx

recursion: run
	cat recursion

recursionx: run
	./run recursionx

iteration: run
	cat iteration

iterationx: run
	./run iterationx

functions: run
	cat functions

functionsx: run
	./run functionsx

lambda: run
	cat lambda

lambdax: run
	./run lambdax

objects: run
	cat objects

objectsx: run
	./run objectsx

problem: run
	cat findSum

problemx: run
	./run findSum commands

clean:
	        $(RM) *.class
