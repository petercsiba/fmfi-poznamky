// Generated from calculator.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link calculatorParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface calculatorVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link calculatorParser#Assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(@NotNull calculatorParser.AssignContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Una}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUna(@NotNull calculatorParser.UnaContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInit(@NotNull calculatorParser.InitContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Par}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar(@NotNull calculatorParser.ParContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Print}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(@NotNull calculatorParser.PrintContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(@NotNull calculatorParser.NotContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Do}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDo(@NotNull calculatorParser.DoContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(@NotNull calculatorParser.ExpContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Mul}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMul(@NotNull calculatorParser.MulContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#lvalue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalue(@NotNull calculatorParser.LvalueContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#While}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(@NotNull calculatorParser.WhileContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Or}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(@NotNull calculatorParser.OrContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(@NotNull calculatorParser.StatementsContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Add}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd(@NotNull calculatorParser.AddContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt(@NotNull calculatorParser.IntContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Var}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(@NotNull calculatorParser.VarContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#If}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(@NotNull calculatorParser.IfContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#And}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(@NotNull calculatorParser.AndContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull calculatorParser.BlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link calculatorParser#Comp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComp(@NotNull calculatorParser.CompContext ctx);
}