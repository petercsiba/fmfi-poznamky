//Author: Peter Csiba 
//Date: 06-01-2014

//TODO: more specific warnings (try to do line numbers with statements counting) 
//TODO: scope of variables; for example 
//  POCHIPUJ tojejedno m
//  {
//    VMOTAJ INT a
//    VMOTAJ INT b 
//  }
//  if 'a' and 'b' haven't been previously declared then llvm creates a new instance each time 
//    and that could be quite consuming (it lead to segmentation fault around 255K loops)

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.stringtemplate.v4.*;

public class CompilerVisitor extends pankoBaseVisitor<CodeFragment> {
        private Map<String, Variable> mem = new HashMap<String, Variable>();
        private int labelIndex = 0;
        private int registerIndex = 0;
        
        private int functionIndex = 0;
        private Map<String, FunctionFragment> functions = new HashMap<String, FunctionFragment>();
        private boolean inFunctionDefine = false; 
        private String inFunctionName = ""; //should not make difference if inFunctionDefine == false
        private final String FUNCTION_MAIN_NAME = "_main"; //so it cannot collide with any function name defined by user 
        
    	//global variables which were shadowed by function variables 
    	Map<String, Variable> paramsShadow = new HashMap<String, Variable>(); 
    	List<String> registeredLocalVariables = new ArrayList<String>();
        List<String> globalVariableDeclarations = new ArrayList<String>(); 

        List<String> compilerConstants = new ArrayList<String>(); 
        
        //generating internal variable names if necessarry 
        private int nameIndex = 0; 

        private String generateNewName() {
            return String.format("_name%d", nameIndex++);
        }
        
        private String generateNewLabel() {
                return String.format("L%d", this.labelIndex++);
        }
        
        private String generateNewRegister() {
        	return this.generateNewRegister(false, null); 
        }

        private String generateNewRegister(boolean global, String definition) {
        	if(global){
        		String result = String.format("@R%d", this.registerIndex++);
        		this.globalVariableDeclarations.add(result + " = common global " + definition); 
        		return result;
        	}
        	else{
        		return String.format("%%R%d", this.registerIndex++);
        	}
        }
        
        //TODO StringBuilder
        private String getCompilerConstants() {
        	String result = "";
        	for(String line : this.compilerConstants){
        		result += line + "\n"; 
        	}
        	return result; 
        }

        //TODO StringBuilder
        private String getGlobalDefinitions() {
        	String result = "";
        	for(String line : this.globalVariableDeclarations){
        		result += line + "\n"; 
        	}
        	return result; 
        }

        //TODO StringBuilder
        private String generateNewFunction() {
            return String.format("@f%d", this.functionIndex++);
        }

        private String getFunctionDefinitions() {
        	String result = "";
        	for(Map.Entry<String, FunctionFragment> entry : functions.entrySet()){
        		if(!entry.getKey().equals(FUNCTION_MAIN_NAME)){
        			result += entry.getValue().all_code.toString();
        		}
        	}
        	return result; 
        }
        
        @Override
        public CodeFragment visitInit(pankoParser.InitContext ctx) {
                CodeFragment statements = visit(ctx.statements());

                ST template = new ST(
                        "declare i32 @_printInt(i32)\n" + 
                        "declare i32 @_printChar(i8)\n" + 
                        "declare i32 @_printFloat(float)\n" +
                        "declare i32 @_printString(i8*)\n" +
                        "declare i32 @_printSlzy()\n" +
                        "declare i32 @_scanInt()\n" + 
                        "declare i8 @_scanChar()\n" + 
                        "declare float @_scanFloat()\n" + 
                        "declare i32 @_iexp(i32, i32)\n" + //TODO should be native 
                        "declare i32* @_MALLOC(i32)\n" +  
                        "declare i32 @_FREE(i32*)\n" + 
                        "declare i32 @_SET_RANDOM()\n" +  
                        "declare i32 @_RANDOM(i32)\n" +   
                        "\n\n;compiler_constants\n<compiler_constants>\n" + 
                        "\n\n;global_definitions\n<global_definitions>\n" + 
                        "\n\n;function_definitions\n<function_definitions>\n" + 
                        "define i32 @main() {\n" + 
                        "start:\n" + 
                        "call i32 @_SET_RANDOM()\n" + 
                        "\n\n;global_code\n<global_code>\n" + 
                        "\n\n;main_code\n<main_code>" +
                        "}\n"
                );
                template.add("compiler_constants", this.getCompilerConstants());
                template.add("global_definitions", this.getGlobalDefinitions());
                template.add("function_definitions", this.getFunctionDefinitions());
                
                //TODO is there global context in LLVM? 
                if(this.functions.containsKey(FUNCTION_MAIN_NAME)){
                	template.add("global_code", statements); 
                	template.add("main_code", this.functions.get(FUNCTION_MAIN_NAME).body_code.toString());
                }
                else{
                	template.add("global_code", statements); 
                	template.add("main_code", "ret i32 0\n");
                }

                return new CodeFragment(template.render(), statements.getRegister());
        }
        
        @Override
        public CodeFragment visitStatements(pankoParser.StatementsContext ctx) {
                CodeFragment code = new CodeFragment();
                for(pankoParser.StatementContext s: ctx.statement()) {
                        CodeFragment statement = visit(s);
                        code.addCode(statement.toString());
                        code.setRegister(statement.getRegister());
                }
                return code;
        }
        
