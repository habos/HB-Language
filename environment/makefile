JFLAGS = -g -Xlint
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Lexeme.java \
	Environment.java \
	Main.java 

default: environment

environment: $(CLASSES:.java=.class)

.PHONY: environment

clean:
	        $(RM) *.class
