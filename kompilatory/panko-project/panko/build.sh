#! /bin/bash
echo "=========== ANTLR4 panko.g4 ===========" 
java -jar panko/bin/antlr-4.1-complete.jar panko/panko.g4 -visitor
mv panko/*java panko/src/
mv panko/*tokens panko/llvm/
echo "=========== LIBRARY library.c=========="
cd panko/bin
gcc -shared -fPIC -std=c99 library.c -o library.so
cd ../..
echo "=========== JAVKA src/===========" 
cd panko/src
javac *java
mv *class ../bin
cd ../..
