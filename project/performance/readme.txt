To run a performance tests, do the following:

1. Copy the test.properties.template file to a location of your choosing, and rename it test.properties.

2. Modify the properties in test.properties:
	base.testCnt: how many test runs
	base.excludeOneRun: whether or not to exclude the first run
	base.includeSubtaskLevels: number of sublevel tasks for which to report data (hint: try 2)
	base.testMemoryUsage: if true, will spit out some memory usage details -- not sure how useful this is
	bridge.document: instance file for parse, serialize, navigation, validation, and transform tests
	bridge.validate.schema: schema file to use for validation test
	bridge.validate.copyTypeAnnotations=true
	bridge.stylesheet: stylesheet file to use for transform test
	bridge.baseURI: base URI for test file location
	bridge.testXXX: whether or not to run this particular test
	bridge.bridgeFactoryClass: the classname (including path) of the bridge factory
	
3. Pass that new properties file location as a command-line argument to the Performance class and execute
