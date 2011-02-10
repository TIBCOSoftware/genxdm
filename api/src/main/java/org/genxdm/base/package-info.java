/**

This package contains the key jumping off points for the abstractions involved in using GenXDM.

<p>

<h1>Overview</h1>
The key jumping off points for GenXDM can be found in this package. The {@link org.genxdm.base.Model}
interface gives you the means to access an XML tree in a stateless fashion. The {@link Cursor}
interface provides a stateful way to do the same, but saves you the hassle of keeping track of
the node you're at. And finally, the {@link org.genxdm.base.ProcessingContext} gives you a way to
access these two abstractions, and other important parts of GenXDM.

<h1>The "N" Parameter</h1>
<p>
The <code>&lt;N&gt;</code> parameter to many GenXDM classes represents the key abstraction that powers
GenXDM. It represents the type of the underlying tree API that you're using.  If you're using DOM,
for example, the "N" parameter represents a {@link org.w3c.dom.Node}. If you're using the reference
tree implementation ("Cx"), the "N" parameter corresponds to <code>org.genxdm.bridge.cx.tree.XmlNode</code>.
Since GenXDM lets you transparently switch between tree models at runtime, this "N" parameter gives
you the type safety to ensure that you're using the correct instances of interfaces needed to
access the underlying tree. 
</p>

<h1>The "Model"</h1>
<p>
With the {@link org.genxdm.base.Model} class, GenXDM defines a
<a href="http://en.wikipedia.org/wiki/Bridge_pattern">bridge</a> to an underlying
tree model. For those used to programming against DOM, the methods on this interface
should look familiar.
</p>

<h1>The "Cursor"</h1>
<p>
With the {@link org.genxdm.base.Cursor} class, GenXDM defines an adapter for the underlying tree.
However, rather than "getting" a child node, for example, the cursor <i>moves</i> to the child
node.
</p>

<p>In general, your code should prefer using the <code>Model</code> class whenever you need to
keep a lot of information about which nodes are relevant to your processing. The <code>Cursor</code>
class, since it preserves state, works better for moving around an XML tree when you don't need
to keep track of many nodes.
</p>
 
<h1>Getting Started</h1>
If your code is written to fully exploit GenXDM, the {@link org.genxdm.base.ProcessingContext} should
be injected into your object, or provided to your API, along with a node from the underlying tree.
Taking that processing context, you can then get the <code>Model</code>, and access the underlying
data. For example
</p>
<pre>
public <N> void myModelMethod(ProcessingContext<N> ctx, N element) {
    Model<N> model = ctx.getModel();
    String value = model.getStringValue(element);
}
</pre>
<p>Alternately, accomplishing the same functionality, but using the Cursor:
</p>
<pre>
public <N> void myCursorMethod(ProcessingContext<N> ctx, N element) {
    Cursor<N> cursor = ctx.newCursor(element);
    String value = cursor.getStringValue();
}
</pre>

<p>
 */
package org.genxdm.base;
