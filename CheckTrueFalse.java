import java.io.*;
import java.util.*;
import java.util.Map.Entry;
/*
  @author Vineeth
 */
public class CheckTrueFalse {
	static HashSet<String> list_symbols = new HashSet<String>();
	public static LogicalExpression knowledge_base = new LogicalExpression();
	public static LogicalExpression statement1 = new LogicalExpression();
	public static LogicalExpression statement2 = new LogicalExpression();
	public static TTEntails ttEntails = new TTEntails();
	public static TTEntails.Model model = ttEntails.new Model();
	public static String buffer;
	public static BufferedReader inputStream;
	public static BufferedWriter outputStream;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if( args.length != 3){
			//takes three arguments
			System.out.println("Usage: " + args[0] +  " [wumpus-rules-file] [additional-knowledge-file] [input_file]\n");
			exit_function(0);
		}

		//create some buffered IO streams
		//open the wumpus_rules.txt
		try {
			inputStream = new BufferedReader( new FileReader( args[0] ) );

			//load the wumpus rules
			System.out.println("loading the wumpus rules...");
			knowledge_base.setConnective("and");

			while(  ( buffer = inputStream.readLine() ) != null )
			{
				if( !(buffer.startsWith("#") || (buffer.equals( "" )) ))
				{
					//the line is not a comment
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				}
				else
				{
					//the line is a comment. do nothing and read the next line
				}
			}

			//close the input file
			inputStream.close();

		} catch(Exception e)
		{
			System.out.println("failed to open " + args[0] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading wumpus rules

		//read the additional knowledge file
		try {
			inputStream = new BufferedReader( new FileReader( args[1] ) );

			//load the additional knowledge
			System.out.println("loading the additional knowledge...");

			// the connective for knowledge_base is already set.  no need to set it again.
			// i might want the LogicalExpression.setConnective() method to check for that
			//knowledge_base.setConnective("and");

			while(  ( buffer = inputStream.readLine() ) != null)
			{
				if( !(buffer.startsWith("#") || (buffer.equals("") )))
				{
					splitbuffer();
					LogicalExpression subExpression = readExpression( buffer );
					knowledge_base.setSubexpression( subExpression );
				}
				else
				{
					//the line is a comment. do nothing and read the next line
				}
			}

			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[1] );
			e.printStackTrace();
			exit_function(0);
		}
		//end reading additional knowledge

		// check for a valid knowledge_base
		if( !valid_expression( knowledge_base ) ) {
			System.out.println("invalid knowledge base");
			exit_function(0);
		}

		// print the knowledge_base
		knowledge_base.print_expression("\n");


		// read the statement file
		try {
			inputStream = new BufferedReader( new FileReader( args[2] ) );

			System.out.println("\n\nLoading the statement file...");
			//buffer = inputStream.readLine();

			// actually read the statement file
			// assuming that the statement file is only one line long
			while( ( buffer = inputStream.readLine() ) != null ) {
				if( !buffer.startsWith("#") ) {
					//the line is not a comment
					checkstatement();
					break;
				} else {
					//the line is a commend. no nothing and read the next line
				}
			}

			//close the input file
			inputStream.close();

		} catch(Exception e) {
			System.out.println("failed to open " + args[2] );
			e.printStackTrace();
			exit_function(0);
		}
		// end reading the statement file

		// check for a valid statement
		if( !valid_expression( statement1 ) ) {
			System.out.println("invalid statement");
			exit_function(0);
		}
		//print the statement
		System.out.println("Statement1:");
		statement1.print_expression( "" );
		System.out.println("\n");
		System.out.println("Statement2:");
		statement2.print_expression( "" );
		//print a new line
		System.out.println("\n");
		calculateTrueorFalse();
	} // END OF MAIN FUNCTION
	public static void splitbuffer(){

		String tempbuffer = buffer;
		if(!tempbuffer.contains("not")) {
			tempbuffer = tempbuffer.trim();
			model.hashMap.put(tempbuffer, true);
		}
		else {
			String[] tempsplit = tempbuffer.split(" ");
			tempsplit[1] = tempsplit[1].substring(0, tempsplit[1].length()-1);
			model.hashMap.put(tempsplit[1], false);
		}
	}
	private static void LogicSym(LogicalExpression logicalstatement) {
		int i=0;
		if((logicalstatement.getUniqueSymbol() == null))
			while(i >= 0 && i < logicalstatement.getSubexpressions().size()) {
				LogicalExpression logicalstatement1 = logicalstatement.getSubexpressions().get(i);
				LogicSym(logicalstatement1);
				if(!(logicalstatement1.getUniqueSymbol() == null))
					list_symbols.add(logicalstatement1.getUniqueSymbol());
				i++;
		}
		else {
				list_symbols.add(logicalstatement.getUniqueSymbol());
			}

	}
	public static void checkstatement(){
		String str1 = "", str2 = "";
		Set<String> unique_Set_Symbol = list_symbols;
		if(!buffer.contains("not")) {
			str1 = buffer;
			str2 = "(not " + buffer + ")";
		}
		else {
			str1 = buffer;
			String[] split_buffer = buffer.split(" ");
			str2 = split_buffer[1].substring(split_buffer[1].length() - 1);

		}
		statement1 = readExpression( str1 );
		statement2 = readExpression( str2 );
		if (valid_expression(statement1)&& !is_Valid_Input(str1, unique_Set_Symbol)) {
			System.out.println("\n INVALID Statement");
			return;
		}
	}
	private static boolean is_Valid_Input(String str, Set<String> set) {
		Iterator<String> itr = set.iterator();
		boolean bool = false;
		while(itr.hasNext()) {
			if(itr.next().equals(str))
				bool = true;
		}
		if(str.contains("(or") || str.contains("(and") || str.contains("(xor") || str.contains("(not") || str.contains("(if")	|| str.contains("(iff"))
			bool = true;
		return bool;
	}



