#!/bin/bash

sh zrob.sh test/const
sh make_panko.sh test/calc
sh make_panko.sh test/loop
sh make_panko.sh test/function
sh make_panko.sh test/array
sh make_panko.sh test/global
sh make_panko.sh test/special
sh make_panko.sh test/prvocisla
sh make_panko.sh test/sort
sh make_panko.sh test/connected_graph1
sh make_panko.sh test/connected_graph2
