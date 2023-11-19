all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C bin .

testing:
	java -jar dist/part2.jar -wt tree.tex test/00-euclid.pmp
	pdflatex tree.tex > /dev/null

clean:
	rm -f src/LexicalAnalyzer.java
	rm -rf bin/*
	rm -f dist/*.jar
	rm -rf doc/javadoc/*

