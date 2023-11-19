import java.util.regex.PatternSyntaxException;

%%// Options of the scanner

%class LexicalAnalyzer // Name
%unicode               // Use unicode
%line                  // Use line counter (yyline variable)
%column                // Use character counter by line (yycolumn variable)
%function nextToken
%type Symbol
%yylexthrow PatternSyntaxException

%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

//______________________________Extended Regular Expressions______________________________//

//_____Elemental bricks
AlphaLowerCase    = [a-z]
Numeric           = [0-9]

LowerAlphaNumeric	= {AlphaLowerCase}|{Numeric}
Dot               = "."
Dots              = {Dot}{Dot}{Dot}
Quotes            = "''"

//_____Advanced bricks
BadNumber     = (0[0-9]+)
Number        = ([1-9][0-9]*)|0
VarName        = ({AlphaLowerCase})({LowerAlphaNumeric})*
LineFeed       = "\n"
CarriageReturn = "\r"
EndLine        = ({LineFeed}{CarriageReturn}?) | ({CarriageReturn}{LineFeed}?)
Space          = (\t | \f | " ")
Spaces         = {Space}+

//______________________________Declare exclusive states/modes______________________________//
%xstate YYINITIAL, SHORTCOMMENTS, LONGCOMMENTS


//______________________________Identification of tokens______________________________//
%%

//_____SHORT COMMENTS MODE
<SHORTCOMMENTS> {
  // End of comment
  {EndLine}        {yybegin(YYINITIAL);} // go back to analysis
  .	   				     {} //ignore any character
}

//_____LONG COMMENTS MODE
<LONGCOMMENTS> {
  // End of comment
  {Quotes}         {yybegin(YYINITIAL);} // go back to analysis
  <<EOF>>          {throw new PatternSyntaxException("A comment is never closed.",yytext(),yyline);}
  .			           {} //ignore any character
}

//_____DEFAULT MODE
<YYINITIAL> {
// Comments
  "**"              {yybegin(SHORTCOMMENTS);} // go to ignore mode
  {Quotes}          {yybegin(LONGCOMMENTS); } // go to ignore mode

// Code Delimiters
  "begin"             {return new Symbol(LexicalUnit.BEG, yyline, yycolumn, yytext());}
  "end"               {return new Symbol(LexicalUnit.END, yyline, yycolumn, yytext());}

// Assignation
  ":="                {return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}

// Parenthesis
  "("                 {return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
  ")"                 {return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}

// Arithmetic signs
  "+"                 {return new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
  "-"                 {return new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
  "*"                 {return new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
  "/"                 {return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}

// Conditional keywords
  "if"                {return new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
  "then"              {return new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
  "else"              {return new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}

// Loop keywords
  "while"             {return new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
  "do"                {return new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext());}

// Comparison operators
  "="                 {return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
  "<"                 {return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}

// IO keywords
  "print"             {return new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext());}
  "read"              {return new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext());}

// Numbers
  {BadNumber}        {System.err.println("Warning! Numbers with leading zeros are not permitted: " + yytext()); return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, Integer.valueOf(yytext()));}
  {Number}           {return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, Integer.valueOf(yytext()));}

// Variable Names
  {VarName}           {return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());}

// Other
  {Dots}              {return new Symbol(LexicalUnit.DOTS, yyline, yycolumn, yytext());}
  {Spaces}	          {} // ignore spaces
  {EndLine}           {} // ignore endlines
  
// Unmatched symbols
  [^]                 {throw new PatternSyntaxException("Unmatched symbol(s) found.",yytext(),yyline);} // unmatched symbols gives an error
}
