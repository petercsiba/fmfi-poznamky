grammar calculator;

init: statements;

statements: NEWLINE* statement (NEWLINE+ statement)* NEWLINE+;

statement: 
     lvalue ASSIGN expression                                                              # Assign
     | expression                                                                          # Print
     | BLOCK_START statements BLOCK_END                                                    # Block
     | IF expression NEWLINE* tr=statement (NEWLINE* ELSE NEWLINE* fa=statement NEWLINE*)? # If
     | WHILE expression NEWLINE* statement                                                 # While
     | DO statement WHILE NEWLINE* expression                                              # Do
     ;

lvalue: STRING;
expression:
     op=('-'|'+') expression                             # Una
     | expression op=EXP<assoc=right> expression         # Exp
     | expression op=(DIV|MUL) expression                # Mul
     | expression op=(ADD|SUB) expression                # Add
     | op=NOT expression                                 # Not
     | expression op=AND expression                      # And
     | expression op=OR expression                       # Or
     | PAREN_OPEN expression PAREN_CLOSE                 # Par
     | STRING                                            # Var
     | INT                                               # Int
     | expression op=(LT|LTE|GT|GTE|EQ|NEQ) expression   # Comp
     ;

INT: DIGIT+;
FLOAT: DIGIT+ '.' DIGIT*;
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
EXP: '^';
WHITESPACE: [ \t] -> skip;
NEWLINE: '\n';
ASSIGN: '=';
BLOCK_START: '{';
BLOCK_END: '}';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
DO: 'do';
AND: 'and';
OR: 'or';
NOT: 'not';
PAREN_OPEN: '(';
PAREN_CLOSE: ')';
STRING: [a-zA-Z][a-zA-Z0-9]*;
LT: '<';
LTE: '<=';
GT: '>';
GTE: '>=';
EQ: '==';
NEQ: '!=';

fragment DIGIT: [0-9];