/**
Provides compliance tests for developers of GenXDM bridges.

<p>This is the base package. The tests are defined as abstract base classes
in packages corresponding to the API.  Implementing compliance testing is
straightforward.</p>

<p>Start by adding the bridgetest dependency to your pom, in test scope:</p>
<pre>
&lt;dependency>
  &lt;groupId>org.genxdm&lt;/groupId>
  &lt;artifactId>bridgetest&lt;/artifactId>
  &lt;version>[,2)&lt;/version>
  &lt;scope>test&lt;/scope>
&lt;/dependency>
</pre>

<p>You will also need junit 4 (groupId junit, artifactId junit, version [4.0,5.0),
scope test).</p>

<p>Create the test tree (<code>src/main/test/reverse/domain/path</code>).
Although the tests are broken here into packages, you need not follow the
same structure.</p>

<p>The first test to instantiate is the <code>ProcessingContext</code> test.
Here's an example (complete) concrete test instantiation for that test, which
illustrates the pattern that ought to be used.  The example assumes that the
bridge is <code>com.example.xml.tree.genxdm</code>. This tree defines
<code>TreeNode</code> to be its &lt;N>ode parameter.</p>

<pre>
package com.example.xml.tree.genxdm.tests;

import com.example.xml.tree.TreeNode;
import com.example.xml.tree.genxdm.TreeProcessingContext;
import org.genxdm.bridgetest.base.ContextTestBase;

public class TreeNodeContextTest
    extends ContextTestBase&lt;TreeNode>
{
    &#x40;Override
    public TreeProcessingContext newProcessingContext()
    {
        return new TreeProcessingContext();
    }
}
</pre>

<p>As soon as this is compiled, the eight tests defined in ContextTestBase
will be performed using the "Example TreeNode" bridge.</p>

<p>It is important to specialize and concretize the tests in a certain order.
The functionality of GenXDM demands it; the test infrastructure also makes
certain demands.  The proper initial order:</p>

<ol>
  <li>bridgetest.base.ContextTestBase : the processing context test</li>
  <li>bridgetest.base.io.BuilderTestBase : the fragment builder test</li>
  <li><ul>
      <li>bridgetest.nodes.NodeInformerBase</li>
      <li>bridgetest.axes.NodeNavigatorBase</li>
      <li>bridgetest.axes.AxisNodeNavigatorBase</li>
      <li>bridgetest.base.ModelTestBase</li>
      </ul> : the model tests</li>
</ol>

<p>The subsequent sequence is less significant, but it is very important
that the context test and builder test are working before more tests are
enabled, in order to reduce the chaos (there is no way to force tests to
execute in a particular order, so it is wisest to turn each test on as the
testable functionality becomes available).  <code>bridgetest</code> relies
upon the processing context and the fragment builder to create the tree models
which are then navigated over.</p>

 **/
package org.genxdm.bridgetest;
