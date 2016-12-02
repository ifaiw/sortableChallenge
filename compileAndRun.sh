#!/bin/bash

if [ "$#" -eq 1 ] || [ "$#" -gt 2 ]; then
	echo "To run: compileAndRun.sh [ProductsFile ListingsFile]"
	echo "If ProductsFile and ListingsFile aren't specified, will try to use products.txt and listings.txt"
	exit -1
fi

if [ $# -eq 2 ]; then
	productFile=$1
	listingFile=$2
else
	productFile="products.txt"
	listingFile="listings.txt"
fi

javac sortable/matcher/Main.java
if [ -e "sortable/matcher/Main.class" ]; then
	echo "Looks like compiling succeeded, trying to run..."
	java sortable.matcher.Main "$productFile" "$listingFile"
else
	echo "Compile failed, trying to run precompiled version..."
	java -cp compiled sortable.matcher.Main "$productFile" "$listingFile"
fi

