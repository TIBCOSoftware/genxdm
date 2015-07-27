# Command Line #

Short version: Maven 2, JDK 1.6, and everything should Just Work.

Longer version: actually, if those prerequisites are met, then everything really should just work. You need the 1.6 JDK and maven 2 (maven 3 is not good; at last check it had a broken documentation cycle).

# Eclipse #

Short version: Eclipse 3.7, Maven2Eclipse, Subversive, BndTools.

## Missing 'generated' directory ##

On first build in a clean environment (after going through all the stuff below), you may see errors for a missing 'generated' directory in multiple bundles. Restarting Eclipse seems to solve this.

## Installation ##

M2e and Subversive are available from the Eclipse repository.

BndTools [installation instructions](http://bndtools.org/installation.html).

After installing the necessary bits, make sure that you have a connector for subversion (best way: open svn repo browsing perspective and add the genxdm repo location--this will insure that projects are automatically associated, with no further effort on your part).

## Configuring BndTools ##

Do this after you have all of the necessary plugins (m2e, subversive, bndtools) installed), but before importing maven projects. You'll have already selected your workspace location. You already have the code checked out.

  * Open Eclipse
  * Select 'Preferences' from the appropriate menu (OS-dependent: OS X application menu; Windows Edit menu)
  * Select 'BndTools OSGi'
  * Accept the default radio button (create configuration), and click 'Next'
  * Accept the default configuration (standard) and click 'Finish'
    * You will return to the preferences dialog, and a project called 'cnf' has been created in the workspace
  * Click 'OK' to dismiss the preferences dialog
  * This is the weird part: highlight the cnf project and right-click. The context menu appears
    * Select 'Refactor|Move'
    * Browse to the location of the checked-out working copy; move cnf inside the 'project' directory (you may have to create a new folder named cnf in order to make this work, oddly; it wants to move the contents of cnf, not the directory itself)

That's it. Now import your maven projects.

## Importing Projects ##

Easy. Select File|Import, choose the maven section, and select import existing maven projects. Browse to the 'project' directory. The whole set of related poms will be discovered (and automatically selected). Accept the selection, and wait for the initial build to complete.