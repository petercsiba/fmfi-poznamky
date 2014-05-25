// Generated from calculator.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link calculatorParser}.
 */
public interface calculatorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link calculatorParser#Assign}.
	 * @param ctx the parse tree
	 */
	void enterAssign(@NotNull calculatorParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Assign}.
	 * @param ctx the parse tree
	 */
	void exitAssign(@NotNull calculatorParser.AssignContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Una}.
	 * @param ctx the parse tree
	 */
	void enterUna(@NotNull calculatorParser.UnaContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Una}.
	 * @param ctx the parse tree
	 */
	void exitUna(@NotNull calculatorParser.UnaContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#init}.
	 * @param ctx the parse tree
	 */
	void enterInit(@NotNull calculatorParser.InitContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#init}.
	 * @param ctx the parse tree
	 */
	void exitInit(@NotNull calculatorParser.InitContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Par}.
	 * @param ctx the parse tree
	 */
	void enterPar(@NotNull calculatorParser.ParContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Par}.
	 * @param ctx the parse tree
	 */
	void exitPar(@NotNull calculatorParser.ParContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Print}.
	 * @param ctx the parse tree
	 */
	void enterPrint(@NotNull calculatorParser.PrintContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Print}.
	 * @param ctx the parse tree
	 */
	void exitPrint(@NotNull calculatorParser.PrintContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Not}.
	 * @param ctx the parse tree
	 */
	void enterNot(@NotNull calculatorParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Not}.
	 * @param ctx the parse tree
	 */
	void exitNot(@NotNull calculatorParser.NotContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Do}.
	 * @param ctx the parse tree
	 */
	void enterDo(@NotNull calculatorParser.DoContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Do}.
	 * @param ctx the parse tree
	 */
	void exitDo(@NotNull calculatorParser.DoContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(@NotNull calculatorParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(@NotNull calculatorParser.ExpContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Mul}.
	 * @param ctx the parse tree
	 */
	void enterMul(@NotNull calculatorParser.MulContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Mul}.
	 * @param ctx the parse tree
	 */
	void exitMul(@NotNull calculatorParser.MulContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterLvalue(@NotNull calculatorParser.LvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitLvalue(@NotNull calculatorParser.LvalueContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#While}.
	 * @param ctx the parse tree
	 */
	void enterWhile(@NotNull calculatorParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#While}.
	 * @param ctx the parse tree
	 */
	void exitWhile(@NotNull calculatorParser.WhileContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Or}.
	 * @param ctx the parse tree
	 */
	void enterOr(@NotNull calculatorParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Or}.
	 * @param ctx the parse tree
	 */
	void exitOr(@NotNull calculatorParser.OrContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(@NotNull calculatorParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(@NotNull calculatorParser.StatementsContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Add}.
	 * @param ctx the parse tree
	 */
	void enterAdd(@NotNull calculatorParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Add}.
	 * @param ctx the parse tree
	 */
	void exitAdd(@NotNull calculatorParser.AddContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Int}.
	 * @param ctx the parse tree
	 */
	void enterInt(@NotNull calculatorParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Int}.
	 * @param ctx the parse tree
	 */
	void exitInt(@NotNull calculatorParser.IntContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Var}.
	 * @param ctx the parse tree
	 */
	void enterVar(@NotNull calculatorParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Var}.
	 * @param ctx the parse tree
	 */
	void exitVar(@NotNull calculatorParser.VarContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#If}.
	 * @param ctx the parse tree
	 */
	void enterIf(@NotNull calculatorParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#If}.
	 * @param ctx the parse tree
	 */
	void exitIf(@NotNull calculatorParser.IfContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#And}.
	 * @param ctx the parse tree
	 */
	void enterAnd(@NotNull calculatorParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#And}.
	 * @param ctx the parse tree
	 */
	void exitAnd(@NotNull calculatorParser.AndContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull calculatorParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull calculatorParser.BlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link calculatorParser#Comp}.
	 * @param ctx the parse tree
	 */
	void enterComp(@NotNull calculatorParser.CompContext ctx);
	/**
	 * Exit a parse tree produced by {@link calculatorParser#Comp}.
	 * @param ctx the parse tree
	 */
	void exitComp(@NotNull calculatorParser.CompContext ctx);
}