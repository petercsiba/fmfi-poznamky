#! /bin/bash
echo "=========== SKOMPILUJ do LLVM $1===========" 
cd bin
java Compiler < ../$1.panko > ../llvm/$1.ll
echo "  --compiler.out"
less compiler.out | sed 's/(/\n/g'
cd ..

sh pure_llvm.sh $1

