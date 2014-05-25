import java.util.HashMap;
import java.util.Map;
import org.stringtemplate.v4.*;

public class CompilerVisitor extends calculatorBaseVisitor<CodeFragment> {
        private Map<String, String> mem = new HashMap<String, String>();
        private int labelIndex = 0;
        private int registerIndex = 0;

        private String generateNewLabel() {
                return String.format("L%d", this.labelIndex++);
        }

        private String generateNewRegister() {
                return String.format("%%R%d", this.registerIndex++);
        }

        @Override
        public CodeFragment visitAssign(calculatorParser.AssignContext ctx) {
                CodeFragment value = visit(ctx.expression());
                String mem_register;
                String code_stub = "";

                String identifier = ctx.lvalue().getText();
                if (!mem.containsKey(identifier)) {
                        mem_register = this.generateNewRegister();
                        code_stub = "<mem_register> = alloca i32\n";
                        mem.put(identifier, mem_register);
                } else {
                        mem_register = mem.get(identifier);
                }
                ST template = new ST(
                        "<value_code>" + 
                        code_stub + 
                        "store i32 <value_register>, i32* <mem_register>\n"
                );
                template.add("value_code", value);
                template.add("value_register", value.getRegister());
                template.add("mem_register", mem_register);
                CodeFragment ret = new CodeFragment();
                ret.addCode(template.render());
                ret.setRegister(value.getRegister());
                return ret;
        }

        @Override
        public CodeFragment visitPrint(calculatorParser.PrintContext ctx) {
                CodeFragment code = visit(ctx.expression());
                ST template = new ST(
                        "<value_code>" + 
                        "call i32 @printInt (i32 <value>)\n"
                );
                template.add("value_code", code);
                template.add("value", code.getRegister());
                
                CodeFragment ret = new CodeFragment();
                ret.addCode(template.render());
                return ret;
        }

