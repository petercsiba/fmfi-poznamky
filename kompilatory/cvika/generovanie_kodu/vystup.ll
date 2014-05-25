declare i32 @printInt(i32)
declare i32 @iexp(i32, i32)
define i32 @main() {
start:
%R0 = add i32 0, 0
%R1 = alloca i32
store i32 %R0, i32* %R1
%R2 = add i32 0, 0
call i32 @printInt (i32 %R2)
br label %L0
L0:
%R3 = load i32* %R1
%R4 = icmp eq i32 %R3, 0
%R5 = zext i1 %R4 to i32
%R9 = icmp ne i32 %R5, 0
br i1 %R9, label %L1, label %L2
L1:
%R6 = load i32* %R1
%R7 = add i32 0, 1
%R8 = add i32 %R6, %R7
store i32 %R8, i32* %R1
br label %L0
L2:
%R10 = add i32 0, 0
%R11 = add i32 0, 111111111
call i32 @printInt (i32 %R11)
%R12 = load i32* %R1
call i32 @printInt (i32 %R12)
%R13 = add i32 0, 0
%R14 = alloca i32
store i32 %R13, i32* %R14
%R15 = load i32* %R1
%R18 = icmp ne i32 %R15, 0
br i1 %R18, label %L3, label %L4
L3:
%R16 = add i32 0, 1
call i32 @printInt (i32 %R16)
br label %L5
L4:
%R17 = add i32 0, 2
call i32 @printInt (i32 %R17)
br label %L5
L5:
%R19 = add i32 0, 0
%R20 = add i32 0, 3
call i32 @printInt (i32 %R20)
ret i32 0
}
