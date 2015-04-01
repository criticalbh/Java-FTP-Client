# Java FTP Client
FTP Client written in java, able tu upload multiple files at once.

Compile:
```sh
$ javac FTPClient.java
```
Run:
```sh
$ java FTPClient -u username -p password -server server -files file1:file2:file3
```
Username, password and server are optional arguments and you can change default values in CmdArgs.java


[args4j]:http://search.maven.org/remotecontent?filepath=args4j/args4j/2.32/args4j-2.32.jar
