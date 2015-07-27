# Releases #

This is an informal list of releases, with information about each.  The list
of releases is below; informal discussion of each appears in reverse
chronological order, probably with detail increasing as the project matures.

Only the latest two releases will be generally available for download, prior
to the 1.0 release.

  * 20101111 : Initial release (no longer available)
  * 20110415 : Tax Day release (no longer available)
  * 20110627 : Independence Day release (no longer available)
  * 20110906 : Labor Day release (no longer available)
  * 20111111 : "Turn it up to eleven!" release (no longer available)
  * 20120222 : Leap day release (deprecated)
  * 20120426 : May day release (deprecated)
  * 20120606 : "Dozens" release
  * 20121221 : "End of the World" release

## "End of the World" Release ##

Since the world is set to end on 21 December 2012, we thought we ought to release the software, one last time. :-)

First release of the OSGi-native builds. GenXDM artifacts are created as OSGi bundles, using the tools specified elsewhere. In addition, we have improvements to the typed API, and have added type-awareness (typed API support) to the Axiom bridge. Numerous bug fixes. Increasing stability; we can actually contemplate the idea of a 1.0 release (though still, perhaps, faint and far off).

Tag 430 (actually, 427, plus required updates 429 and 430). Tag genxdm-20121221.

## "Dozens" Release ##

So named for the numbers in the release (a dozen and two half dozens). Further information not currently available, alas; we've done a poor job of documenting the releases here.

Revision unknown. Tag genxdm-20120606.

## May Day Release ##

No information available.

Revision unknown. Tag genxdm-20120426.

## Leap Day Release ##

No information available (**sigh**).

Revision unknown. Tag genxdm-20120222.

## "Turn it up to eleven!" Release ##

One year after our initial release, we released again, on Armistice Day 2011: the 11/11/11 release is, naturally, the "Turn it up to eleven!" release.

This is an incremental release, incorporating fewer significant changes than past releases, and working toward a more frequent release schedule.  Several packages have been marked (by the creation of package javadoc in package-info.java files) as 'mature', meaning that we do not expect significant further changes prior to the 1.0 release.

A problem with XML Schema regular expressions in the parser was resolved (code moved from the validator to make it available to both).

A problem with namespaces during typed parsing was addressed.

A number of new test skeletons were added.

Some of the schema-related abstractions in bridge kit gained increased visibility.

Some core APIs were cleaned up, others deprecated.

[Revision 289](https://code.google.com/p/genxdm/source/detail?r=289), tag genxdm-20111111.

## Labor Day Release ##

Someone has failed utterly to keep this up to date, hasn't she?  We'll come back to this to see if we can add the information, belatedly.  Sorry about that.

## Independence Day Release ##

## Tax Day Release ##

The API was significantly stabilized; the mutable API was redesigned completely
to conform to the XQuery Update Facility specification.  The XPath API and processor saw
significant attention.  In bridges, a new test harness was introduced which
resulted in rapid maturation of the Axiom bridge.  The DOM bridge showed a
number of potential issues by the time of this release.  Cx remained stable.

The conversion and i/o processors changed little.  Work began on refreshing
the typed API (and the schema parse and validation processors).

A fair amount of reorganization (modules, packages, classes) occurred.

This release is known to have been usable for a (publicly released) port of
Santuario/XML Security (which therefore works with all three tree model APIs
supported in the release) and with an unreleased XPath 2 API and processor.

[Revision 197](https://code.google.com/p/genxdm/source/detail?r=197), tag genxdm-20110415.

## Initial Release ##

This was a working release, but with a number of known issues.  The API,
DOM and Cx bridges, and the input-output and conversion processors were
most mature.

[Revision 60](https://code.google.com/p/genxdm/source/detail?r=60), tag genxdm-20101111.