        @Override
    	public CodeFragment visitRValue(@NotNull pankoParser.RValueContext ctx){
            return visit(ctx.rvalue());
    	}

        @Override
    	public CodeFragment visitMod(@NotNull pankoParser.ModContext ctx){
            return generateBinaryOperator(
                    visit(ctx.rvalue(0)),
                    visit(ctx.rvalue(1)),
                    ctx.op.getType()
            );
    	}

        @Override
    	public CodeFragment visitExp(@NotNull pankoParser.ExpContext ctx){
            return generateBinaryOperator(
                    visit(ctx.rvalue(0)),
                    visit(ctx.rvalue(1)),
                    ctx.op.getType()
            );
    	}

        @Override
    	public CodeFragment visitMul(@NotNull pankoParser.MulContext ctx){
            return generateBinaryOperator(
                    visit(ctx.rvalue(0)),
                    visit(ctx.rvalue(1)),
                    ctx.op.getType()
            );
    	}
        
        @Override
    	public CodeFragment visitAdd(@NotNull pankoParser.AddContext ctx){
            return generateBinaryOperator(
                    visit(ctx.rvalue(0)),
                    visit(ctx.rvalue(1)),
                    ctx.op.getType()
            );
    	}
        

        @Override 
        public CodeFragment visitNot(pankoParser.NotContext ctx) {
                return generateUnaryOperator(
                        visit(ctx.rvalue()),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitAnd(pankoParser.AndContext ctx) {
                return generateBinaryOperator(
                        visit(ctx.rvalue(0)),
                        visit(ctx.rvalue(1)),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitOr(pankoParser.OrContext ctx) {
                return generateBinaryOperator(
                        visit(ctx.rvalue(0)),
                        visit(ctx.rvalue(1)),
                        ctx.op.getType()
                );
        }
        
        //TODO why
        @Override
        public CodeFragment visitEqual(pankoParser.EqualContext ctx) {
                return generateBinaryOperator(
                        visit(ctx.rvalue(0)),
                        visit(ctx.rvalue(1)),
                        ctx.op.getType()
                );
        }
        
        @Override
        public CodeFragment visitSmaller(pankoParser.SmallerContext ctx) {
                return generateBinaryOperator(
                        visit(ctx.rvalue(0)),
                        visit(ctx.rvalue(1)),
                        ctx.op.getType()
                );
        }
        
        @Override
    	public CodeFragment visitPipkos(@NotNull pankoParser.PipkosContext ctx){
        	return generateConstant("0"); 
        }
        
        @Override
    	public CodeFragment visitFajne(@NotNull pankoParser.FajneContext ctx){
        	return generateConstant("1"); 
        }

        @Override
    	public CodeFragment visitTisic(@NotNull pankoParser.TisicContext ctx){
            return generateConstant("2147483647"); 
    	}

        @Override
        public CodeFragment visitInt(pankoParser.IntContext ctx) {
        	return generateConstant(ctx.INT().getText()); 
        }

        //     | BAVI rvalue                                # RandomValue
        @Override
        public CodeFragment visitRandomValue(pankoParser.RandomValueContext ctx) {
        	CodeFragment valueCode = visit(ctx.rvalue()); 
        	
    		String ret_reg = this.generateNewRegister(); 
    		
            ST template = new ST(
            		"<value_code>" + 
                    "<ret_reg> = call i32 @_RANDOM(i32 <value_reg>)\n"
            );
            template.add("value_code", valueCode.toString());
            template.add("value_reg", valueCode.getRegister());
            template.add("ret_reg", ret_reg);
            
            return new CodeFragment(template.render(), ret_reg);
        }
        
        @Override
    	public CodeFragment visitSuchy(@NotNull pankoParser.SuchyContext ctx){
            return new CodeFragment();
    	}
        
        @Override
    	public CodeFragment visitEvaluate(@NotNull pankoParser.EvaluateContext ctx){
            return visit(ctx.rexpression());
    	}
        
        @Override
    	public CodeFragment visitEmp(@NotNull pankoParser.EmpContext ctx){
            return new CodeFragment();
    	}

        //TODO refactor with visitAssign
        //TODO type 
        //     | VMOTAJ TYPE NAME                               # Vmotaj
    	@Override
        public CodeFragment visitVmotaj(pankoParser.VmotajContext ctx) {
    		CodeFragment addressCode = visit(ctx.address()); 
    		String value_register = this.generateNewRegister(); 
    		
            ST template = new ST(
            		"<address_code>" + 
                    "<value_register> = call i32 @_scanInt()\n" +
                    "store i32 <value_register>, i32* <mem_register>\n"
            );
            template.add("address_code", addressCode.toString());
            template.add("mem_register", addressCode.getRegister());
            template.add("value_register", value_register);
            
            return new CodeFragment(template.render(), value_register);
        }
    	
    	@Override
        public CodeFragment visitVymotaj(pankoParser.VymotajContext ctx) {
    			if(ctx.rexpression() != null){
	                CodeFragment code = visit(ctx.rexpression());
	                ST template = new ST(
	                        "<value_code>" + 
	                        "call i32 @_printInt (i32 <value>)\n"
	                );
	                template.add("value_code", code);
	                template.add("value", code.getRegister());
	                
	                return new CodeFragment(template.render(), code.getRegister());
    			}
    			else{
    				String string_to_print = ctx.STRING().getText(); 
    				string_to_print = string_to_print.substring(1, string_to_print.length() - 1); 
    				
	                String constant_id = String.format("@.%d", this.compilerConstants.size()); 
	                int length = string_to_print.length() + 1; // \00 
	                
	                ST template_definition = new ST(
	                		"<constant_id> = private unnamed_addr constant [<length> x i8] c\"<string>\00\", align 1" 
	                );
	                template_definition.add("constant_id", constant_id);
	                template_definition.add("string", string_to_print);
	                template_definition.add("length", length);
	                
	                this.compilerConstants.add(template_definition.render());
	                
	                ST template_print = new ST(
	                        "call i32 @_printString (i8* getelementptr inbounds ([<length> x i8]* <constant_id>, i32 0, i32 0))\n"
	                );
	                template_print.add("constant_id", constant_id);
	                template_print.add("length", length);
	                
	                return new CodeFragment(template_print.render(), null);
    			}
        }
    	
    	//if(afterDeclareTemplate != null) //assumes existence of <mem_reg>  (and nothing else to replace) 
    	private CodeFragment generateDefine(String identifier, String type, CodeFragment valueCode, String afterDeclareTemplateString, String exists_error){
            CodeFragment memCode = null; 
            
            if (!mem.containsKey(identifier)) {
                    //local definition 
                    if(this.inFunctionDefine){
                		String mem_reg = this.generateNewRegister(); 
                        mem.put(identifier, new Variable(mem_reg, type));
                        
	                    ST template = new ST(
	                    	"<mem_reg> = alloca <type>\n"
	                    );
	                    template.add("mem_reg", mem_reg);
	                    template.add("type", type);
	                    
	                    memCode = new CodeFragment(template.render(), mem_reg);
                    }
                    //global definition 
                    else{
                    	//TODO refactor to Variable class (? :) 
                    	String mem_reg = this.generateNewRegister(true, type + " " + ((type.indexOf('*') >= 0) ? "null" : "0"));
                        mem.put(identifier, new Variable(mem_reg, type));
                        
                        //declaration is done at the beginning of the resulting code 
                    	memCode = new CodeFragment("", mem_reg); 
                    }
                    
                    if(afterDeclareTemplateString != null){
                    	ST template = new ST(
                    		"<mem_code>" + 
                    		afterDeclareTemplateString  
                    	);
                    	template.add("mem_code", memCode.toString()); 
                    	template.add("mem_reg", memCode.getRegister()); 
                    	
                    	memCode = new CodeFragment(template.render(), memCode.getRegister()); 
                    }
                    
                    //this should be no problem: 
                	if(this.functions.containsKey(identifier)){
                		System.err.println("Warning: Variable '"+identifier+"' has same name as some function (generateDefineAssign)");
                	}
            } else {
                    memCode = new CodeFragment("", mem.get(identifier).register);
                    
                    System.err.println(String.format(exists_error, identifier));
            }
            
            if(valueCode != null){
            	return generateAssign(memCode, valueCode);
            }
            else{
            	return memCode; 
            }
    	}
    	
        //TODO type 
    	//TODO block 
    	/*
    	 * TODO how could this be not an error:
			POCHIPUJ j 21
			{
			  PAN INT buf ROLKA - 41 j arr
    	 */
        @Override
        public CodeFragment visitVariableDefine(pankoParser.VariableDefineContext ctx) {
        	String identifier = ctx.NAME().getText();
        	
            if (this.inFunctionDefine){
        		registerLocalVariable(identifier, "Warning: Global variable '" + identifier + "' was shadowed by parameter of function '" + inFunctionName + "' (generateDefineAssign)");
            }
            
        	return generateDefine(identifier, "i32", visit(ctx.rexpression()), null, "Warning: identifier '%s' already exists (PAN).\n  RVALUE will be set to the existing variable.");
        }

        //TODO check type 
        @Override
        public CodeFragment visitVariableAddress(pankoParser.VariableAddressContext ctx) {
        	return this.generateGetVariableAddress(ctx.NAME().getText()); 
        }
        
        //returns pointer to zero if not found or bad type 
        private CodeFragment generateGetVariableAddress(String var_name){
        	if(this.mem.containsKey(var_name)){
        		Variable var = this.mem.get(var_name); 
        		
        		if(var.type == Variable.TYPE_ARRAY_INT){
        			System.err.println("Warning: Didn't expected array name '"+var_name+"' (generateGetVariableAddress).\n  You probably wanted use ROLKA rvalue ARRAY_NAME.\n  Address to new variable i32(0) returned.");
        			return new CodeFragment(
            				generateDefine(this.generateNewName(), "i32", generateConstant("0"), null, null).toString(), 
            				this.mem.get(var_name).register
            		); 
        		}
        		
        		return new CodeFragment("", var.register); 
	        }
	        else{
	        	System.err.println("Warning: Non-existing variable '"+var_name + "' (visitVariableAddress)\n  Address to new variable i32(0) returned.\n  This could lead to Segmentation fault if occured in a loop of magnitude 100,000.\n    But no problem with functions as they free their variables.");
        		return new CodeFragment(
        				generateDefine(var_name, "i32", generateConstant("0"), null, null).toString(), 
        				this.mem.get(var_name).register
        		); 
	        }
        }
        
        //address: 
        //    NAME                                             # VariableAddress
        //    | ROLKA rvalue NAME                                # ArrayAddress  
        @Override
        public CodeFragment visitAddressValue(pankoParser.AddressValueContext ctx) {
        	return generateGetAddressValue(visit(ctx.address()));
        }
        
        //     | NAMOTAJ address rexpression                    # Assign 
        @Override
        public CodeFragment visitAssign(pankoParser.AssignContext ctx) {
        	return this.generateAssign(visit(ctx.address()), visit(ctx.rexpression())); 
        }
        
        private CodeFragment generateAssign(CodeFragment addressCode, CodeFragment valueCode) {
        	ST template = new ST(
        			"<address_code>" + 
                	"<value_code>" + 
                    "store i32 <value_reg>, i32* <address_reg>\n"
        	);
        	template.add("address_code", addressCode.toString());
        	template.add("value_code", valueCode.toString());
        	template.add("address_reg", addressCode.getRegister());
        	template.add("value_reg", valueCode.getRegister());
            
	        return new CodeFragment(template.render(), valueCode.getRegister());
        }
        
        private CodeFragment generateGetAddressValue(CodeFragment addressCode){
            String value_reg = generateNewRegister();
            
        	ST template = new ST(
        			"<address_code>" + 
        			"<value_reg> = load i32* <address_reg>\n"
        	);
        	template.add("address_code", addressCode.toString());
        	template.add("value_reg", value_reg);
        	template.add("address_reg", addressCode.getRegister());
            
	        return new CodeFragment(template.render(), value_reg);
        }
        
        private CodeFragment generateGetIdentifierValue(String identifier){
        	if(!mem.containsKey(identifier)){
        		System.err.println("Warning: Identifier '"+identifier+"' not know (generateGetIdentifierValue)");
        		return generateConstant("0");
        	}
        	return generateGetAddressValue(new CodeFragment("", mem.get(identifier).register)); 
        }
        
        @Override
        public CodeFragment visitPrimotaj(pankoParser.PrimotajContext ctx) {
        	CodeFragment addressCode = visit(ctx.address());  
        	return generateAssign(addressCode, generateBinaryOperator(
        			generateGetAddressValue(addressCode), 
        			generateConstant("1"), 
        			pankoParser.ADD));
        }
        @Override
        public CodeFragment visitOdmotaj(pankoParser.OdmotajContext ctx) {
        	CodeFragment addressCode = visit(ctx.address());  
        	return generateAssign(addressCode, generateBinaryOperator(
        			generateGetAddressValue(addressCode), 
        			generateConstant("1"), 
        			pankoParser.SUB));
        }
        @Override
        public CodeFragment visitPochill(pankoParser.PochillContext ctx) {
        	return new CodeFragment(); 
        }
        @Override
        public CodeFragment visitSlzy(pankoParser.SlzyContext ctx) {
        	return new CodeFragment("call i32 @_printSlzy()\n", null); 
        }
        
        //====================================== ARRAYS =================================

        //TODO NAME checks => remember type
        //TODO! reallocating to same name 
        //| WCBOOK rexpression TYPE NAME                   # ArrayDefine
        @Override
        public CodeFragment visitArrayDefine(pankoParser.ArrayDefineContext ctx) {
        	String arr_name = ctx.NAME().getText(); 
        	String arr_type = ctx.TYPE().getText(); 
        	
        	String size_reg = generateNewRegister(); 
        	String alloc_reg = generateNewRegister();
        	
        	CodeFragment evalCountCode = visit(ctx.rexpression()); 
        	
        	ST template = new ST( 
                	"<eval>" + 
        			"<size_reg> = mul i32 <variable_size>, <count_reg>\n" +
        		    "<alloc_reg> = call noalias i32* @_MALLOC(i32 <size_reg>) nounwind\n" +  
        		    "store i32* <alloc_reg>, i32** " //<mem_reg>\n" 
        	);
        	template.add("variable_size", "4"); 
        	template.add("eval", evalCountCode.toString());
        	template.add("alloc_reg", alloc_reg);
        	template.add("count_reg", evalCountCode.getRegister());
        	template.add("size_reg", size_reg);
        	//template.add("mem_reg", "<mem_reg>"); 
        	
        	return generateDefine(arr_name, "i32*", null, template.render() + "<mem_reg>\n", "Warning: Declaring array '"+arr_name+"' identifier already exists.\n  This leads to no action.\n  Note that in the future this could lead to reallocating the array (as assignment).\n");
        }

        @Override 
        public CodeFragment visitArrayAddress(pankoParser.ArrayAddressContext ctx) {
        	return generateGetIndexAddress(ctx.NAME().getText(), visit(ctx.rvalue())); 
        }
        
        private CodeFragment generateGetIndexAddress(String arr_name, CodeFragment indexEvalCode){
        	if(!this.mem.containsKey(arr_name)){
        		System.err.println("Warning: Identifier '"+arr_name+"' not found (generateGetIndexAddress)");
        		return generateConstant("0");
        	}
        	
        	Variable var = this.mem.get(arr_name);
        	
        	if(var.type != Variable.TYPE_ARRAY_INT){
        		System.err.println("Warning: Cannot delete '"+arr_name+"' as it is not an array (generateGetIndexAddress)");
        		return generateConstant("0");
        	}
        	
        	String arr_reg = mem.get(arr_name).register; 
        	
        	String mem_reg = generateNewRegister(); 
        	
        	String index_ptr_reg = generateNewRegister(); 
        	
        	ST template = new ST(
        			"<index_eval>" + 
        		    "<mem_reg> = load i32** <arr_reg>\n" +
        		    "<index_ptr_reg> = getelementptr inbounds i32* <mem_reg>, i32 <index_reg>\n"
        	);
        	template.add("arr_reg", arr_reg);
        	template.add("mem_reg", mem_reg);
        	template.add("index_eval", indexEvalCode.toString());
        	template.add("index_reg", indexEvalCode.getRegister());
        	template.add("index_ptr_reg", index_ptr_reg);

        	return new CodeFragment(template.render(), index_ptr_reg);
        }
        
        //TODO test type checking 
        public CodeFragment visitArrayDelete(pankoParser.ArrayDeleteContext ctx){
        	String arr_name = ctx.NAME().getText(); 
        	
        	if(!this.mem.containsKey(arr_name)){
        		System.err.println("Warning: Identifier '"+arr_name+"' not found (generateGetIndexAddress)");
        		return generateConstant("0");
        	}
        	
        	Variable var = this.mem.get(arr_name);
        	
        	if(var.type != Variable.TYPE_ARRAY_INT){
        		System.err.println("Warning: Cannot delete '"+arr_name+"' as it is not an array (generateGetIndexAddress)");
        		return generateConstant("0");
        	}
        	
        	String load_mem_reg = this.generateNewRegister(); 
        	
        	ST template = new ST(
        			"<load_mem_reg> = load i32** <arr_reg>\n" + 
        			"call i32 @_FREE(i32* <load_mem_reg>)\n"
        	); 
        	template.add("arr_reg", var.register);
        	template.add("load_mem_reg", load_mem_reg); 
        	return new CodeFragment(template.render(), null);
        }
        //======================================= FUNCTIONS =========================================
        
        /** 
    MOTAC TYPE NAME (TYPE NAME)* NEWLINE 
    (statements NEWLINE)?
    VYPAPAJ rexpression 
    ;  
         */
        
        private void registerLocalVariable(String var_name, String err){
    		if(mem.containsKey(var_name)){
    			System.err.println(err);
    			paramsShadow.put(var_name, mem.get(var_name)); 
    		} 
    		registeredLocalVariables.add(var_name); 
    		mem.remove(var_name);
        }
        
        private void unregisterLocalVariables(){
            for(String var_name : this.registeredLocalVariables){
            	unregisterLocalVariable(var_name);
            }
            this.registeredLocalVariables.clear(); 
        }
        
        private void unregisterLocalVariable(String var_name){
        	mem.remove(var_name);
        	if(paramsShadow.containsKey(var_name)){
        		mem.put(var_name, paramsShadow.get(var_name)); 
        	}
        }

        @Override
        public CodeFragment visitMain(pankoParser.MainContext ctx) {
        	if(this.functions.containsKey(FUNCTION_MAIN_NAME)){
        		System.err.println("Warning: MEGA MOTAC '"+ctx.funkcia().NAME().get(0).getText()+"' already defined (visitMain).");
        	}
        	return generateFunction(ctx.funkcia(), FUNCTION_MAIN_NAME); 
        }
        
        @Override
        public CodeFragment visitFunctionDefine(pankoParser.FunctionDefineContext ctx) {
        	return generateFunction(ctx.funkcia(), null); 
        }
        
        @Override
        public CodeFragment visitFunctionExtern(pankoParser.FunctionExternContext ctx) {
        	String fn_name = ctx.NAME().get(0).getText();
        	String llvm_function_name = "@" + fn_name; 
        	
        	FunctionFragment functionFragment = parseFunctionHeader(fn_name, llvm_function_name, ctx.TYPE(), ctx.NAME());
        	if(functionFragment == null){
        		this.inFunctionDefine = false; 
        		return new CodeFragment(); 
        	}
        	
        	ParamsFragment paramsFragment = functionFragment.paramsFragment; 
        	
            ST template_body = new ST(
            		"declare i32 <llvm_function_name>(<params_definition_code>)\n"
            );
            template_body.add("params_definition_code", paramsFragment.definitionsCode.toString());
            template_body.add("llvm_function_name", llvm_function_name);
            
            //TODO refactor, CodeFragment.copy()
            functionFragment.body_code = new CodeFragment(template_body.render(), null);
            functionFragment.all_code = new CodeFragment(template_body.render(), null);

    		this.inFunctionDefine = false; 
    		
            //function code must be at the beginning 
            return new CodeFragment(); 
        }
        
        // Note that first of both types and names is the type and name of the function 
        private ParamsFragment parseFunctionArgs(String fn_name, List<TerminalNode> types, List<TerminalNode> names){
        	ParamsFragment result = new ParamsFragment(); 
        	
        	//TODO types 
        	for(int i=1; i< names.size() ; i++){
        		String var_name = names.get(i).getText(); 
        		result.params.add(var_name);
        		
        		String register = generateNewRegister();
        		if(i != 1) result.definitionsCode.addCode(","); 
        		result.definitionsCode.addCode("i32 " + register);
        		
        		registerLocalVariable(var_name, "Warning: Global variable '" + var_name + "' was shadowed by parameter of function '"+fn_name+"' (FUNCTION_DEFINE)");
        		
        		// generateDefineAssign adds it to this.mem
        		CodeFragment defineVariable = generateDefine(var_name, "i32", new CodeFragment("", register), null, "PLACE petrz : exists error '"+var_name+"' (generateFunction)");
        		result.toVariablesCode.addCode(defineVariable.toString()); 
        	}
        	
        	return result; 
        }
        
        private FunctionFragment parseFunctionHeader(String fn_name, String llvm_name, List<TerminalNode> types, List<TerminalNode> names){
        	//String fn_return_type = ctx.TYPE(0).getText(); //TODO 
        	
        	if(this.inFunctionDefine){
    			System.err.println("Warning: Defining function '" + fn_name + "' in function. It will be ignored. (parseFunctionHeader)");
    			return null; 
        	}
        	if(this.mem.containsKey(fn_name)){
        		System.err.println("Warning: Function '"+fn_name+"' has same name as some variable (parseFunctionHeader)");
        	}

        	this.inFunctionDefine = true; 
        	this.inFunctionName = fn_name;
        	
        	FunctionFragment functionFragment = new FunctionFragment();
    		functionFragment.llvm_name = llvm_name; 
    		this.functions.put(fn_name, functionFragment);
        	
    		//Generate parameters 
    		ParamsFragment paramsFragment = parseFunctionArgs(fn_name, types, names); 
    		functionFragment.paramsFragment = paramsFragment; 
    	
    		return functionFragment; 
        }
        
        private CodeFragment generateFunction(pankoParser.FunkciaContext ctx, String override_name){
        	String fn_name = (override_name == null) ? ctx.NAME().get(0).getText() : override_name;
        	
        	FunctionFragment functionFragment = parseFunctionHeader(fn_name, generateNewFunction(), ctx.TYPE(), ctx.NAME());
        	if(functionFragment == null){
            	this.inFunctionDefine = false; 
        		return new CodeFragment(); 
        	}
        	
        	ParamsFragment paramsFragment = functionFragment.paramsFragment; 

        	//TODO test 
        	if(override_name != null && "_main".equals(override_name) && paramsFragment.params.size() > 0){
        		System.err.println("Warning: Main '"+ fn_name + "'cannot have parameters.");
        		paramsFragment.params.clear();
        		paramsFragment.toVariablesCode = new CodeFragment();
        		paramsFragment.definitionsCode = new CodeFragment();
        	}
        	
        	CodeFragment statementsCode = (ctx.statements() == null) ? null : visit(ctx.statements());
        	CodeFragment returnCode = visit(ctx.rexpression()); 

            ST template_body = new ST(
            		"<param_variables>" + 
                    "<statements>" +
                    "<return_code>" + 
                    "ret i32 <return_register>\n"
            );
            template_body.add("param_variables", paramsFragment.toVariablesCode.toString());
            template_body.add("statements", statementsCode);
            template_body.add("return_code", returnCode);
            template_body.add("return_register", returnCode.getRegister());
            
            functionFragment.body_code = new CodeFragment(template_body.render(), returnCode.getRegister());
            
            ST template = new ST(
            		";<function_name>\n" + 
                    "define i32 <llvm_name>(<params_code>){\n" +
                    "    entry:\n" + 
                    "<body_code>" + 
                    "}" + "\n"
            );
            template.add("function_name", fn_name);
            template.add("llvm_name", functionFragment.llvm_name);
            template.add("params_code", paramsFragment.definitionsCode);
            template.add("body_code", functionFragment.body_code.toString());
            
            functionFragment.all_code = new CodeFragment(template.render(), returnCode.getRegister()); 
            
            //clean up parameter variables 
            unregisterLocalVariables(); 
            
            this.inFunctionDefine = false; 
            
            //function code must be at the beginning 
            return new CodeFragment(); 
        }
        
        //TODO type
        //TODO check argument count 
        /**
         *  ZMOTAJ NAME (rvalue*) (rexpression)?             # FunctionValue
         */
        @Override
        public CodeFragment visitFunctionValue(pankoParser.FunctionValueContext ctx) {
        	String fn_name = ctx.NAME().getText();
        	FunctionFragment fn_fragment = this.functions.get(fn_name); 
        	
        	if(fn_fragment == null){
        		System.err.println("Warning: Not recognized function '"+fn_name+"' (FUNCTION_VALUE).\n  Could be order of declaration, mistyping.");
        		return generateConstant("0");
        	}
        	
        	//evaluate parameter expressions and generate param string 
        	List<CodeFragment> paramEvals = new ArrayList<CodeFragment>();
        	
        	if(ctx.rvalue() != null){
        		for(int i=0; i<ctx.rvalue().size(); i++){
        			paramEvals.add(visit(ctx.rvalue(i)));  
        		}
        	}
        	if(ctx.rexpression() != null){
        		paramEvals.add(visit(ctx.rexpression())); 
        	}

        	String evalCode = ""; 
        	String paramsCode = ""; 
        	
        	for(int i=0; i<paramEvals.size() ; i++){
        		CodeFragment cf = paramEvals.get(i);
        		
        		if(paramsCode.length() > 0){
        			paramsCode += ",";
        		}
        		
        		paramsCode += "i32 " + cf.getRegister();
        		evalCode += cf.toString(); 
        		
        		if(i >= fn_fragment.paramsFragment.params.size()){
        			System.err.println("Warning: Too many ('"+paramEvals.size()+"') parameters for function '"+fn_name+"' (visitFunctionValue).\n  Excess arguments are ignored in the function call but are evaluated.\n");
        			break;
        		}
        	}
        	
        	if(paramEvals.size() < fn_fragment.paramsFragment.params.size()){
        		System.err.println("Warning: Too little ('"+paramEvals.size()+"') parameters for function '"+fn_name+"' (visitFunctionValue).\n  Missing arguments were set to '0' (or null for pointers).");
        		for(int i = paramEvals.size() ; i < fn_fragment.paramsFragment.params.size() ; i++){
        			paramsCode += "i32 0"; //TODO test 
        		}
        	}

        	String fn_register = generateNewRegister();
        	
        	//put all together
            ST template = new ST( 
            		"<eval_params>" + 
                    "<fn_reg> = call i32 <llvm_name>(<parameters>)\n"
            );
            template.add("fn_reg", fn_register);
            template.add("llvm_name", fn_fragment.llvm_name);
            template.add("eval_params", evalCode); 
            template.add("parameters", paramsCode); 
                        
            CodeFragment ret = new CodeFragment();
            ret.setRegister(fn_register);
            ret.addCode(template.render());

            return ret;
        }

        @Override 
        public CodeFragment visitIf(pankoParser.IfContext ctx) {
            CodeFragment condition = visit(ctx.rexpression());
            CodeFragment statement_true = visit(ctx.tr);
            CodeFragment statement_false = (ctx.fa != null) ? visit(ctx.fa) : new CodeFragment();

            ST template = new ST(
                    "; -- AGE? begin\n" +
                    "<condition_code>" + 
                    "<cmp_reg> = icmp ne i32 <con_reg>, 0\n" + 
                    "br i1 <cmp_reg>, label %<block_true>, label %<block_false>\n" +
                    "<block_true>:\n" +
                    "<statement_true_code>" +
                    "br label %<block_end>\n" + 
                    "<block_false>:\n" + 
                    "<statement_false_code>" +
                    "br label %<block_end>\n" + 
                    "<block_end>:\n" +
                    "<ret> = add i32 0, 0\n" + 
                    "; -- AGE? end\n"
            );
            template.add("condition_code", condition);
            template.add("statement_true_code", statement_true);
            template.add("statement_false_code", statement_false);
            template.add("cmp_reg", this.generateNewRegister());
            template.add("con_reg", condition.getRegister());
            template.add("block_true", this.generateNewLabel());
            template.add("block_false", this.generateNewLabel());
            template.add("block_end", this.generateNewLabel());
            String return_register = generateNewRegister();
            template.add("ret", return_register);
            
            CodeFragment ret = new CodeFragment();
            ret.setRegister(return_register);
            ret.addCode(template.render());

            return ret;
        }

        @Override
        public CodeFragment visitWhile(pankoParser.WhileContext ctx) {
                CodeFragment condition = visit(ctx.rexpression());
                CodeFragment body = visit(ctx.statement());
                
                ST template = new ST(
                        "; -- MACKAJ begin\n" +
                		"br label %<cmp_label>\n" + 
                        "<cmp_label>:\n" + 
                        "<condition_code>" +
                        "<cmp_register> = icmp ne i32 <condition_register>, 0\n" + 
                        "br i1 <cmp_register>, label %<body_label>, label %<end_label>\n" + 
                        "<body_label>:\n" + 
                        "<body_code>" + 
                        "br label %<cmp_label>\n" + 
                        "<end_label>:\n" + 
                        "<ret> = add i32 0, 0\n" +
                        "; -- MACKAJ end\n"
                );
                template.add("cmp_label", generateNewLabel());
                template.add("condition_code", condition);
                template.add("cmp_register", generateNewRegister());
                template.add("condition_register", condition.getRegister());
                template.add("body_label", generateNewLabel());
                template.add("end_label", generateNewLabel());
                template.add("body_code", body);
                String end_register = generateNewRegister();
                template.add("ret", end_register);
                
                CodeFragment ret = new CodeFragment();
                ret.addCode(template.render());
                ret.setRegister(end_register);
                return ret;
        }
        
        //TODO refactor it to WHILE 
        //TODO notworking if already defined (used before) :
//POCHIPUJ i 42
//  NAMOTAJ ROLKA i arr * i i
//POCHIPUJ i 42
//  VYMOTAJ ROLKA i arr
        @Override
        public CodeFragment visitFor(pankoParser.ForContext ctx) {
        		String identifier = ctx.NAME().getText();
        		
        		if(this.mem.containsKey(identifier)){
        			if(this.paramsShadow.containsKey(identifier)){
        				System.err.println("Warning: You are a noob who declared same variable '"+identifier+"' in function '"+this.inFunctionName+"', function body and for loop.\n  Or in two nested POCHIPUJ loops\n  The outmost variable reference lost.");
        			}
        			this.registerLocalVariable(identifier, "Warning: Identifier '"+identifier+"' shadowed (visitFor).");
        		}
        		
                CodeFragment statement_najprv = generateDefine(identifier, "i32", generateConstant("0"), null, "Warning: (POCHIPUJ-najprv) identifier '%s' already exists");
                CodeFragment statement_motaj = visit(ctx.statement());
                //identifier = identifier + 1
                CodeFragment statement_potom = generateAssign(
                	this.generateGetVariableAddress(identifier), 
                	generateBinaryOperator(
                        generateGetIdentifierValue(identifier),
                        generateConstant("1"),
                        pankoParser.ADD
                    )
                );
                
                CodeFragment condition_value = generateBinaryOperator(
                		generateGetIdentifierValue(identifier),
                        visit(ctx.rexpression()),
                        pankoParser.SUB
                );
                
                ST template = new ST(
                        "; -- POCHIPUJ begin\n" +
                        "<statement_najprv_code>" + 
                        "br label %<body_label>\n" + 
                        "<cmp_label>:\n" + 
                        "<condition_code>" +
                        "<cmp_register> = icmp ne i32 <condition_register>, 0\n" + 
                        "br i1 <cmp_register>, label %<body_label>, label %<end_label>\n" + 
                        "<body_label>:\n" + 
                        "<statement_motaj_code>" + 
                        "<statement_potom_code>" + 
                        "br label %<cmp_label>\n" + 
                        "<end_label>:\n" + 
                        "<ret> = add i32 0, 0\n" +
                        "; -- POCHIPUJ end\n"
                );
                
                template.add("cmp_label", generateNewLabel());
                template.add("condition_code", condition_value);
                template.add("cmp_register", generateNewRegister());
                template.add("condition_register", condition_value.getRegister());
                template.add("body_label", generateNewLabel());
                template.add("end_label", generateNewLabel());
                template.add("statement_najprv_code", statement_najprv);
                template.add("statement_potom_code", statement_potom);
                template.add("statement_motaj_code", statement_motaj);
                String end_register = generateNewRegister();
                template.add("ret", end_register);
                
                this.unregisterLocalVariable(identifier);
                
                return new CodeFragment(template.render(), end_register);
        }
        
        //TODO type 
    	private CodeFragment generateConstant(String value){
            CodeFragment code = new CodeFragment();
            String register = generateNewRegister();
            code.setRegister(register);
            code.addCode(String.format("%s = add i32 0, %s\n", register, value));
            return code;
    	}
    	
    	//TODO type 
        private CodeFragment generateBinaryOperator(CodeFragment left, CodeFragment right, Integer operator) {
                String code_stub = "<ret> = <instruction> i32 <left_val>, <right_val>\n";
                String instruction = "or";
                switch (operator) {
                        case pankoParser.ADD:
                                instruction = "add";
                                break;
                        case pankoParser.SUB:
                                instruction = "sub";
                                break;
                        case pankoParser.MUL:
                                instruction = "mul";
                                break;
                        case pankoParser.DIV:
                                instruction = "sdiv";
                                break;
                        case pankoParser.MOD:
                            instruction = "srem";
                            break;
                        case pankoParser.EXP:
                                instruction = "@_iexp";
                                code_stub = "<ret> = call i32 <instruction>(i32 <left_val>, i32 <right_val>)\n";
                                break;
                        case pankoParser.AND:
                                instruction = "and";
                        case pankoParser.OR:
                                ST temp1 = new ST(
                                        "<r1> = icmp ne i32 \\<left_val>, 0\n" +
                                        "<r2> = icmp ne i32 \\<right_val>, 0\n" +
                                        "<r3> = \\<instruction> i1 <r1>, <r2>\n" +
                                        "\\<ret> = zext i1 <r3> to i32\n"
                                );
                                temp1.add("r1", this.generateNewRegister());
                                temp1.add("r2", this.generateNewRegister());
                                temp1.add("r3", this.generateNewRegister());
                                code_stub = temp1.render();
                                break;
                        case pankoParser.EQUAL:
                            ST temp2 = new ST(
                                    "<r1> = icmp eq i32 \\<left_val>, \\<right_val>\n" +
                                    "\\<ret> = zext i1 <r1> to i32\n"
                            );
                            temp2.add("r1", this.generateNewRegister());
                            code_stub = temp2.render(); 
                        	break;
                        case pankoParser.SMALLER:
                            ST temp3 = new ST(
                                    "<r1> = icmp slt i32 \\<left_val>, \\<right_val>\n" +
                                    "\\<ret> = zext i1 <r1> to i32\n"
                            );
                            temp3.add("r1", this.generateNewRegister());
                            code_stub = temp3.render();
                            break;
                }
                ST template = new ST(
                        "<left_code>" + 
                        "<right_code>" + 
                        code_stub
                );
                template.add("left_code", left);
                template.add("right_code", right);
                template.add("instruction", instruction);
                template.add("left_val", left.getRegister());
                template.add("right_val", right.getRegister());
                String ret_register = this.generateNewRegister();
                template.add("ret", ret_register);
                
                CodeFragment ret = new CodeFragment();
                ret.setRegister(ret_register);
                ret.addCode(template.render());
                return ret;
        
        }
        
        //TODO type 
        private CodeFragment generateUnaryOperator(CodeFragment code, Integer operator) {
                if (operator == pankoParser.ADD) {
                        return code;
                }

                String code_stub = "";
                switch(operator) {
                        case pankoParser.SUB:
                                code_stub = "<ret> = sub i32 0, <input>\n";
                                break;
                        case pankoParser.NOT:
                                ST temp = new ST(
                                        "<r> = icmp eq i32 \\<input>, 0\n" + 
                                        "\\<ret> = zext i1 <r> to i32\n"
                                );
                                temp.add("r", this.generateNewRegister());
                                code_stub = temp.render();
                                break;
                }
                ST template = new ST("<code>" + code_stub);
                String ret_register = this.generateNewRegister();
                template.add("code", code);
                template.add("ret", ret_register);
                template.add("input", code.getRegister());

                CodeFragment ret = new CodeFragment();        
                ret.setRegister(ret_register);
                ret.addCode(template.render());
                return ret;
                
        }
    	

        @Override 
        public CodeFragment visitBlock(pankoParser.BlockContext ctx) {
                return visit(ctx.statements());
        }
}
