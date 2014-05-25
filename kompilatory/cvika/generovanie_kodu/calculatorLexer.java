// Generated from calculator.g4 by ANTLR 4.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class calculatorLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INT=1, FLOAT=2, MUL=3, DIV=4, ADD=5, SUB=6, EXP=7, WHITESPACE=8, NEWLINE=9, 
		ASSIGN=10, BLOCK_START=11, BLOCK_END=12, IF=13, ELSE=14, WHILE=15, DO=16, 
		AND=17, OR=18, NOT=19, PAREN_OPEN=20, PAREN_CLOSE=21, STRING=22, LT=23, 
		LTE=24, GT=25, GTE=26, EQ=27, NEQ=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"INT", "FLOAT", "'*'", "'/'", "'+'", "'-'", "'^'", "WHITESPACE", "'\n'", 
		"'='", "'{'", "'}'", "'if'", "'else'", "'while'", "'do'", "'and'", "'or'", 
		"'not'", "'('", "')'", "STRING", "'<'", "'<='", "'>'", "'>='", "'=='", 
		"'!='"
	};
	public static final String[] ruleNames = {
		"INT", "FLOAT", "MUL", "DIV", "ADD", "SUB", "EXP", "WHITESPACE", "NEWLINE", 
		"ASSIGN", "BLOCK_START", "BLOCK_END", "IF", "ELSE", "WHILE", "DO", "AND", 
		"OR", "NOT", "PAREN_OPEN", "PAREN_CLOSE", "STRING", "LT", "LTE", "GT", 
		"GTE", "EQ", "NEQ", "DIGIT"
	};


	public calculatorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "calculator.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 7: WHITESPACE_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WHITESPACE_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\36\u009d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\6\2?\n\2\r"+
		"\2\16\2@\3\3\6\3D\n\3\r\3\16\3E\3\3\3\3\7\3J\n\3\f\3\16\3M\13\3\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3"+
		"\f\3\f\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\7\27\u0087\n\27\f\27\16"+
		"\27\u008a\13\27\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34"+
		"\3\34\3\34\3\35\3\35\3\35\3\36\3\36\2\37\3\3\1\5\4\1\7\5\1\t\6\1\13\7"+
		"\1\r\b\1\17\t\1\21\n\2\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37"+
		"\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33\1"+
		"\65\34\1\67\35\19\36\1;\2\1\3\2\6\4\2\13\13\"\"\4\2C\\c|\5\2\62;C\\c|"+
		"\3\2\62;\u009f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3"+
		"\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2"+
		"\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3"+
		"\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2"+
		"\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\2"+
		"9\3\2\2\2\3>\3\2\2\2\5C\3\2\2\2\7N\3\2\2\2\tP\3\2\2\2\13R\3\2\2\2\rT\3"+
		"\2\2\2\17V\3\2\2\2\21X\3\2\2\2\23\\\3\2\2\2\25^\3\2\2\2\27`\3\2\2\2\31"+
		"b\3\2\2\2\33d\3\2\2\2\35g\3\2\2\2\37l\3\2\2\2!r\3\2\2\2#u\3\2\2\2%y\3"+
		"\2\2\2\'|\3\2\2\2)\u0080\3\2\2\2+\u0082\3\2\2\2-\u0084\3\2\2\2/\u008b"+
		"\3\2\2\2\61\u008d\3\2\2\2\63\u0090\3\2\2\2\65\u0092\3\2\2\2\67\u0095\3"+
		"\2\2\29\u0098\3\2\2\2;\u009b\3\2\2\2=?\5;\36\2>=\3\2\2\2?@\3\2\2\2@>\3"+
		"\2\2\2@A\3\2\2\2A\4\3\2\2\2BD\5;\36\2CB\3\2\2\2DE\3\2\2\2EC\3\2\2\2EF"+
		"\3\2\2\2FG\3\2\2\2GK\7\60\2\2HJ\5;\36\2IH\3\2\2\2JM\3\2\2\2KI\3\2\2\2"+
		"KL\3\2\2\2L\6\3\2\2\2MK\3\2\2\2NO\7,\2\2O\b\3\2\2\2PQ\7\61\2\2Q\n\3\2"+
		"\2\2RS\7-\2\2S\f\3\2\2\2TU\7/\2\2U\16\3\2\2\2VW\7`\2\2W\20\3\2\2\2XY\t"+
		"\2\2\2YZ\3\2\2\2Z[\b\t\2\2[\22\3\2\2\2\\]\7\f\2\2]\24\3\2\2\2^_\7?\2\2"+
		"_\26\3\2\2\2`a\7}\2\2a\30\3\2\2\2bc\7\177\2\2c\32\3\2\2\2de\7k\2\2ef\7"+
		"h\2\2f\34\3\2\2\2gh\7g\2\2hi\7n\2\2ij\7u\2\2jk\7g\2\2k\36\3\2\2\2lm\7"+
		"y\2\2mn\7j\2\2no\7k\2\2op\7n\2\2pq\7g\2\2q \3\2\2\2rs\7f\2\2st\7q\2\2"+
		"t\"\3\2\2\2uv\7c\2\2vw\7p\2\2wx\7f\2\2x$\3\2\2\2yz\7q\2\2z{\7t\2\2{&\3"+
		"\2\2\2|}\7p\2\2}~\7q\2\2~\177\7v\2\2\177(\3\2\2\2\u0080\u0081\7*\2\2\u0081"+
		"*\3\2\2\2\u0082\u0083\7+\2\2\u0083,\3\2\2\2\u0084\u0088\t\3\2\2\u0085"+
		"\u0087\t\4\2\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2"+
		"\2\2\u0088\u0089\3\2\2\2\u0089.\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c"+
		"\7>\2\2\u008c\60\3\2\2\2\u008d\u008e\7>\2\2\u008e\u008f\7?\2\2\u008f\62"+
		"\3\2\2\2\u0090\u0091\7@\2\2\u0091\64\3\2\2\2\u0092\u0093\7@\2\2\u0093"+
		"\u0094\7?\2\2\u0094\66\3\2\2\2\u0095\u0096\7?\2\2\u0096\u0097\7?\2\2\u0097"+
		"8\3\2\2\2\u0098\u0099\7#\2\2\u0099\u009a\7?\2\2\u009a:\3\2\2\2\u009b\u009c"+
		"\t\5\2\2\u009c<\3\2\2\2\7\2@EK\u0088";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}