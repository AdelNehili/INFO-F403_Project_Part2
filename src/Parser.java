import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;


public class Parser {
    private Symbol token;
    private Symbol matched;
    private LexicalUnit tokenUnit;
    private final Lexer lexer;
    private ArrayList<Integer> leftMostD;

    public Parser(FileReader source){
        lexer = new Lexer(source);
    }
    
    /**
     * We check if there is nothing after the last token. 
     * We print the list of the left most derivative 
     * @return parseTree
     */
    public ParseTree beginParsing(){
        ParseTree parseTree = PROGRAM();
        getNextToken();
        if (tokenUnit!=LexicalUnit.END_OF_STREAM){
            System.err.println("Sorry but "+token.toString() + " is after lexical unit: " + LexicalUnit.END);
            System.exit(0);
        }
        StringBuilder correctPrint = new StringBuilder();
        for (Integer integer : leftMostD) {
            correctPrint.append(integer.toString()).append(" ");
        }
        System.out.println(correctPrint);
        return parseTree;
    }

    public ParseTree PROGRAM(){
        ArrayList<ParseTree> chdn = new ArrayList<>();
        getNextToken();
        switch (tokenUnit){
            case BEG:
                addLeftMostD(1);
                chdn.add(match(LexicalUnit.BEG));
                chdn.add(CODE()); 
                chdn.add(match(LexicalUnit.END));
                break;
            default:
                syntaxError(token); 
                break;
        }
        return new ParseTree(new Symbol("Program"), chdn);
    }

