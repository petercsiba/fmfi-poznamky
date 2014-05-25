Skompilujeme to nasledovne:
  * antlr4 calculator.g4 -visitor
  * javac *.java
  * gcc -shared -fPIC -std=c99 library.c -o library.so
Spustenie kompilatora: 
  * java Compiler < vstup.c  > vystup.ll
Optimalizacia vygenerovaneho kodu: 
  * opt-3.2 -S -std-compile-opts vystup.ll > vystup.optimized.ll 
Spustenie llvm kodu:
  * lli-3.2 -load=./library.so vystup.optimized.ll
Spustenie interpretera: 
  *java Calc < vstup.c
