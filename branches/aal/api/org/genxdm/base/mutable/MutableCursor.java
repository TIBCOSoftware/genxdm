/**
 * Copyright (c) 2010 TIBCO Software Inc.
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

import org.genxdm.base.Cursor;

// parallels the MutableModel interface.
// here, we're not going to include the extremely oddball bits.
public interface MutableCursor<N>
    extends Cursor<N>
{
    void insertBefore(Iterable<N> content);

    void insertAfter(Iterable<N> content);

    // question: where does the cursor end up?  I would say we use the "following"
    // axis, because that's consonant with the document-as-sequence approach,
    // which is xquery-ish.  so the cursor is positioned on following()[1]
    void delete();

    // question of position again.  in keeping with the semantics of delete,
    // here i think the cursor gets positioned at the end of the replacement sequence.
    void replace(Iterable<N> replacement);

    // rename a node.  Must be a named node.  TODO: what if the namespace isn't declared here?
    // throws an exception if the node is not a named node (incl doc, comment, text, and namespace)
    // (works for attribute, element, and pi)
    void rename(QName name) throws GxmlException;

    // replace the value of a node (as text).  throws an exception for nodes
    // that do not have (simple) string values: doc, element, namespace
    // (works for attribute, text, comment, and pi)
    void replaceValue(String value) throws GxmlException;

// The following three can only be used on documents and elements.
    // all three throw an exception if it's
    // not a container or if the content is illegal.
    // cursor position does not change.
    // "prependChild" is more comprehensible
    void insertIntoAsFirst(Iterable<N> content) throws GxmlException;
    // "appendChild" is more comprehensible
    void insertIntoAsLast(Iterable<N> content) throws GxmlException;
    // some idiot wants this to exist and have an "implementation-defined position".
    // that's just bone-headed, if you ask me.  Let's blow this one off.
    void insertInto(Iterable<N> content) throws GxmlException;

// The following three are only legal for element containers
    // all throw an exception if the target is
    // not a container or the supplied parameters are illegal.
    // cursor position is unchanged.
    // this one is pretty much as expected.
    void insertAttributes(Iterable<N> attributes) throws GxmlException;
    // this is a little stranger.  instead of treating namespaces as nodes,
    // this is used to treat them as sort of metadata related to the tree.
    // the effect is to create a namespace declaration on the element specified, though.
    void propagateNamespace(String prefix, URI namespace) throws GxmlException;
    // and this one is weirder than collecting snake toenails.  Basically, this
    // is a way to destroy an entire subtree, but instead of just doing it, you
    // replace it with a single text node--and then delete the text node, I guess?
    // the replacement value can be null, too.  *sigh*  To replace the value of
    // a single text node child, you'd obviously use replaceValue, so this is
    // apparently bizarritude intended primarily to destroy a tree fragment;
    // I'm not real clear on why they couldn't just say that was what they
    // were doing--deleteContent(N target) ?
    void replaceElementContent(String value) throws GxmlException;

// The following three belong on a *typed* mutable API.
    // this is the key piece that we should think about.  "revalidate" ought
    // to be parsimonious, and it ought to allow us to move from untyped to
    // typed models.
    void revalidate(String validationMode/*, SequenceHandler validator*/) throws GxmlException;
    // note: the distinction between removeType and setToUntyped is that
    // removeType happens on a node in a typed tree, and recurses upward
    // through the ancestor axis.  setToUntyped happens when a typed node/fragment
    // is placed into an untyped tree, and recurses downward through the
    // descendant and attribute axes.  It's weird, and I'm not sure that
    // it's necessary, but we could discuss.
    void removeType();
    void setToUntyped();

}
