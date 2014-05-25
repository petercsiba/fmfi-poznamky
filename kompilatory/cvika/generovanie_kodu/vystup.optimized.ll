; ModuleID = 'vystup.ll'

declare i32 @printInt(i32)

declare i32 @iexp(i32, i32)

define i32 @main() {
start:
  %R2 = call i32 @iexp(i32 10, i32 5)
  %0 = call i32 @printInt(i32 %R2)
  %1 = call i32 @printInt(i32 30)
  %2 = call i32 @printInt(i32 10)
  br label %L7

L7:                                               ; preds = %start, %L7
  %R271 = phi i32 [ 0, %start ], [ %R32, %L7 ]
  %R32 = add i32 %R271, 1
  %3 = call i32 @printInt(i32 %R32)
  %R34 = icmp eq i32 %R32, 10
  br i1 %R34, label %L8, label %L7

L8:                                               ; preds = %L7
  %4 = call i32 @printInt(i32 0)
  %5 = call i32 @printInt(i32 1)
  %6 = call i32 @printInt(i32 30)
  %7 = call i32 @printInt(i32 -10)
  %8 = call i32 @printInt(i32 200)
  %9 = call i32 @printInt(i32 2)
  %R62 = call i32 @iexp(i32 10, i32 5)
  %10 = call i32 @printInt(i32 %R62)
  ret i32 0
}