    private ParseTree CODE() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case READ: // Fall through
            case PRINT: // Fall through
            case IF: // Fall through
            case WHILE: // Fall through
            case BEG: // Fall through
            case VARNAME:
                addLeftMostD(1);
                children.add(INSTLIST());
                break;
            case END: // The 'end' token leads to an epsilon production
                addLeftMostD(3);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("Code"), children);
    }


    private ParseTree INSTLIST() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case BEG: // Fall through
            case PRINT: // Fall through
            case READ: // Fall through
            case IF: // Fall through
            case VARNAME: // Fall through
            case WHILE:
                addLeftMostD(3); // Adjusted for 'Instruction', 'DOTS', and 'InstList'
                children.add(INSTRUCTION());
                children.add(match(LexicalUnit.DOTS));
                children.add(INSTLIST());
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("InstList"), children);
    }

    private ParseTree INSTRUCTION() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case BEG:
                addLeftMostD(3); // 'begin', 'InstList', 'end'
                children.add(match(LexicalUnit.BEG));
                children.add(INSTLIST());
                children.add(match(LexicalUnit.END));
                break;
            case PRINT:
                addLeftMostD(1); // 'Print'
                children.add(PRINT());
                break;
            case VARNAME:
                addLeftMostD(1); // 'Assign'
                children.add(ASSIGN());
                break;
            case WHILE:
                addLeftMostD(1); // 'While'
                children.add(WHILE());
                break;
            case READ:
                addLeftMostD(1); // 'Read'
                children.add(READ());
                break;
            case IF:
                addLeftMostD(1); // 'If'
                children.add(IF());
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("Instruction"), children);
    }

    private ParseTree PRINT() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        if (tokenUnit == LexicalUnit.PRINT) {
            addLeftMostD(4); // 'print', '(', 'VarName', ')'
            children.add(match(LexicalUnit.PRINT));
            children.add(match(LexicalUnit.LPAREN)); // Left parenthesis
            children.add(VARNAME());
            children.add(match(LexicalUnit.RPAREN)); // Right parenthesis
        } else {
            syntaxError(token);
        }
        return new ParseTree(new Symbol("Print"), children);
    }

    private ParseTree READ() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        if (tokenUnit == LexicalUnit.READ) {
            addLeftMostD(4); // 'read', '(', 'VarName', ')'
            children.add(match(LexicalUnit.READ));
            children.add(match(LexicalUnit.LPAREN)); // Left parenthesis
            children.add(VARNAME());
            children.add(match(LexicalUnit.RPAREN)); // Right parenthesis
        } else {
            syntaxError(token);
        }
        return new ParseTree(new Symbol("Read"), children);
    }

    private ParseTree IF() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        if (tokenUnit == LexicalUnit.IF) {
            addLeftMostD(6); // 'if', 'Cond', 'then', 'Instruction', 'else', 'Instruction'
            children.add(match(LexicalUnit.IF));
            children.add(COND());
            children.add(match(LexicalUnit.THEN));
            children.add(INSTRUCTION());
            children.add(match(LexicalUnit.ELSE));
            children.add(INSTRUCTION());
        } else {
            syntaxError(token);
        }
        return new ParseTree(new Symbol("If"), children);
    }

    private ParseTree COND() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case NUMBER: // Fall through
            case MINUS: // Fall through
            case VARNAME: // Fall through
            case LPAREN: // Fall through
                addLeftMostD(1); // 'SimpleCond'
                children.add(SIMPLECOND());
                children.add(CONDTAIL());
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("Cond"), children);
    }

    private ParseTree SIMPLECOND() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case NUMBER: // Fall through
            case MINUS: // Fall through
            case VARNAME: // Fall through
            case LPAREN:
                addLeftMostD(3); // 'ExprArith', 'Comp', 'ExprArith'
                children.add(EXPRARITH());
                children.add(COMP());
                children.add(EXPRARITH());
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("SimpleCond"), children);
    }

    private ParseTree COMP() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case SMALLER: // Token for '<'
                addLeftMostD(1);
                children.add(match(LexicalUnit.SMALLER));
                break;
            case EQUAL: // Token for '='
                addLeftMostD(1);
                children.add(match(LexicalUnit.EQUAL));
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("Comp"), children);
    }

    private ParseTree EXPRARITH() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case NUMBER: // Fall through
            case MINUS: // Fall through
            case LPAREN: // Fall through
            case VARNAME:
                addLeftMostD(2); // 'SimpleExpr', 'ExprArithTail'
                children.add(SIMPLEEXPR());
                children.add(EXPRARITHTAIL());
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("ExprArith"), children);
    }

    private ParseTree EXPRARITHTAIL() {
        ArrayList<ParseTree> children = new ArrayList<>();
        peekNextToken();
        switch (tokenUnit) {
            case DIVIDE: // Fall through
            case TIMES: // Fall through
            case MINUS: // Fall through
            case PLUS:
                addLeftMostD(2); // 'Op', 'ExprArith'
                children.add(OP());
                children.add(EXPRARITH());
                break;
            case END: // Fall through
            case AND: // Fall through
            case SMALLER: // Fall through
            case OR: // Fall through
            case RBRACK: // Fall through
            case ELSE: // Fall through
            case THEN: // Fall through
            case LBRACK: // Fall through
            case RPAREN: // Fall through
            case DOTS: // Fall through
            case DO: // Fall through
            case EQUAL:
                addLeftMostD(3);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("ExprArithTail"), children);
    }



    private ParseTree CONDTAIL() {
        ArrayList<ParseTree> children = new ArrayList<>();
        peekNextToken();
        switch (tokenUnit) {
            case AND:
                addLeftMostD(2); // 'and', 'Cond'
                children.add(match(LexicalUnit.AND));
                children.add(COND());
                break;
            case OR:
                addLeftMostD(2); // 'or', 'Cond'
                children.add(match(LexicalUnit.OR));
                children.add(COND());
                break;
            case LBRACK:
                addLeftMostD(3); // '{', 'Cond', '}'
                children.add(match(LexicalUnit.LBRACK));
                children.add(COND());
                children.add(match(LexicalUnit.RBRACK));
                break;
            case RBRACK: // Fall through
            case THEN: // Fall through
            case DO:
                addLeftMostD(3);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("CondTail"), children);
    }

    private ParseTree SIMPLEEXPR() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        switch (tokenUnit) {
            case NUMBER:
                addLeftMostD(1); // 'Number'
                children.add(match(LexicalUnit.NUMBER));
                break;
            case MINUS:
                addLeftMostD(2); // '-', 'ExprArith'
                children.add(match(LexicalUnit.MINUS));
                children.add(EXPRARITH());
                break;
            case LPAREN:
                addLeftMostD(3); // '(', 'ExprArith', ')'
                children.add(match(LexicalUnit.LPAREN));
                children.add(EXPRARITH());
                children.add(match(LexicalUnit.RPAREN));
                break;
            case VARNAME:
                addLeftMostD(1); // 'VarName'
                children.add(match(LexicalUnit.VARNAME));
                break;
            default:
                syntaxError(token);
                break;
        }
        return new ParseTree(new Symbol("SimpleExpr"), children);
    }

    private ParseTree WHILE() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        if (tokenUnit == LexicalUnit.WHILE) {
            addLeftMostD(4); // 'while', 'Cond', 'do', 'Instruction'
            children.add(match(LexicalUnit.WHILE));
            children.add(COND());
            children.add(match(LexicalUnit.DO));
            children.add(INSTRUCTION());
        } else {
            syntaxError(token);
        }
        return new ParseTree(new Symbol("While"), children);
    }

    private ParseTree ASSIGN() {
        ArrayList<ParseTree> children = new ArrayList<>();
        getNextToken();
        if (tokenUnit == LexicalUnit.VARNAME) {
            addLeftMostD(3); // 'VarName', ':=', 'ExprArith'
            children.add(match(LexicalUnit.VARNAME));
            children.add(match(LexicalUnit.ASSIGN));
            children.add(EXPRARITH());
        } else {
            syntaxError(token);
        }
        return new ParseTree(new Symbol("Assign"), children);
    }
    
    /**__________________________________________________**/
    /**
     * Stores the type of the token in tokenUnit
     */
    private void convertToken(){
        tokenUnit = token.getType();
    }

    /**
     * Adds the rule number i to the list leftMostD. 
     * @param i the last rule that has been used.
     */
    private void addLeftMostD(int i) {
        if (leftMostD == null){
            leftMostD = new ArrayList<>();
        }
        leftMostD.add(i);
    }

    /**
     * Fetches the following token if matched is null or equals to the current token. 
     */
    private void getNextToken(){
        if (matched==null || matched.equals(token)){
            try{
                token = lexer.nextToken();
            } catch (IOException e){
                e.printStackTrace();
            }
            convertToken();
        }
    
    }

    /***
     * Launches an error if the token is not what was expected. 
     * Adds the token to the root of the parseTree. 
     * @param expected The expected lexicalUnit
     * @return root
     */
    private ParseTree match(LexicalUnit expected){
        if (matched!=null){getNextToken();}
        if (!expected.equals(tokenUnit)){
            syntaxError(token);
        }
        ParseTree root = new ParseTree(token);
        matched = token;
        return root;
    }

    /**
     * Launches an error and interrupts the code. 
     * @param symbol the symbol that generated the error.
     */
    private void syntaxError(Symbol symbol){
        System.err.println("An error occured when reading the token : " + symbol.getValue()+" at ligne : " + symbol.getLine());
        System.exit(1);
    }
}