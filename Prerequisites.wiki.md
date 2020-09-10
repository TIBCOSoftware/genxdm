# Command Line

Updated September 2020.

The preferred development environment uses JDK 1.8 (or a later version
configured as 1.8 source/binary), with the Eclipse IDE, configured to support
both git and the internal version control environment (known to be subversion
for some committers), plus the BndTools plugin to handle OSGi-ness.

Up to version 1.5.0, we used JDK 1.7 max. At 1.5.1, we transitioned to 1.8.

Note that you _must_ do a command line build (mvn clean install) before
any commits. The project is defined in terms of the command line tools;
this information on developing with an IDE is helpful, not required.

# Eclipse

Use Eclipse 4.7.3. It's tested/working. Install, but _do not_ do an update
of the software (as soon as you do that, it stops being 4.7.3 and everything
goes sideways). We prefer 4.7.3 because it's the most recent version that
supports our plugin set. Once you've got Eclipse running, install m2eclipse
and Subversive, from the "install new software" menu. Both are part of the
Eclipse release for Oxygen (4.7). Add a new site, named BndTools, and give
it the backlevel (but works with 4.7) URL: https://dl.bintray.com/bndtools/bndtools/4.0.0/
Then install all three BndTools components. Also, make sure that you
have a connector for subversion.

That's it. Now import your maven projects.

Note: in my installation, at least, if "Build Automatically" is on in 4.7.3,
it builds continuously, starting a new build as soon as the old one completes.
If that happens to you, disable it, and build when you make changes.

## Importing Projects

Easy. Select File|Import, choose the maven section, and select import existing 
maven projects. Browse to the 'project' directory. The whole set of related 
poms will be discovered (and automatically selected). Accept the selection, and 
wait for the initial build to complete.

## Note

You can use any development environment you like, so long as the result is
able to build in maven 3 using the supplied pom.xml (and bnd.bnd for OSGi
metadata). Eclipse is preferred because it's what we've used, successfully,
and we know how it works with our code. Likewise the m2e, git, subversion,
and bndtools plugins. But if your code works in Eclipse, not at the command
line, don't check it in. It must build at the command line to be accepted.
