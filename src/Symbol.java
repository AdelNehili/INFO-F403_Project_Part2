public class Symbol{
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	public static final String NO_NAME = null;
	public static final LexicalUnit NO_UNIT = null;
	
	
	
	private final LexicalUnit type;
	private final Object value;
	private final int line,column;
	private final String name;

	public Symbol(LexicalUnit unit,int line,int column,Object value,String name){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
		this.name = name;
	}

	public Symbol(LexicalUnit unit,int line,int column,Object value){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
		this.name = NO_NAME;
	}
	
	public Symbol(LexicalUnit unit,int line,int column){
		this(unit,line,column,NO_VALUE,NO_NAME);
	}
	
	public Symbol(LexicalUnit unit,int line){
		this(unit,line,UNDEFINED_POSITION,NO_VALUE,NO_NAME);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE,NO_NAME);
	}
	
	public Symbol(String name){
		this(NO_UNIT,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE,name);
	}


	public Symbol(LexicalUnit unit,Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value);
	}

	public boolean isTerminal(){
		return this.type != null;
	}
	
	public boolean isNonTerminal(){
		return this.type == null;
	}
	
	public LexicalUnit getType(){
		return this.type;
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}

 	public String toTexString() {
        // Implement conversion of this Symbol to a TeX string format
        // This is an example; adjust according to your requirements:
        if (this.type != null) {
            return this.type.toString(); // Convert the type or value to a TeX string
        } else {
            return "NonTerminal"; // or some other representation for non-terminals
        }
    }
	@Override
	public int hashCode(){
		final String value	= this.value != null? this.value.toString() : "null";
		final String type		= this.type  != null? this.type.toString()  : "null";
		return new String(value+"_"+type).hashCode();
	}
	
	@Override
	public String toString(){
		if(this.isTerminal()){
			final String value	= this.value != null? this.value.toString() : "null";
			final String type		= this.type  != null? this.type.toString()  : "null";
			return "token: "+value+"\tlexical unit: "+type;
		}
		return "Non-terminal symbol";
	}
}
