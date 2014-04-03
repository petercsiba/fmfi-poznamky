set   autoscale # scale axes automatically
unset log # remove any log-scaling
unset label # remove any previous labels
set xtic auto  
set ytic auto  
set terminal pdf
set key autotitle columnhead

#============= avg(time) =================
set dgrid3d 13,11
set output 'out/rand_time_avg.pdf'

set xlabel "L"
set xrange [] reverse

set ylabel "Sigma"
set yrange [] reverse

set zlabel "avg(Time)"

splot "out/rand_BSTree.dat" using 1:2:3 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:3 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:3 with lines title "STLTree" 
      
      
#============= std(time) =================
set output 'out/rand_time_std.pdf'
set zlabel "std(Time)"

splot "out/rand_BSTree.dat" using 1:2:4 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:4 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:4 with lines title "STLTree" 
      
      
#============= avg(cmp) =================
set output 'out/rand_cmp_avg.pdf'
set zlabel "avg(Comparison)"

splot "out/rand_BSTree.dat" using 1:2:5 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:5 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:5 with lines title "STLTree" 
      
      
#============= std(cmp) =================
set output 'out/rand_cmp_std.pdf'
set zlabel "std(Comparison)"

splot "out/rand_BSTree.dat" using 1:2:6 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:6 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:6 with lines title "STLTree" 
