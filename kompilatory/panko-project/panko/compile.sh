#! /bin/bash
echo "=========== SKOMPILUJ $1===========" 
cd panko/bin
java Compiler < ../../$1 > tmp.ll
cd ../..

echo "=========== ZOPTIMALIZUJ $1===========" 
opt-2.9 -S -std-compile-opts panko/bin/tmp.ll > $2