        public CodeFragment generateBinaryOperatorCodeFragment(CodeFragment left, CodeFragment right, Integer operator) {
                String code_stub = "<ret> = <instruction> i32 <left_val>, <right_val>\n";
                String instruction = "or";
                switch (operator) {
                        case calculatorParser.ADD:
                                instruction = "add";
                                break;
                        case calculatorParser.SUB:
                                instruction = "sub";
                                break;
                        case calculatorParser.MUL:
                                instruction = "mul";
                                break;
                        case calculatorParser.DIV:
                                instruction = "sdiv";
                                break;
                        case calculatorParser.EXP:
                                instruction = "@iexp";
                                code_stub = "<ret> = call i32 <instruction>(i32 <left_val>, i32 <right_val>)\n";
                                break;
                        case calculatorParser.AND:
                                instruction = "and";
                        case calculatorParser.OR:
                                ST temp = new ST(
                                        "<r1> = icmp ne i32 \\<left_val>, 0\n" +
                                        "<r2> = icmp ne i32 \\<right_val>, 0\n" +
                                        "<r3> = \\<instruction> i1 <r1>, <r2>\n" +
                                        "\\<ret> = zext i1 <r3> to i32\n"
                                );
                                temp.add("r1", this.generateNewRegister());
                                temp.add("r2", this.generateNewRegister());
                                temp.add("r3", this.generateNewRegister());
                                code_stub = temp.render();
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
        
        public CodeFragment generateUnaryOperatorCodeFragment(CodeFragment code, Integer operator) {
                if (operator == calculatorParser.ADD) {
                        return code;
                }

                String code_stub = "";
                switch(operator) {
                        case calculatorParser.SUB:
                                code_stub = "<ret> = sub i32 0, <input>\n";
                                break;
                        case calculatorParser.NOT:
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
        public CodeFragment visitAdd(calculatorParser.AddContext ctx) {
                return generateBinaryOperatorCodeFragment(
                        visit(ctx.expression(0)),
                        visit(ctx.expression(1)),
                        ctx.op.getType()
                );
        }

        @Override 
        public CodeFragment visitMul(calculatorParser.MulContext ctx) {
                return generateBinaryOperatorCodeFragment(
                        visit(ctx.expression(0)),
                        visit(ctx.expression(1)),
                        ctx.op.getType()
                );
        }

        @Override 
        public CodeFragment visitExp(calculatorParser.ExpContext ctx) {
                return generateBinaryOperatorCodeFragment(
                        visit(ctx.expression(0)),
                        visit(ctx.expression(1)),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitPar(calculatorParser.ParContext ctx) {
                return visit(ctx.expression());
        }


        @Override
        public CodeFragment visitUna(calculatorParser.UnaContext ctx) {
                return generateUnaryOperatorCodeFragment(
                        visit(ctx.expression()),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitVar(calculatorParser.VarContext ctx) {
                String id = ctx.STRING().getText();
                CodeFragment code = new CodeFragment();
                String register = generateNewRegister();
                String pointer = "!\"Unknown identifier\"";
                if (!mem.containsKey(id)) {
                        System.err.println(String.format("Error: idenifier '%s' does not exists", id));

                } else {
                        pointer = mem.get(id);
                }
                code.addCode(String.format("%s = load i32* %s\n", register, pointer));
                code.setRegister(register);
                return code;
        }

        @Override
        public CodeFragment visitInt(calculatorParser.IntContext ctx) {
                String value = ctx.INT().getText();
                CodeFragment code = new CodeFragment();
                String register = generateNewRegister();
                code.setRegister(register);
                code.addCode(String.format("%s = add i32 0, %s\n", register, value));
                return code;
        }

        @Override 
        public CodeFragment visitBlock(calculatorParser.BlockContext ctx) {
                return visit(ctx.statements());
        }

        @Override 
        public CodeFragment visitIf(calculatorParser.IfContext ctx) {
                CodeFragment condition = visit(ctx.expression());
                CodeFragment statement_true = visit(ctx.statement(0));
                CodeFragment statement_false = visit(ctx.statement(1));

                ST template = new ST(
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
                        "<ret> = add i32 0, 0\n"
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
        public CodeFragment visitWhile(calculatorParser.WhileContext ctx) {
                CodeFragment condition = visit(ctx.expression());
                CodeFragment body = visit(ctx.statement());
                
                ST template = new ST(
                        "br label %<cmp_label>\n" + 
                        "<cmp_label>:\n" + 
                        "<condition_code>" +
                        "<cmp_register> = icmp ne i32 <condition_register>, 0\n" + 
                        "br i1 <cmp_register>, label %<body_label>, label %<end_label>\n" + 
                        "<body_label>:\n" + 
                        "<body_code>" + 
                        "br label %<cmp_label>\n" + 
                        "<end_label>:\n" + 
                        "<ret> = add i32 0, 0\n"
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
        
        @Override
        public CodeFragment visitDo(calculatorParser.DoContext ctx) {
                CodeFragment condition = visit(ctx.expression());
                CodeFragment body = visit(ctx.statement());
                
                ST template = new ST(
                        "br label %<body_label>\n" + 
                        "<cmp_label>:\n" + 
                        "<condition_code>" +
                        "<cmp_register> = icmp ne i32 <condition_register>, 0\n" + 
                        "br i1 <cmp_register>, label %<body_label>, label %<end_label>\n" +                        
                        "<body_label>:\n" + 
                        "<body_code>" + 
                        "br label %<cmp_label>\n" + 
                        "<end_label>:\n" + 
                        "<ret> = add i32 0, 0\n"
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

        @Override 
        public CodeFragment visitNot(calculatorParser.NotContext ctx) {
                return generateUnaryOperatorCodeFragment(
                        visit(ctx.expression()),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitAnd(calculatorParser.AndContext ctx) {
                return generateBinaryOperatorCodeFragment(
                        visit(ctx.expression(0)),
                        visit(ctx.expression(1)),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitOr(calculatorParser.OrContext ctx) {
                return generateBinaryOperatorCodeFragment(
                        visit(ctx.expression(0)),
                        visit(ctx.expression(1)),
                        ctx.op.getType()
                );
        }

        @Override
        public CodeFragment visitInit(calculatorParser.InitContext ctx) {
                CodeFragment body = visit(ctx.statements());

                ST template = new ST(
                        "declare i32 @printInt(i32)\n" + 
                        "declare i32 @iexp(i32, i32)\n" + 
                        "define i32 @main() {\n" + 
                        "start:\n" + 
                        "<body_code>" + 
                        "ret i32 0\n" +
                        "}\n"
                );
                template.add("body_code", body);

                CodeFragment code = new CodeFragment();
                code.addCode(template.render());
                code.setRegister(body.getRegister());
                return code;
        }
        
        @Override
        public CodeFragment visitStatements(calculatorParser.StatementsContext ctx) {
                CodeFragment code = new CodeFragment();
                for(calculatorParser.StatementContext s: ctx.statement()) {
                        CodeFragment statement = visit(s);
                        code.addCode(statement);
                        code.setRegister(statement.getRegister());
                }
                return code;
        }

//        @Override
//        public CodeFragment visitEmp(calculatorParser.EmpContext ctx) {
//                return new CodeFragment();
//        }
        
        @Override
        public CodeFragment visitComp(calculatorParser.CompContext ctx) {
        	Integer operator = ctx.op.getType();
        	CodeFragment left = visit(ctx.expression(0));
        	CodeFragment right = visit(ctx.expression(1));

            String code_stub = "<ret> = icmp <cond> i32 <left_val>, <right_val>\n" +
            				   "<ret2> = zext i1 <ret> to i32";
            String cond = "";
            
            switch (operator) {
                    case calculatorParser.LT:
                        cond = "ult";
                        break;
                    case calculatorParser.LTE:
                    	cond = "ule";
                        break;
                    case calculatorParser.GT:
                    	cond = "ugt";
                        break;
                    case calculatorParser.GTE:
                    	cond = "uge";
                        break;
                    case calculatorParser.EQ:
                    	cond = "eq";
                        break;                            
                    case calculatorParser.NEQ:
                    	cond = "ne";
                        break;
            }
            
            ST template = new ST(
                    "<left_code>" + 
                    "<right_code>" + 
                    code_stub
            );
            template.add("left_code", left);
            template.add("right_code", right);
            template.add("cond", cond);
            template.add("left_val", left.getRegister());
            template.add("right_val", right.getRegister());
            String ret_register = this.generateNewRegister();
            String ret2_register = this.generateNewRegister();
            template.add("ret", ret_register);
            template.add("ret2", ret2_register);
            
            CodeFragment ret = new CodeFragment();
            ret.setRegister(ret2_register);
            ret.addCode(template.render());
            return ret;
            		
        }
        
}
