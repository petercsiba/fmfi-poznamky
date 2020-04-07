= SPECIFICATION of the LANGUAGE PANKO =
Author: Peter Csiba (petherz@gmail.com , http://www.csip.sk/) 
Date: Ocotober 2013 - January 2014
Actual: https://github.com/Petrzlen/compilers

== Abstract == 
This document describes implementation of PANKO in version 0.1beta. If you want to be sure to see the full power of the language please read also the section 'TODO' and labels '!NOT in'. 
!Note that when using PANKO (could|is|...) we talk about the language and not the implementation.
When we dive into implementation specifics we always use PANKO 0.1beta. 

== Introduction ==

PANKO is a procedural language with variables, functions and arrays. 
There are no objects, anonymous anythings, function pointers, pointers to pointers and all this fancy stuff. PANKO doesn't need that. 
PANKO has basic types of variables ad provides automatic conversion between them. 

PANKO's syntax is inspired by C, Python, Polish (prefix) notation and most importantly our slang. It's aimed for markets with cheap
programming force as this language deeply reduces [1] compiling time and therefore saves so precious machine time. As we know there are only few languages of this kind. This and easy to grasp syntax makes PANKO special. 

PANKO also tries to compile whatever possible. That presumes the programmer isn't a noob and so the programmer is actually a PANKO. 
 
[1] This is because the simple syntax for computers to parse. 

== 1 Scope ==
This specification aims to provide basic understanding of language PANKO.
Note that the language has no '[', ']' keywords so in the following documentation [token] means a token/regexp to be explained later.  

Each line is exactly one statement. The special case is an empty statement (=do nothing) and a start and end of a block statement:
  '{' 
  statements* 
  '}'
  
  Note that both '{' and '}' must be the only token on that line.
  Deprecation warning! In future this will be replaced by block indendation as in python. 

Now we will define the basic RegExps: 
  KEYWORD: [A-Z]
  NAME: [a-z]
  TYPE: 'INT' | 'FLOAT' | 'CHAR'
  OPERATOR: [/*-+^%]
  LOGICAL_OPERATOR: 'AND' | 'OR' | 'NOT' | 'SMALLER' | 'EQUAL' 
  
  Note that as for PANKO 0.1beta only INT is available. 

Each non-special statement has the form: 
  KEYWORD [KEYWORD|NAME|TYPE|OPERATOR]* 
  
  Note that this allows us treating each statement in the same fassion (even operators have this syntax). This have many benefits but especially we can do very easy RANDOM_CALL - see POHALUZ.  

Note that  keywords are in the regexp [A-Z] and user defined tokens [a-z] so there is no possible collision between these two sets what supports easy to parse code. 

== 2 Normative references ==
At the end we provide examples of programs in PANKO to solve the following problems:
  Sort
  Finding primes
  Check if a graph is connected
  
  To check just run:
  $python judge.py panko     
   
Also we provide comprehensive tests for all constructs in PANKO: 
  array
  calc
  const
  global
  loop
  sort
  special 

  All (including sort, primes and connected_graph) could be run with 'places.sh'

== 3 Definitions and Conventions ==
Note that all parsable text (excluding comments) of PANKO 0.1beta are only:
PARSABLE_NO_COMMENT: 
  [a-zA-Z/*\-+^%{}""] 
  
  Note that \- is escaped '-'. 
  Note that {} is deprecated. 
  Note that PANKO doesn't have any separator but [ \t]+ and [\n] and that limits especially 
  function calls because the grammar doesn't know how many arguments a particular function token has. More on this in section for FUNCTION_VALUE. 
  Note that this structure resembles an CSV file.

INTEGER_VALUE: 
  [\+\-]*[0-9]+
  Note that '\-' is escaped '-'.

CHARACTER_VALUE:
  '[.]'
  Note that '.' is any character.

FLOAT_VALUE:
  INTEGER_VALUE\.[0-9]+ 

INT_CONSTANTS: 
  PIPKOS := 0 
  FAJNE := 1
  TISIC := <max_int> 

INT_RANDOM: 
  BAVI RVALUE
  Note that RVALUE is defined below. 

STRING_CONSTANT:
  "[^"]*"

PRIMITIVE_VALUE: 
  INTEGER_VALUE | CHARACTER_VALUE | FLOAT_VALUE 

MEMORY_VALUE:
  VARIABLE_ADDRESS | ARRAY_INDEX_ADDRESS
  
  Note that both 'reference' (i.e. pointer) values are here automatically dereferenced if needed. 

RVALUE: 
  OPERATOR (RVALUE
    | PRIMITIVE_VALUE
    | INT_CONSTANTS
    | INT_RANDOM
    | MEMORY_VALUE)
    
  Note that RVALUE could be recursive.

REXPRESSION: 
  RVALUE | FUNCTION_VALUE

VARIABLE_DEFINITION with keyword PAN:
  PAN TYPE=variable_type NAME=variable_name RVALUE=initial_value
  
  If defining a variable with existing name then a warning is thrown (and 'initial_value' is set to the existing one). 

VARIABLE_ASSIGNMENT with keyword NAMOTAJ:
  NAMOTAJ [NAME=declared_name|ARRAY_ACCESS] REXPRESSION
  
  If variable of 'declared_name' doesn't exists a new variable is created with default value. 
  If REXPRESSION.TYPE != NAME.TYPE then no action is taken.
    Note this will change as PANKO 1.0 provides autocasting. 
    Note that all functions return a value so no problems with casting 'null' or 'void'. 
  Note that ARRAY_ACCESS will be defined later.

VARIABLE_ADDRESS

FUNCTION_DEFINE with keyword MOTAC: 
  MOTAC TYPE=return_type NAME=function_name (TYPE NAME)* NEWLINE 
  (statements NEWLINE)? 
  VYPAPAJ rexpression
  
  If defining function with existing name then a warning is thrown and the first declaration is used.
  If defining function with same name as variable then only a warning is thrown. So PANKO allows having functions and variables with same name. 
  VYPAPAJ is equivalent to RETURN in C 
  There is no VOID keyword in PANKO as in C.

The C language main() equivalent in PANKO
  MEGA FUNCTION_DEFINE
  
  If no MEGA is found then compiler assumes the whole file (expect function declarations) is a body of MEGA MOTAC. 
  Note that MEGA MOTAC also takes arguments and NAME=function_name. Both are ignored. 'function_name' could act similary as class name of Main() in Java. 
  In PANKO 1.0: Note that it's enough to write MEGA. But who on earth writes MEGA without MOTAC if actually it is a MOTAC? In this case a warning is thrown. 
  Note that in PANKO 1.0 this would be changed.  

FUNCTION_VALUE with keyword ZMOTAJ:
  ZMOTAJ NAME=function_name RVALUE* REXPRESSION 
  
  Only the last argument could have FUNCTION_VALUE inside. This is because the compiler doesn't know how many parameters the function has and so it cannot distinguish between: 
    ZMOTAJ f g a b g c 
           f(g(a), b, g(c))
           f(g(), a, b, g())
  This is an issue on which our developers will work hard. 

ARRAY_DECLARATION with keyword WCBOOK: 
  WCBOOK RVALUE=count TYPE=elements_type NAME=array_name
  
  If array_name = some variable name then the declaration is ignored. 
    Note that this could change to take the last definition. 
  Note that zero long arrays could be declared. 

ARRAY_INDEX_ADDRESS with keyword ROLKA
  ROLKA RVALUE=index NAME=array_name

  Bad index leads to segmentation fault (hopefully). 

== 4 Compliance ==

This specification describes only the PANKO language. It makes no provision for either the library or the preprocessor. But tries to explain how the bleeding-edge compiler PANKO 0.1beta works. 

== 5 Environment ==

It could be compiled on machines where a compiler is available. Currently the machines which support Java and LLVM. 

== 6 Language == 

6.1 Lexical elements

6.1.1 Keywords
  See rule KEYWORD. 

6.1.2 Identifiers
  See rule NAME. 

6.1.2.1 Scopes of identifiers
  6.1.2.1.1 PANKO v0.1beta
    Has two scopes. 
    6.1.2.1.2.2 Global scope.
      Global scope are the first level STATEMENT-s 
      That is statements not in Local scope. 
      In the global scope all is allowed. You can use all constructs of PANKO 0.1beta
      
      The order of statements matter. PANKO will not see forward declarations. 
      So using an identifier before declaring it could lead to unexpected behaviour. 
      All statements are executed before MEGA MOTAC. 
      
      Note that in PANKO 0.1beta the global execution statements are treated as the beginning of main. 
      So if recursion on main would be possible (it is not) then this could lead to unexpected behaviour. 
      
    6.1.2.1.2.1 Local scope.  
      Statements in function definitions. 
      It is not allowed to define a function inside Local scope. 
  
  6.1.2.1.2 PANKO v1.0
    Scope is made up with double space indentation as in Python: 

    For example: 
    PAN INT in_main_scope 42
      PAN INT in_sub_scope 47 
    PRIMOTAJ in_sub_scope SUCHY PES this line makes error as PANKO in_sub_scope is no more defined

    It is allowed to declare variable / function with same name but a warning is thrown. 
    Old declaration is forgotten and new used. 

    PANKO allows declaration of functions not in the main scope. 
    
6.1.2.2 Shadowing variables 
  Occurs when declaring identifier='id' with same name as in a higher scope. 
  For example when declaring a function parameter with same 'id' as some global variable.
  In this case the global variable is 'shadowed'. That means only the local variable 
  is accessible with 'id' in the function body. But after leaving the function the global 
  variable has same value as before, i.e. it is 'unshadowed'. 
  
  This also happens in the FOR_LOOP. 
  
  PANKO 0.1beta only provides one-level shadowing, i.e. it is not allowed to have more then 2 identifiers of variables of same name. 
  
  Example: 
    PAN INT global 42
    MOTAC INT function INT global
      NAMOTAJ global + global 47
      VYPAPAJ global  
    
    VYMOTAJ function 3
    VYMOTAJ global
    
    will printout: 
    50
    42

6.1.2.2 Linkages of identifiers
EXTERN_FUNCTION_DECLARATION with keyword DZEDZINSKY:
  DZEDZINSKY MOTAC TYPE NAME=function_name (TYPE NAME)* 

  Defining extern functions. For example:
    DZEDZINSKY MOTAC INT cudzia INT a INT b 
    
    is translated to: 
    int cudzia(int a, int b)
    
  It's possible to use 'function_name' as it was defined in PANKO directly. 
  Note that 'function_name' must satisfy the syntax of PANKO, i.e. the RegExp NAME. 

6.1.2.6 Compatible type and composite type
  No composite types as struct in C or object in Python. 
  That's for noobs and not PANKOs. 

6.1.3 Constants
  6.1.3.2 Integer constants
    PIPKOS := 0
    FAJNE := 1
    TISIC := <max_int>
    Semantics of PIPKOS is FALSE and of FAJNE is TRUE. 
    Note that the above a pseudocode inspired by an ancient programming language. 

  6.1.3.1 Floating constants
    !NOT in PANKO 0.1beta
      TROSKU := min FLOAT > 0 
      Mind the floating point precision. 
      That means for most of values V holds V + TROSKU = V. 

6.1.9 Comments
  !NOT in PANKO 0.1beta
    All characters after keyword SUCHY PES are ignored by the compiler. 
    Note that it's enough to write SUCHY. But who on earth writes SUCHY without PES? 

6.2 Conversions
  !NOT in PANKO 0.1beta
    PANKO provides implicit conversions between KEY_TYPES. 

==7 Library==
  See EXTERN_FUNCTION_DECLARATION. 

  Note that PANKO doesn't allow including other files. This of course could be easily overcommed with appending files on compile time. 

==8 Consistent Renaming for Ordinary Identifiers==
  WAT 65? No. 

== 9 Internal Representation of Types ==
  Compiler uses LLVM to compile into low level languages as assembler. 
  PANKO 0.1beta was using lli-2.9

== 10 Type Analysis == 
  PANKO 0.1beta uses only INT and arrays of INT. 
  Only type analysis is done between INT, array and function 
  as explained in (VARIABLE|ARRAY|FUNCTION)_DEFINITION. 

  PANKO remembers all types 
== 11 Tree Type Computations == 

  Conditions are evaluated as in the C family of languages. 
  So only INT(0) is FALSE value and all other values are TRUE. 
  Boolean operations are special operations on INT. 

  BOOLEAN_VALUE := (PIPKOS, FAJNE)
    is kind of RVALUE
    Note that BOOLEAN_VALUE has only semantical reason - it is not a TYPE. 

  Using logical operators: 
    (AND | OR | NOT) RVALUE RVALUE

  COMPARISON
    (EQUAL | SMALLER) RVALUE RVALUE 

    Note that greater, smaller or equal and all the other comparison operators are not necessary. 

== 11.1 Execution flow
  Same as in all other languages.
  Only for block is different. 

  IF and ELSE: 
    AGE? REXPRESSION NEWLINE
      STATEMENT
    (NEWLINE 
    PIVO NEWLINE
      STATEMENT   
    )? 
      
    Note that VYMOTAJ STRING is used only for clarification

  WHILE LOOP: 
    MACKAJ REXPRESSION NEWLINE
      STATEMENT
  
  FOR LOOP: 
    POCHIPUJ NAME=variable_name REXPRESSION=to_value
      STATEMENT 
      
    
  !NOT in PANKO 0.1beta
    BREAK loop control: 
      MAM PASS
      Note that it's enough to write MAM. But who on earth writes MAM without PASS? 

  !NOT in PANKO 0.1beta
    CONTINUE loop control:
      CHILL 

== 12 Special ==
  Read from stdin: 
    VMOTAJ TYPE MEMORY_VALUE

  Write to stdout: 
    VYMOTAJ (STRING | REXPRESSION) 

  Increment: 
    PRIMOTAJ MEMORY_VALUE

  Decrement: 
    ODMOTAJ MEMORY_VALUE

  Do nothing: 
    POCHILL

  STRINGS, i.e. array of chars. 
    !NOT in PANKO 0.1beta
    PANKO provides a shortcut for creating array of chars: 
      WCBOOK 0 CHAR STRING 
      
    IN PANKO 0.1beta only use of STRING is: 
      VYMOTAJ STRING

  !NOT in PANKO 0.1beta
    Error throwing: 
    YOLO - try 
    TVOJ FOTER [CONDITION_EXPRESSION] - catch if value
    HATE - throw
    FREE - finally 
    PLACES [string] - writes to std_err a message: "Places, lebo {string}."  

  !NOT in PANKO 0.1beta
    ASSERT: 
    SPOKO [CONDITION_EXPRESSION] - assert
    PLACES [string] 
    This is a shortcut to YOLO block.

  !NOT in PANKO 0.1beta
    RANDOM_CALL: 
    POHALUZ RVALUE* REXPRESSION 
    
    Executes a random KEYWORD from PANKO language except block control, declarations and IO (could be POHALUZ). Uses as much operands as needed - if needs more it generates more. 
    Note that evaluated on compile time.
    Note that RANDOM_CALL is an REXPRESSION.  

  SLZY TVY - writes out text of a famous song (see in APPENDIX 1) 

== 13 TODO ==
  Multidimensional array
  Extern 
  DIRECT - goto
  PURE - null pointer 
  VYFAJCENY KAPOR -  
  DOGE BLOCK: MANY, MUCH, VERY, SO, SUCH 
  SURE

  variable reference, i.e. pointers  
  
== 14 APPENDEX 1 == 
Chvilku vzpomínej, je to všechno jen pár let
Na kytaru v duchu hrej, tvoje parta je tu hned
Z cigaret je modrej dým, hraje magneťák
Holka sedla na tvůj klín, nevíš ani jak,
nevíš jak.

Tvý roky bláznivý chtěly křídla pro svůj let
Dneska už možná nevíš sám proč tě tenkrát pálil svět
Chtěl jsi prachy na mejdan, byl to hloupej špás
Když jsi v noci vyšel ven, snad ses trochu třás,
trochu třás

Když tě našel noční hlídač
byl by to jen příběh bláznivýho kluka
Nebejt nože ve tvejch dětskej rukách
Nebejt strachu mohlo to bejt všechno jináč

R.:
Slzy tvý mámy šedivý stékají na polštář
Kdo tě zná, se vůbec nediví, že stárne její tvář
Nečekej úsměv od ženy, který jsi všechno vzal
Jen pro tvý touhy zborcený,
léta ztracený,
ty oči pláčou dál.


Když jsi vyšel ven, ze žalářních vrat
Možná, že jsi tenkrát chtěl znovu začínat
Poctivejma rukama, jako správnej chlap
snad se někdo ušklíb jen, že jsi křivě šláp,
křivě šláp

I když byl někdo k tobě krutej
Proč jsi znovu začal mezi svejma
Tvůj pocit křivdy se pak těžko smejvá
Když hledáš vinu vždycky jenom v druhejch. 


Ref.:...
Slzy tvý mámy šedivý stékají na polštář
Kdo tě zná, se vůbec nediví, že stárne její tvář
Nečekej úsměv od ženy, který jsi všechno vzal
Vrať jí ty touhy zborcený,
ať pro léta ztracený
nemusí plakat dál.
