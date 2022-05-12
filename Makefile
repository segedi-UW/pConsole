JC =javac
JFLAGS=-Xlint
lint=-Xlint

.SUFFIXES:.java .class

.java.class:
	@$(JC) $(JFLAGS) $*.java

CLASSES = \
		  Callable.java \
		  Cons.java \
		  LoadHandler.java \
		  Loader.java \
		  Main.java \
		  ManCall.java \
		  Prog.java \
		  ProgCall.java \
		  ANSIParser.java

default : classes

classes : $(CLASSES:.java=.class)

run : classes
	@java Main

clean :
	$(RM) *.class
