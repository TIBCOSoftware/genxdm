/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.base.mutable;

import org.genxdm.base.Model;
import org.genxdm.base.io.FragmentBuilder;

// This is the XQuery Update compatibility design.
// The existing methods have been thrown out wholesale.
// The new methods are based on XQuery Update.
public interface MutableModel<N> extends Model<N>
{
    // returns the target. variant: void return.
    N insertBefore(N target, Iterable<N> content);
    
    // returns the target. variant: void return.
    N insertAfter(N target, Iterable<N> content);
    
    // remove the node from the tree; return it.
    N delete(N target);
    
    // replace the target with the replacement sequence; return the replaced node.
    N replace(N target, Iterable<N> replacement);
    
    // rename a node.  Must be a named node.  TODO: what if the namespace isn't declared here?
    // throws an exception if the node is not a named node (incl doc, comment, text, and namespace)
    // (works for attribute, element, and pi)
    // return the renamed node.
    N rename(N target, QName name) throws GxmlException;
    
    // replace the value of a node (as text).  throws an exception for nodes
    // that do not have (simple) string values: doc, element, namespace
    // (works for attribute, text, comment, and pi)
    N replaceValue(N target, String value) throws GxmlException;
    
// The following three can only be used on documents and elements.
    // all three return the target container, and throw an exception if it's
    // not a container or if the content is illegal.
    // "prependChild" is more comprehensible
    N insertIntoAsFirst(N target, Iterable<N> content) throws GxmlException;
    // "appendChild" is more comprehensible
    N insertIntoAsLast(N target, Iterable<N> content) throws GxmlException;
    // some idiot wants this to exist and have an "implementation-defined position".
    // that's just bone-headed, if you ask me.  Let's blow this one off.
    N insertInto(N target, Iterable<N> content) throws GxmlException;

// The following three are only legal for element containers
    // all return the container; they throw an exception if the target is
    // not a container or the supplied parameters are illegal.
    // this one is pretty much as expected.
    N insertAttributes(N target, Iterable<N> attributes) throws GxmlException;
    // this is a little stranger.  instead of treating namespaces as nodes,
    // this is used to treat them as sort of metadata related to the tree.
    // the effect is to create a namespace declaration on the element specified, though.
    N propagateNamespace(N target, String prefix, URI namespace) throws GxmlException;
    // and this one is weirder than collecting snake toenails.  Basically, this
    // is a way to destroy an entire subtree, but instead of just doing it, you
    // replace it with a single text node--and then delete the text node, I guess?
    // the replacement value can be null, too.  *sigh*  To replace the value of
    // a single text node child, you'd obviously use replaceValue, so this is
    // apparently bizarritude intended primarily to destroy a tree fragment;
    // I'm not real clear on why they couldn't just say that was what they
    // were doing--deleteContent(N target) ?
    N replaceElementContent(N target, String value) throws GxmlException;
    
// The following three belong on a *typed* mutable API.
    // this is the key piece that we should think about.  "revalidate" ought
    // to be parsimonious, and it ought to allow us to move from untyped to
    // typed models.  A possibility is the addition of a third parameter:
    // N origin, String validationMode, SequenceHandler validator
    // Another possibility is that "validate" could move onto the TypedProcessingContext.
    N revalidate(N origin, String validationMode) throws GxmlException;
    // note: the distinction between removeType and setToUntyped is that
    // removeType happens on a node in a typed tree, and recurses upward
    // through the ancestor axis.  setToUntyped happens when a typed node/fragment
    // is placed into an untyped tree, and recurses downward through the
    // descendant and attribute axes.  It's weird, and I'm not sure that
    // it's necessary, but we could discuss.
    N removeType(N target);
    N setToUntyped(N target);
    
// The following two are part of a processing model that may not be
// appropriate in a mutable tree API.  If appropriate, they may need
// to be in a separate interface/implementation, or may need very serious
// consideration in order to determine how best to implement.
    // Note that we're changing definition from the spec's "PendingUpdateList"
    // to a Iterable specialized for the "PendingUpdate" interface/class.
    Iterable<PendingUpdate> mergeUpdates(Iterable<PendingUpdate> list1, Iterable<PendingUpdate> list2);
    // does this one need a "target"? or is that part of "PendingUpdate"? return the modified tree/sequence?
    Iterable<N> applyUpdates(Iterable<PendingUpdate>, String revalidationMode, boolean inheritNamespaces);
    
// This bit is completely bizarre; it's an artifact of XQuery as a database
// replacement, so there are people who want it to be as much network
// protocol as language.  This is, basically, a REST-ful resource creation.
    // It's completely out of place in this API, though, so if it's supported,
    // it needs to go somehwhere else.
    boolean put(N node, URI target);
}
