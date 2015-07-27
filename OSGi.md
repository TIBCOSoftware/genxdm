# Summary #

Update: this is wrong, and needs revision. On the list. We'll change this to
be 'using the tools' and 'best practices', prolly.

At the moment, this is a record of experiences in adding OSGi support. Expect
it to be somewhat chaotic. Currently, the code is in /branches/osgi-project.

At some point in the future, this will either go away (and so will this page)
because we've given it up as a bad job or an impossible task, or it will
be restructured and rewritten to help folks understand how the (successful)
OSGi integration works, and where the problems are.

# Initial Steps #

Way back at the start of the project, we tried to integrate felix. Didn't
seem to work (partly due to versioning issues, which remain a known problem).

# Second Attempt #

Following interactions with an OSGi guru, and a renewed interest in using the
facilities of OSGi for modularization and such, we undertook a new effort.

## First Phase ##

  * create a branch, so that we can play in it (/branches/osgi-project)
  * discover BNDTools, which appears to be extremely useful
  * software base:
    * maven 2
    * Eclipse 3.7
    * subversive
    * m2eclipse
    * BNDTools 1.0.0
  * add the repo in svn repo exploring (installs svn connector)
  * import existing maven projects from the branch
    * leave out the book, because it causes errors we don't care about right now
    * it gets you to install an additional thing for m2eclipse archiving
    * then i had to fix all the .project and .classpath files because of m2e updates that changed builder and nature
    * it doesn't seem to work to give a container (pom) project bndtools nature?
      * I _think_ that's the problem I first encountered
      * or it might have to do with bndtools creating a cnf in the workspace, when it has to be a sibling of the projects
      * grrrr. okay, bndtools apparently doesn't support multi-module, at a guess. commenting on github.
      * but no, you can't give a pom bndtools nature, and there can only be one project with the name cnf imported, so we're at least temporarily stymied

Summary of first phase: although the 'alpha' bndtools mentioned in a blog
posting from march 2011 had a wizard, it appears that the released bndtools
lacks the functionality mentioned. It looks as though bndtools offers no
particular advantages over pure felix: we still need to do all the parallel
management of manifest and pom manually. Some of the things that should
be happening in bndtools aren't; some things just don't work the way that
they ought to.

Next step: abandon bndtools and try doing this with pure felix and manual
management of the pom. Note that this is high maintenance. We may
dropkick the current branch and start over, as well (restructuring was
a bndtools requirement).

Whoops! wrong. It appears that a little more research (that I literally
stumbled across while looking for other things) solves most of the issues,
though it means a remarkably complex-looking root POM. But then the generated
bits start looking really pretty cool--rather nice manifests (although there
are things in there that we **really** oughta clean up).

## Second Pass ##

Working now, with Eclipse 3.7, m2e, and bndtools.