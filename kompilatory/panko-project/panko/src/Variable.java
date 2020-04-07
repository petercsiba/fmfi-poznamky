
public class Variable {
	final public static int TYPE_INT = 0; 
	final public static int TYPE_ARRAY_INT = 1;
	
	public String register = null;
	public int type = -1;
	
	public Variable(String register, int type){
		this.register = register;
		this.type = type; 
	}
	
	public Variable(String register, String type){
		this.register = register;
		this.type = Variable.typeFromString(type); 
	}
	
	public static int typeFromString(String type){
		if("i32".equals(type)){
			return Variable.TYPE_INT;
		}
		if("i32*".equals(type)){
			return Variable.TYPE_ARRAY_INT;
		}
		return Variable.TYPE_INT;
	}
}
