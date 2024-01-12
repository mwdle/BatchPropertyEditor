
# Bulk Property File Editor
#### Author: Michael Wadley
#### Date: 12/05/2023

## Purpose:
This console application aims to simplify and significantly hasten the process of changing a specific property value in many property files.

## Definitions:
* Property File: In the context of this application, a property file is a test file containing key-value pairs that are used to configure a program.
  * Key value pairs are written in the format of key=value, and are listed one per line.
* Deep search: A type of search that looks for a file in a folder and all of its sub-folders.

## How To Use:
** This has only been tested using a Java SDK of 17 and above **
#### The application has informative prompts to guide you through using it, but if you are unsure of something you can always refer to this section.

* Upon opening, you are prompted to specify a folder (ex: C:\someFolder) to do a deep search for property files in.
  * If you input an invalid directory path, you will be warned of an error and prompted to enter a valid directory.
  * **Warning:** This application is designed to work on Windows, this means all directory paths must be specified using backslashes (\) instead of forward slashes (/). Application behavior with forward slashes is undefined and may result in errors.
* Then you will be prompted to enter the common name shared between the property files you want to modify (ex: user.properties).
* Then you will be prompted to decide the file modification mode. There are two options:
  * Setting the value of a given key. This option means you must specify a key and a value. For example, you could specify a key 'username' and a value 'dave', then each property file containing the key 'username' would have its value set to 'dave'.
  * Setting a value given the old value. This option means you must specify an old value and a new value. For example, you could specify an old value 'dave' and a new value 'david', then each property file containing the old value 'dave' would have its value set to 'david'.
    * **Warning:** a property file can have multiple keys with the same value. This option will change all keys with the old value to the new value. Be cautious with this.
* Then, depending on which option you selected in the last step, you will be prompted to enter either a key or an old value.
* Lastly, you will be prompted to enter the new value you wish to set in the property files.
* The application will then perform a deep search for property files that match the name you specified in the directory you specified. It will then modify the property files according to the mode you specified, and output the name and path of each modified file as well as how many files were modified.
