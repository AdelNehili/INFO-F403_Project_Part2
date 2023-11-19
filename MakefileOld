all:
	jflex src/LexicalAnalyzer.flex
	mkdir -p bin
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part1.jar Main -C bin .
#	javadoc -private src/Main.java -d doc/javadoc

testing:
	java -jar dist/part1.jar test/euclid.pmp
	
testing1:
	java -jar dist/part1.jar test/test1.pmp
testing2:
	java -jar dist/part1.jar test/test2.pmp

clean:
	rm -f src/LexicalAnalyzer.java
	rm -rf bin/*
	rm -f dist/part1.jar
	rm -rf doc/javadoc/*
