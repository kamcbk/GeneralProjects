# GitLite: A Simple, Local, Version Control File Tracker


<p align="center">
<img src="https://github.com/kamcbk/GeneralProjects/blob/master/GitLite/logEx.png" width="40%" height="40%" >
</p>
<p align="center">
  Performing log operation to observe commit history.
</p>
<br>

## About
GitLite is a local version control system similar to Git. It's a mock version of Git with only local features, using basic data structures, hence the Lite in GitLite. Several commands and functions from Git are included in GitLite, from *add* and *commit* to *branch* and *reset*. Other commands, like *find*, are brand new.

Below are the following commands supported for GitLite:
* `init` - Initializes a gitlite repository.
* `add` - Adds a file to the staging area. Begins temporarily tracking new staged files.
* `commit` - Creates a savepoint/snapshot of currently tracked files. Newly added files are now continuously tracked.
* `rm` - Removes a file from being tracked in next commit/snapshot. Drops file from staging area if existing. **DOES NOT** delete the file.
* `status` - Shows current staged files, branches, files planned for removal, modified but not staged files, and untracked files.
* `branch` - Creates a new branch split off from the current commit.
* `rm-branch` - Cuts off the branch, unabling the user further access yet retaining all information saved while on the branch. Prevents from cutting branch you're currently on.
* `log` - From current head in current branch, shows history of commits all the way to the initialization of gitlite repository. Includes SHA-1 ID, time, and commit message.
* `global-log` - Same as log but returns information about every commit ever made in no specific order.
* `find` - Prints out all SHA-1 IDs of commits that contain a given commit message.
* `checkout` - Depending on inputs, `checkout` either reverts a file back to it's most recent committed snapshot, reverts a file to a past commit, or changes the head of the current brach to be pointed to another branch.
* `reset` - Resets current working directory back to a previous commit, deleting any unsaved work. 

## Limitations
* GitLite currently does not support nor track directories.
* Mainly tested on txt files. Other files may vary.
* GitLite reads on a byte level. Extremely large files may cause potential errors.

## Prerequesits
* Install jar files in lib to JDKs
* Currently using Java 12. May or may not work with other versions of Java. 

## Authors
* Kevin Marroquin
* Six methods in Utils.java and the intro (~6 lines) of UnitTest.java written by Paul Hilfinger

## Acknowledgments
This project could not have been done without the following people:
- Philip Nilsson and his [data structure analysis of Git](https://blog.jayway.com/2013/03/03/git-is-a-purely-functional-data-structure/).
- Joseph Moghadam, Paul Hilfinger, and the [CS61B](https://inst.eecs.berkeley.edu/~cs61b/fa19) course and staff. They've provide a testing ecosystem that's made testing easier.
