import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.TreeMap;
import java.util.Map;

/**
 *
 * Project Part 1: Lexical Analyzer
 *
 * @author Sarah Winter, Marie Van Den Bogaard, Léo Exibard, Gilles Geeraerts
 *
 */

public class Main{  
    /**
     *
     * The scanner
     *
     * @param args  The argument(s) given to the program
     * @throws IOException java.io.IOException if an I/O-Error occurs
     * @throws FileNotFoundException java.io.FileNotFoundException if the specified file does not exist
     *
    */

    //Main Method
    public static void main(String[] args) throws FileNotFoundException, IOException, SecurityException{
        // Display the usage when the number of arguments is wrong (should be 1)
        if(args.length != 1){
            System.out.println("Usage:  java -jar part1.jar file.fs\n"
                             + "or\tjava "+Main.class.getSimpleName()+" file.fs");
            System.exit(0);
        }

        
        FileReader source = new FileReader(args[0]); // Open the file given in argument
        final LexicalAnalyzer analyzer = new LexicalAnalyzer(source); //The lexer generated by <a href="http://www.jflex.de/">JFlex</a>

        TreeMap<String,Symbol> variablesTable = new TreeMap<String,Symbol>(); //The variables, sorted in a TreeMap, which maps the name of the variable to its corresponding Symbol.
        
        Symbol symbol = null; // symbol represents the currently read symbol
        
        // We iterate while we do not reach the end of the file (EOS)
        while(!(symbol = analyzer.nextToken()).getType().equals(LexicalUnit.EOS)){
            
            System.out.println(symbol.toString());

            // If it is a variable, add it to the table
            //if(symbol.getType().equals(LexicalUnit.VARNAME) || symbol.getType().equals(LexicalUnit.BEG)){
            if(symbol.getType().equals(LexicalUnit.VARNAME)){
                if(!variablesTable.containsKey(symbol.getValue())){
                    variablesTable.put(symbol.getValue().toString(),symbol);
                }
            }
        }

        System.out.println("\nVariables");
        // Print the variables
        for(Map.Entry<String, Symbol> variable : variablesTable.entrySet())
            //System.out.println(variable.getKey()+",\t first encoureted line: "+(variable.getValue().getLine()));
            System.out.println(variable.getKey()+"\t"+(variable.getValue().getLine()));
    }
}
