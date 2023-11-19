import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * Project Part 1: Lexical Analyzer
 *
 * @author Marie Van Den Bogaard, Léo Exibard, Gilles Geeraerts
 *
 */
public class Main{
    /**
     *
     * The scanner
     *
     * @param args  The argument(s) given to the program
     *
     */
    public static void main(String[] args) {
        // Display the usage when the number of arguments is wrong (should be 1 or 3)
        if(args.length < 1){
            System.out.println("Usage:  java -jar Part2.jar -wt output_file.tex input_file.co\n"
                             + "or\tjava "+Main.class.getSimpleName()+" file.co");
            System.exit(0);
        }else if (args.length > 3){
            System.err.println("Too many arguments passed in command line");
            System.exit(0);
        }

        /*
          Initialisation of the different input and ouput variables files.
         */
        String texFile=null;
        String inputFile=null;
        FileReader source=null;

        /*
          Checks the arguments passed by command line.
         */
        for (int i = 0; i<args.length;i++){
            if (args[i].equals("-wt")){
                if (i+1<args.length){
                    texFile = args[i+1];
                    i++;
                }else{
                    System.err.println("no file have been submited with the -wt command");
                    System.exit(0);
                }
            }
            else{
                if (inputFile==null){
                    inputFile = args[i];
                }
            }
        }

        /*
          Checks if the input file has been updated, if no then it means no input file has been sent via command line and throws
          an error.
         */
        if (inputFile==null){
            System.err.println("The input file has not been set in command line.");
            System.exit(0);
        }
        
        /*
          Tries to open the input file
         */
        try{
            source = new FileReader(inputFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        /*
          Creates the parser and executes it.
         */
        final Parser parser = new Parser(source);
        ParseTree parseTree = parser.beginParsing();

        /*
          If the TexFile has been given by command line then we will try to write the parseTree into the texFile.
         */
        if (texFile!=null){
            try {
                FileWriter outPutFile = new FileWriter(texFile);
                outPutFile.write(parseTree.toLaTeX());
                outPutFile.close();
            }catch (IOException e){
                e.printStackTrace();    
            }
        }
    }
}