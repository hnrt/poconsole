# poconsole
Simple po file editor with SWT

## Prerequites to build

Download SWT binary and source zip file from http://download.eclipse.org/eclipse/downloads/ and import it into the workspace.

This project is of eclipse. It assumes that swt.jar is located in ../org.eclipse.swt directory.

## How to build

A jar file that includes all dependencies can be built as follows:

    ./gradlew fatJar

If successful, poconsole-all-1.0.jar will be created in build/libs directory.

## How to run

    java -jar build/libs/poconsole-all-1.0.jar
