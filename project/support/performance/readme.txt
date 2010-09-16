To run the performance tests:

1. Copy the test.properties.template file to a location of your choosing, and rename it test.properties.
2. Modify the properties in test.properties to point to the files you want to test:
	a. baseURI: path from which relative document & schema paths are resolved
	b. document: the instance file to test, path relative to baseURI
	c. schema: the schema file used for validation
3. Configure the test parameters in test.properties:
	a. testCnt: number of times to run the test
	b. testMemoryUsage: "true" or "false" indicating whether to record memory usage
	c. testParsing: "true" or "false" indicating whether to record parsing performance
	d. testSerialization: "true" or "false" indicating whether to record serialization performance
	e. testNavigation: "true" or "false" indicating whether to record navigation performance
	f. testValidation: "true" or "false" indicating whether to record validation performance
4. Pass that new file location as a command-line argument to one of these concrete test classes (they've all got a main method) and execute:
	a. DomOnlyPerformance
	b. DomBridgePerformance
	c. CxBridgePerformance
	d. AxiomBridgePerformance
	
Note: ask Joe for the standard.zip file which contains a 112Mb standard.xml file from xmark. It's too large to checkin to svn.  
