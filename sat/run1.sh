#!/bin/bash

if [ $# == 0 ]
  then 
    fileroot="test"
  else
    fileroot=$1
fi

java -classpath ./build/jar/A2.jar A2 $fileroot.graphs $fileroot.satinput $fileroot.satoutput $fileroot.mapping 1