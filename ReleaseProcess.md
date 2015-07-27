# Introduction #

So that we do this consistently, this page captures the process whereby we do a release.

# Details #

  * Verify copyright licenses on all files in release
  * If this is the first release of a new year, verify that all files changed since the first of the year have an updated copyright date on them.
  * Confirm that all bugs marked [fixed](http://code.google.com/p/genxdm/issues/list?can=2&q=status%3AFixed+&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles) for the release have been verified.
  * Request a vote on the particular revision # planned for release
  * Tag the code included in the build.
  * Build the release from the tag (mvn clean install)
  * Create zip and tar.gz archives of the source, as well as the built binary files, using the YYYYMMDD date convention
  * Build the site (mvn site)
  * Update the site http://www.genxdm.org (mvn site-deploy)
  * Deploy to the repository http://www.genxdm.org/maven2 (mvn deploy)
  * Upload the zips and tarballs of code and source to GoogleCode, removing the earliest deprecated build and deprecating the earliest undeprecated build
  * Send out an email announcing the release
  * Post an announcement to the [wiki](http://genxdm.wordpress.com/)