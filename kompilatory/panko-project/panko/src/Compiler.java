import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Compiler {
    public static void main(String[] args) throws Exception {
    	PrintStream out = null;
	    try {
	        out = new PrintStream(new FileOutputStream("compiler.out"));
	    }
	    catch(Exception e) {}
        
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        
        ANTLRInputStream input = new ANTLRInputStream(is);
        pankoLexer lexer = new pankoLexer(input);
        
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        pankoParser parser = new pankoParser(tokens);
        ParseTree tree = parser.init(); // parse
        
        CompilerVisitor eval = new CompilerVisitor();
        CodeFragment body = eval.visit(tree);
        //CodeFragment header = eval.getHeader(); //functions (& extern) 
        //header.addCode(body.toString());
        System.out.print(body);
        
        //out.println("TREE: " + tree.toStringTree());
        
        out.close(); 
    }
}
