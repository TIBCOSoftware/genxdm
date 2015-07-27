# Overview #

We don't want to be too religious about our coding conventions, but we do have a few we need to establish.

Like many guidelines, these are very much aspirational, rather than being strictly followed everywhere.

# Additive #

  * Every non-anonymous class has a Javadoc description
  * Every public method in an interface has Javadoc, and every abstract class method has Javadoc
  * Every package gets javadoc description
    * Use package-info.java, not package.html
  * If code is left incomplete for later revision, use TODO in a comment

# Legal #

  * Every java file, and every file containing creative content, must contain a copyright statement
  * The copyright statement must reference ASL 2.0

# Conforming #

  * Use spaces instead of tabs
  * Indent by 4 spaces
  * Provide adequate white space: code is read more often than it is written
  * Bracket usage must be consistent:
    * For new code, opening bracket on a new line
    * For existing code, match the bracket convention in use
    * You may completely change bracket convention in a class file, to opening bracket on new line (only)

# Unwanted #

  * No `@author` tags
  * No empty `catch` blocks (at least comment why there's no code!)