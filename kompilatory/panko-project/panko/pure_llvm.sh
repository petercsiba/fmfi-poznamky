#! /bin/bash
echo "=========== ZOPTIMALIZUJ $1===========" 
opt-2.9 -S -std-compile-opts llvm/$1.ll > llvm/$1.optimized.ll
echo "=========== MOTAJ $1===========" 
#echo "  == in =="
#cat $1.in
lli-2.9 -load=bin/library.so llvm/$1.optimized.ll < $1.in > llvm/$1.out
echo "=========== TEST $1============" 
#echo "  == target =="
#cat "$1.test"
#echo "  == out ==" 
#cat llvm/$1.out
echo "  == diff =="
diff "$1.test" llvm/$1.out

