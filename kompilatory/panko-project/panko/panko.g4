grammar panko;

init: statements;

statements: statement (NEWLINE statement)*;

/**
TODO: Check if rexpression is parsed uniquelly (NAME 
TODO: PAN TYPE NAME (rexpression)? 
TODO: VYMOTAJ ROLKA i  //is not a parser error
TODO: Mal by podporovať volanie externych funkcii (napriklad C-ckovych funkcii, staci iba tie, co pouzivaju kompatobilne typy)
TODO: refactor to common identifiers (function, array, variable) -- do it when types
TODO: change main to no params / name  
TODO: refactor EXTERN TYPE NAME (TYPE NAME)*    
TODO: Maybe we should change statement: KEYWORD PARAMS so we could give more specific errors. 
*/

statement: 
     SUCHY .*?                                        # Suchy
     | MEGA funkcia                                   # Main
     | EXTERN MOTAC TYPE NAME (TYPE NAME)*            # FunctionExtern
     | funkcia                                        # FunctionDefine
     | WCBOOK rexpression TYPE NAME                   # ArrayDefine
     | FREE_PRE FREE_TEPLYCH NAME                     # ArrayDelete
     | PAN TYPE NAME rexpression                      # VariableDefine
     | NAMOTAJ address rexpression                    # Assign 
     | BLOCK_START statements BLOCK_END               # Block
     | IF rexpression NEWLINE tr=statement (NEWLINE ELSE NEWLINE fa=statement)?     # If
     | WHILE rexpression NEWLINE statement            # While
     | FOR NAME rexpression NEWLINE statement         # For
     | VMOTAJ TYPE address                            # Vmotaj
     | VYMOTAJ (STRING|rexpression)                   # Vymotaj  
     | PRIMOTAJ address                               # Primotaj
     | ODMOTAJ address                                # Odmotaj 
     | POCHILL                                        # Pochill 
     | SLZY                                           # Slzy
     | rexpression                                    # Evaluate 
     |                                                # Emp 
     ;

address: 
     NAME                                             # VariableAddress
     | ROLKA rvalue NAME                              # ArrayAddress
;
  
funkcia: 
    MOTAC TYPE NAME (TYPE NAME)* NEWLINE 
    (statements NEWLINE)?
    VYPAPAJ rexpression
    ;                              

rvalue:
     op=EXP<assoc=right> rvalue rvalue            # Exp
     | op=(DIV|MUL) rvalue rvalue                 # Mul
     | op=(ADD|SUB) rvalue rvalue                 # Add
     | op=MOD rvalue rvalue                       # Mod
     | op=OR rvalue rvalue                        # Or
     | op=AND rvalue rvalue                       # And
     | op=NOT rvalue                              # Not
     | op=EQUAL rvalue rvalue                     # Equal
     | op=SMALLER rvalue rvalue                   # Smaller
     | INT                                        # Int
     | PIPKOS                                     # Pipkos
     | FAJNE                                      # Fajne
     | TISIC                                      # Tisic
     | address                                    # AddressValue 
     | BAVI rvalue                                # RandomValue
     ;
     
rexpression: 
    rvalue                                             # RValue
    | ZMOTAJ NAME (rvalue*) (rexpression)?             # FunctionValue
    ;
    
STRING: '"' ~["]* '"'; 
     
SUCHY: 'SUCHY'; 

INT: DIGIT+;
FLOAT: DIGIT+ '.' DIGIT*;
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
EXP: '^';
MOD: '%'; 
FREE_PRE: 'PRE';
FREE_TEPLYCH: 'TEPLYCH'; 
WHITESPACE: [ \t] -> skip;
NEWLINE: '\n';
MEGA: 'MEGA'; 
EXTERN: 'DZEDZINSKY'; 
MOTAC: 'MOTAC'; 
VYPAPAJ: 'VYPAPAJ'; 
ZMOTAJ: 'ZMOTAJ'; 
PAN: 'PAN'; 
NAMOTAJ: 'NAMOTAJ'; 
WCBOOK: 'WCBOOK'; 
ROLKA: 'ROLKA'; 
BLOCK_START: '{';
BLOCK_END: '}';
IF: 'AGE?';
ELSE: 'PIVO';
WHILE: 'MACKAJ';
FOR: 'POCHIPUJ';
AND: 'AND';
OR: 'OR';
NOT: 'NOT';
EQUAL: 'EQUAL';
SMALLER: 'SMALLER'; 

VMOTAJ: 'VMOTAJ'; 
VYMOTAJ: 'VYMOTAJ'; 

PRIMOTAJ: 'PRIMOTAJ'; 
ODMOTAJ: 'ODMOTAJ'; 

POCHILL: 'POCHILL'; 
SLZY: 'SLZY'; 

PIPKOS: 'PIPKOS'; 
FAJNE: 'FAJNE';
TISIC: 'TISIC';
TROSKU: 'TROSKU'; 
BAVI: 'BAVI'; 

TYPE: 'INT' | 'FLOAT' | 'CHAR';
NAME: [a-zA-Z][a-zA-Z0-9]*;
fragment DIGIT: [0-9];