	/* this method reads logical expressions
	 * if the next string is a:
	 * - '(' => then the next 'symbol' is a subexpression
	 * - else => it must be a unique_symbol
	 *
	 * it returns a logical expression
	 *
	 * notes: i'm not sure that I need the counter
	 *
	 */
	public static LogicalExpression readExpression( String input_string )
	{
		LogicalExpression result = new LogicalExpression();

		//testing
		//System.out.println("readExpression() beginning -"+ input_string +"-");
		//testing
		//System.out.println("\nread_exp");

		//trim the whitespace off
		input_string = input_string.trim();

		if( input_string.startsWith("(") )
		{
			//its a subexpression

			String symbolString = "";

			// remove the '(' from the input string
			symbolString = input_string.substring( 1 );
			//symbolString.trim();

			//testing
			//System.out.println("readExpression() without opening paren -"+ symbolString + "-");

			if( !symbolString.endsWith(")" ) )
			{
				// missing the closing paren - invalid expression
				System.out.println("missing ')' !!! - invalid expression! - readExpression():-" + symbolString );
				exit_function(0);

			}
			else
			{
				//remove the last ')'
				//it should be at the end
				symbolString = symbolString.substring( 0 , ( symbolString.length() - 1 ) );
				symbolString.trim();

				//testing
				//System.out.println("readExpression() without closing paren -"+ symbolString + "-");

				// read the connective into the result LogicalExpression object
				symbolString = result.setConnective( symbolString );

				//testing
				//System.out.println("added connective:-" + result.getConnective() + "-: here is the string that is left -" + symbolString + "-:");
				//System.out.println("added connective:->" + result.getConnective() + "<-");
			}

			//read the subexpressions into a vector and call setSubExpressions( Vector );
			result.setSubexpressions( read_subexpressions( symbolString ) );

		}
		else
		{
			// the next symbol must be a unique symbol
			// if the unique symbol is not valid, the setUniqueSymbol will tell us.
			result.setUniqueSymbol( input_string );

			//testing
			//System.out.println(" added:-" + input_string + "-:as a unique symbol: readExpression()" );
		}

		return result;
	}
	public static void calculateTrueorFalse(){
		boolean output1 = ttEntails.ttEntails(knowledge_base, statement1, model);
		boolean output2 = ttEntails.ttEntails(knowledge_base, statement2, model);

		try {
			outputStream = new BufferedWriter(new FileWriter(new File("result.txt")));
			if (output1 == output2 && output1 == false) {
				System.out.println(" Possibly TRUE, Possibly FALSE");
				outputStream.write(" Possibly TRUE, Possibly FALSE");
			}
			else if (output1 != output2) {
				System.out.println(" Definitely " + output1);
				outputStream.write(" Definitely " + output1);
			} else if (output1 == output2 && output1 == true) {
				System.out.println(" Both True and False");
				outputStream.write(" Both True and False");
			}
			outputStream.close();
		} catch (IOException e) {
			System.out.println("Error Message : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/* this method reads in all of the unique symbols of a subexpression
	 * the only place it is called is by read_expression(String, long)(( the only read_expression that actually does something ));
	 *
	 * each string is EITHER:
	 * - a unique Symbol
	 * - a subexpression
	 * - Delineated by spaces, and paren pairs
	 *
	 * it returns a vector of logicalExpressions
	 *
	 *
	 */

	public static Vector<LogicalExpression> read_subexpressions( String input_string ) {

		Vector<LogicalExpression> symbolList = new Vector<LogicalExpression>();
		LogicalExpression newExpression;// = new LogicalExpression();
		String newSymbol = new String();

		//testing
		//System.out.println("reading subexpressions! beginning-" + input_string +"-:");
		//System.out.println("\nread_sub");

		input_string.trim();

		while( input_string.length() > 0 ) {

			newExpression = new LogicalExpression();

			//testing
			//System.out.println("read subexpression() entered while with input_string.length ->" + input_string.length() +"<-");

			if( input_string.startsWith( "(" ) ) {
				//its a subexpression.
				// have readExpression parse it into a LogicalExpression object

				//testing
				//System.out.println("read_subexpression() entered if with: ->" + input_string + "<-");

				// find the matching ')'
				int parenCounter = 1;
				int matchingIndex = 1;
				while( ( parenCounter > 0 ) && ( matchingIndex < input_string.length() ) ) {
					if( input_string.charAt( matchingIndex ) == '(') {
						parenCounter++;
					} else if( input_string.charAt( matchingIndex ) == ')') {
						parenCounter--;
					}
					matchingIndex++;
				}

				// read untill the matching ')' into a new string
				newSymbol = input_string.substring( 0, matchingIndex );

				//testing
				//System.out.println( "-----read_subExpression() - calling readExpression with: ->" + newSymbol + "<- matchingIndex is ->" + matchingIndex );

				// pass that string to readExpression,
				newExpression = readExpression( newSymbol );

				// add the LogicalExpression that it returns to the vector symbolList
				symbolList.add( newExpression );

				// trim the logicalExpression from the input_string for further processing
				input_string = input_string.substring( newSymbol.length(), input_string.length() );

			} else {
				//its a unique symbol ( if its not, setUniqueSymbol() will tell us )

				// I only want the first symbol, so, create a LogicalExpression object and
				// add the object to the vector

				if( input_string.contains( " " ) ) {
					//remove the first string from the string
					newSymbol = input_string.substring( 0, input_string.indexOf( " " ) );
					input_string = input_string.substring( (newSymbol.length() + 1), input_string.length() );

					//testing
					//System.out.println( "read_subExpression: i just read ->" + newSymbol + "<- and i have left ->" + input_string +"<-" );
				} else {
					newSymbol = input_string;
					input_string = "";
				}

				//testing
				//System.out.println( "readSubExpressions() - trying to add -" + newSymbol + "- as a unique symbol with ->" + input_string + "<- left" );

				newExpression.setUniqueSymbol( newSymbol );

				//testing
				//System.out.println("readSubexpression(): added:-" + newSymbol + "-:as a unique symbol. adding it to the vector" );

				symbolList.add( newExpression );

				//testing
				//System.out.println("read_subexpression() - after adding: ->" + newSymbol + "<- i have left ->"+ input_string + "<-");

			}

			//testing
			//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-beforeTrim end of while");

			input_string.trim();

			if( input_string.startsWith( " " )) {
				//remove the leading whitespace
				input_string = input_string.substring(1);
			}

			//testing
			//System.out.println("read_subExpression() - left to parse ->" + input_string + "<-afterTrim with string length-" + input_string.length() + "<- end of while");
		}
		return symbolList;
	}


	/* this method checks to see if a logical expression is valid or not
	 * a valid expression either:
	 * ( this is an XOR )
	 * - is a unique_symbol
	 * - has:
	 *  -- a connective
	 *  -- a vector of logical expressions
	 *
	 * */
	public static boolean valid_expression(LogicalExpression expression)
	{

		// checks for an empty symbol
		// if symbol is not empty, check the symbol and
		// return the truthiness of the validity of that symbol

		if ( !(expression.getUniqueSymbol() == null) && ( expression.getConnective() == null ) ) {
			// we have a unique symbol, check to see if its valid
			return valid_symbol( expression.getUniqueSymbol() );

			//testing
			//System.out.println("valid_expression method: symbol is not empty!\n");
		}

		// symbol is empty, so
		// check to make sure the connective is valid

		// check for 'if / iff'
		if ( ( expression.getConnective().equalsIgnoreCase("if") )  ||
				( expression.getConnective().equalsIgnoreCase("iff") ) ) {

			// the connective is either 'if' or 'iff' - so check the number of connectives
			if (expression.getSubexpressions().size() != 2) {
				System.out.println("error: connective \"" + expression.getConnective() +
						"\" with " + expression.getSubexpressions().size() + " arguments\n" );
				return false;
			}
		}
		// end 'if / iff' check

		// check for 'not'
		else   if ( expression.getConnective().equalsIgnoreCase("not") ) {
			// the connective is NOT - there can be only one symbol / subexpression
			if ( expression.getSubexpressions().size() != 1)
			{
				System.out.println("error: connective \""+ expression.getConnective() + "\" with "+ expression.getSubexpressions().size() +" arguments\n" );
				return false;
			}
		}
		// end check for 'not'

		// check for 'and / or / xor'
		else if ( ( !expression.getConnective().equalsIgnoreCase("and") )  &&
				( !expression.getConnective().equalsIgnoreCase( "or" ) )  &&
				( !expression.getConnective().equalsIgnoreCase("xor" ) ) ) {
			System.out.println("error: unknown connective " + expression.getConnective() + "\n" );
			return false;
		}
		// end check for 'and / or / not'
		// end connective check


		// checks for validity of the logical_expression 'symbols' that go with the connective
		for( Enumeration e = expression.getSubexpressions().elements(); e.hasMoreElements(); ) {
			LogicalExpression testExpression = (LogicalExpression)e.nextElement();

			// for each subExpression in expression,
			//check to see if the subexpression is valid
			if( !valid_expression( testExpression ) ) {
				return false;
			}
		}

		//testing
		//System.out.println("The expression is valid");

		// if the method made it here, the expression must be valid
		return true;
	}




	/* this function checks to see if a unique symbol is valid */
	//////////////////// this function should be done and complete
	// originally returned a data type of long.
	// I think this needs to return true /false
	//public long valid_symbol( String symbol ) {
	public static boolean valid_symbol( String symbol ) {
		if (  symbol == null || ( symbol.length() == 0 )) {

			//testing
			//System.out.println("String: " + symbol + " is invalid! Symbol is either Null or the length is zero!\n");

			return false;
		}

		for ( int counter = 0; counter < symbol.length(); counter++ ) {
			if ( (symbol.charAt( counter ) != '_') &&
					( !Character.isLetterOrDigit( symbol.charAt( counter ) ) ) ) {

				System.out.println("String: " + symbol + " is invalid! Offending character:---" + symbol.charAt( counter ) + "---\n");

				return false;
			}
		}

		// the characters of the symbol string are either a letter or a digit or an underscore,
		//return true
		return true;
	}

	private static void exit_function(int value) {
		System.out.println("exiting from checkTrueFalse");
		System.exit(value);
	}

}
class TTEntails {
	Set<String> symbol_List = new HashSet<String>();
	boolean PropositionalLogic(LogicalExpression KnowledgeBase, Model md){
		Vector<LogicalExpression> vector = KnowledgeBase.getSubexpressions();
		Boolean bool = false;

		if( KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("if") ) {
			bool = !(bool && !(PropositionalLogic(vector.get(1),md)));
			return bool;
		}
		else if(KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("iff")){
			return bool == PropositionalLogic(vector.get(1),md);
		}
		else if(KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("and")){
			bool = true;
			int i=0;
			while(i >= 0 && i < vector.size()){
				bool = bool && PropositionalLogic(vector.get(i),md);
				if(bool==false){
					return bool;
				}
				i++;
			}
			return bool;
		}
		else if(KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("or")){
			int i=0;
			while(i >= 0 && i<vector.size()){
				bool = bool || PropositionalLogic(vector.get(i),md);
				i++;
			}
			return bool;
		}
		else if(KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("xor")){
			int truecount=0;
			for(int cnt = 0; cnt < vector.size(); cnt++){
				boolean retrieved = PropositionalLogic(vector.get(cnt),md);
				if(retrieved==true)
					truecount++;
				if(truecount>1)
					return false;
				bool = ((bool||retrieved) && !(bool && retrieved));
			}
			return bool;
		}
		else if(KnowledgeBase.getConnective()!=null && KnowledgeBase.getConnective().equalsIgnoreCase("not")){
			return !(PropositionalLogic(KnowledgeBase.getNextSubexpression(),md));
		}
		else if(KnowledgeBase.getConnective() == null){
			return md.hashMap.get(KnowledgeBase.getUniqueSymbol());
		}
		return true;
	}
	private List<String> deleteSym(Model md, List<String> ConstructorList) {
		Iterator<Entry<String,Boolean>> bool2 = md.hashMap.entrySet().iterator();
		while (bool2.hasNext()) {
			Entry<String,Boolean> bool1 = (Entry<String,Boolean>)bool2.next();
			ConstructorList.remove(bool1.getKey());
		}
		return ConstructorList;
	}
	public boolean EntailCheck(LogicalExpression KnowledgeBase, LogicalExpression t,List<String> symbols, Model model) {
		if (!symbols.isEmpty()) {

			String P = (String)symbols.get(0);
			List<String> rest = symbols.subList(1, symbols.size());
			Model trueModel = model.extend(P, true);
			Model falseModel = model.extend(P, false);
			return (EntailCheck(KnowledgeBase, t, rest, trueModel) && (EntailCheck(KnowledgeBase, t, rest, falseModel)));
		}
		else {
			boolean PropositionalLogic = PropositionalLogic(KnowledgeBase, model);
			if(!PropositionalLogic)
				return true;
			else
				return PropositionalLogic(t, model);

		}
	}
	void get_LogicalSymbols(LogicalExpression LogicalSt){
		int i=0;
		if((LogicalSt.getUniqueSymbol() == null))
			while(i >= 0 && i < LogicalSt.getSubexpressions().size()){
				LogicalExpression LogicalSt1 = LogicalSt.getSubexpressions().get(i);
				get_LogicalSymbols(LogicalSt1);
				if(!(LogicalSt1.getUniqueSymbol() == null))
					symbol_List.add(LogicalSt1.getUniqueSymbol());
				i++;
			}

		else {
			symbol_List.add(LogicalSt.getUniqueSymbol());
		}
	}
	public boolean ttEntails(LogicalExpression knowledge_base, LogicalExpression statement, Model model) {
		List<String> symbol_List = getSymbols(knowledge_base, statement);
		symbol_List = deleteSym(model,symbol_List);
		return EntailCheck(knowledge_base, statement, symbol_List, model);
	}
	List<String> getSymbols(LogicalExpression k_base, LogicalExpression alpha){
		get_LogicalSymbols(k_base);
		get_LogicalSymbols(alpha);
		List<String> list = new ArrayList<String>(symbol_List);
		return list;
	}


	class Model{
		public HashMap<String,Boolean> hashMap = new HashMap<String,Boolean>();
		public Model extend(String sym, boolean bool3) {
			Model mod = new Model();
			mod.hashMap.putAll(this.hashMap);
			mod.hashMap.put(sym, bool3);
			return mod;
		}
	}
}