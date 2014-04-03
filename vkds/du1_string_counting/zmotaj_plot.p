set   autoscale # scale axes automatically
unset log # remove any log-scaling
unset label # remove any previous labels
set xtic auto  
set ytic auto  
set terminal pdf
set key autotitle columnhead

#============= depth ===============
set output 'out/depth_10.pdf' 

set xlabel "frequency" 
set logscale x

set ylabel "depth" rotate by 90

plot "out/depth_10_BSTree.txt" using 1:2 with dots title "BSTree", \
     "out/depth_10_SplayTree.txt" using 1:2 with dots title "SplayTree"
     
set output 'out/depth_100.pdf' 

plot "out/depth_100_BSTree.txt" using 1:2 with dots title "BSTree", \
     "out/depth_100_SplayTree.txt" using 1:2 with dots title "SplayTree"
     
set output 'out/depth_1000.pdf' 

plot "out/depth_1000_BSTree.txt" using 1:2 with dots title "BSTree", \
     "out/depth_1000_SplayTree.txt" using 1:2 with dots title "SplayTree"

#============= avg(time) =================
unset log # remove any log-scaling

set dgrid3d 13,11
set output 'out/rand_time_avg.pdf'

set xlabel "L"
set xrange [] reverse

set ylabel "Sigma"
set yrange [] reverse

set zlabel "avg(Time)" rotate by 90;

splot "out/rand_BSTree.dat" using 1:2:3 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:3 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:3 with lines title "STLTree" 
      
      
#============= std(time) =================
set output 'out/rand_time_std.pdf'
set zlabel "std(Time)" rotate by 90;

splot "out/rand_BSTree.dat" using 1:2:4 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:4 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:4 with lines title "STLTree" 
      
      
#============= avg(cmp) =================
set output 'out/rand_cmp_avg.pdf'
set zlabel "avg(Comparison)" rotate by 90;

splot "out/rand_BSTree.dat" using 1:2:5 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:5 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:5 with lines title "STLTree" 
      
      
#============= std(cmp) =================
set output 'out/rand_cmp_std.pdf'
set zlabel "std(Comparison)" rotate by 90;

splot "out/rand_BSTree.dat" using 1:2:6 with lines title "BSTree", \
      "out/rand_SplayTree.dat" using 1:2:6 with lines title "SplayTree", \
      "out/rand_StlTree.dat" using 1:2:6 with lines title "STLTree" 
