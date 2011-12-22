#! /bin/sh

echo $PATH
java -Djava.ext.dirs=../lib -jar bootstrap.jar
echo `date`
