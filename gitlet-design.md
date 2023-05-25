# Gitlet Design Document
author: Chawinphat Tankuranand

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

###Gitlet (main)
Allows us to run the different commands from terminal
Main method takes in arguments

###Blob
Store contents of files
Instance: file contents as String

###Tree
Tree data structure mapping names to references to blobs and other trees
Instance: Prev, Next, Value, Branch objects
Static: Head pointer to current branch


###Commit
log message, timestamp, a mapping of file names to blob references, a parent reference, and (for merges) a second parent reference.
Instance: ALl commit metadata, SGA-1 id

###Staging Area
Files are added to staging area before being added to Tree



## 2. Algorithms

###Gitlet (main) Class
1. Main method: account for all commands that can be inputted into the program
   
2. setUpPersistence(): set up necessary file system

3. init: create new gitlet instance in current directory 
   containing one commit instance with no files and initial commit message
   
4. add:Adds a copy of the file as it currently exists to the staging area 
   Overwrite previous entry if needed
   
5. commit(Message):Save snapshot of tracked files in current commit and staging area
   only update the contents of files it is tracking that have been staged for addition at the time of commit
   clear staging area at end
   commit has SHA-id
   
6. rm: Unstage file if staged for addition
   if file tracked in current commit, stage for removal from working directory
   
7. log: loop starting from head commit
   each loop show information 
   end at intial commit
   
8. global-log:
   Iterate over files in directory
   show all commits ever made
   
9. find:
   loop through commits to print all commits with certain commit message
   
10. status:
    Loop through all instance of branch objects
    check for current branch and mark with *

11a. checkout(file name)
11b. checkout(commit id, file name)
11c. checkout(branchname)
    
12. branch(branch name): create new branch object with given name
    point branch at current head node
    code should start by running with default master branch
    
13. rm-branch(branch name):
    remove branch that has branch name
    
14. reset(commit id):
    checks out (checkout method) all files tracked by given commit
    remove tracked files not present
    
15. merge:
    merge files from given branch into current branch

###Blob Class
1. gethash()
2. getContents()
3. getName()


###Tree Class
1. add(Commit) :
2. Find(Commit ID):
   traverse backwards to find commit with correct commit ID
   returns that commit ID object  
3. getHead():
   returns current head pointer of tree


###Commit Class
1. commit():
2. methods to get each meta data
3. getParent()



Merge conflict
isMerge()
isMergeConflict()
representConflict()



## 3. Persistence

###git log / commit log
git log will be stored in a single text file which is added to everytime.
Everytime there is an addition, the add function will re update the git log variable.
This reduces the number of calls needed. 

###Trees, commits, blobs
Tree, commit, and blob objects will be stored in the .gitlet folder on hard disk, in a subfolder labelled trees
this allows for tree objects to be stored in the same place, and a singular loop can be used to iterate througha of them. 

###On start up          
When re opening gitlet, before executing any code, we must go through the saved files and find the most current commit, 
and set static variables such as the tree head pointer, and re initialize datastrucutres that utilize different class objects.
This allows use to save time spent searching through the folders everytime we desire information.

           
###stage folder
contains stage folder for files on stage
                                     
###garbage folder
contains files marked as untracked

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.




