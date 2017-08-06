# poconsole

This is an SWT application of simple .po file (gettext portable object file) editor.

## Prerequites to build

This project has been created and developed by using eclipse.

Download SWT binary and source zip file from http://download.eclipse.org/eclipse/downloads/ and import it into the workspace by using Existing Projects into Workspace.

It is assumed that swt.jar is located in ../org.eclipse.swt directory.

## How to build

A jar file that includes all dependencies can be built as follows:

    ./gradlew fatJar

If successful, poconsole-all.jar will be created in build/libs directory.

## How to run

    java -jar build/libs/poconsole-all.jar
