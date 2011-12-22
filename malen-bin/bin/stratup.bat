@ECHO OFF

ECHO %PATH%
java -Djava.ext.dirs=../lib -jar bootstrap.jar
ECHO %DATE%
