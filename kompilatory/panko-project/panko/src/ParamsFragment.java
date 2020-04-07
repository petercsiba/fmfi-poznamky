import java.util.ArrayList;
import java.util.List;

//Returned from generateFunctionArgs
public class ParamsFragment {
	public List<String> params = new ArrayList<String>();
	
	//LLVM code which copies function arguments to local variables 
	CodeFragment definitionsCode = new CodeFragment(); 

	//LLVM code which declares function parameters  
	CodeFragment toVariablesCode = new CodeFragment(); 
}
