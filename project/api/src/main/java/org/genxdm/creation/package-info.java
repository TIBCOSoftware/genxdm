/*
 * Copyright (c) 2017 TIBCO Software Inc.
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
/**

<p>This package contains the interfaces that describe tools for the creation of XML instances.</p>

<h1>Overview</h1>
<p>The interfaces defined in this package have canonical (or 'reference') implementations in
the {@link org.genxdm.bridgekit.content} package. The interfaces describe five categories of
functionality:</p>

<h2>Attributes</h2>
<p>The helper interfaces are element-oriented: one creates an entire simple element
at once (name, namespaces, attributes, and textual content), or creates an entire
complex element start tag at once (name, namespaces, attributes). While namespace
bindings are simple, easily representable as a map of (string) prefix to (string)
namespace URI, attributes are potentially more complex. The {link org.genxdm.creation.Attrib}
interface describes the necessary properties for attribute creation in the context
of an element start-tag. Its subinterface, {@link org.genxdm.creation.BinaryAttrib}
provides for the representation of an attribute that contains base64Binary bytes
(or possibly hexBinary) in memory without paying the potentially enormous cost for
encoding these types as {@link java.lang.String} (which is strikingly painful given
that the internal representation of string is UTF-16).</p>

<h2>Helpers</h2>
<p>The factories for attributes are the helper interfaces: {@link org.genxdm.creation.ContentHelper}
is the base untyped interface and it has a corresponding {@link org.genxdm.creationBinaryContentHelper}
interface which differs primarily in adding methods for passing byte-streams in for the
creation of attributes or simple-content elements with binary (encoded) types. Note that
the responsibility for determining and annotating types is always left to the implementation
of the helper interface, and it is not possible to create an XML instance with typed values
or type annotations unless it is valid per some XML schema (which must be supplied to
the helper at construction).</p>

<h2>Events</h2>
<p>When instances must be created partially out of order or conditionally, this is
accomplished by using queues to store the events until they can be fired into the
helper. The queues act as factories for, and collections of {@link org.genxdm.creation.ContentEvent}
and {@link org.genxdm.creation.TypedContentEvent}. The {@link org.genxdm.creation.EventKind}
enumeration indicates the type of the event, each corresponding approximately to one
of the methods found in {@link org.genxdm.io.ContentHandler} or {@link org.genxdm.typed.io.SequenceHandler}.</p>

<h2>Queues</h2>
<p>{@link org.genxdm.creation.EventEueue} and {@link org.genxdm.creation.TypedEventQueue}
are the untyped and typed queue implementations and (implicit) event factories which
extend the base content helper and binary content helper. Their difference from the
helpers they extend is in the implementation, which is targeted toward generating the
single method that each interface provides, which returns the list of generated
simple or typed content events.</p>

<h2>Copiers</h2>
<p>For the cases where an existing tree or tree fragment needs to be copied into
a tree that is under construction, the interfaces
{@link org.genxdm.creation.BranchCopier} and {@link org.genxdm.creation.TypeAwareBranchCopier}
are provided. In the reference implementation, these interfaces are implemented within
the same classes implementing the content helpers. Note that it is generally unnecessary
to implement them when implementing queues; doing so suggests making multiple copies
of the same instance, which should be avoided.</p>

<h1>Usage</h1>
<p>In general, a ContentHelper (or BinaryContentHelper) is the initial interface used.
Implementations of ContentHelper are also factories for Attrib or BinaryAttrib (the
latter is <em>significantly</em> less common). For the common case of creation of an
XML document in-order, these are sufficient.</p>

<p>When a document must be created partially out or order, or conditionally, experience
suggests that it is <em>not</em> necessary to be able to move around a partially-constructed
tree at random and to modify it at any point. For the rare cases when this is genuinely
necessary, use the mutable API ({@link org.genxdm.mutable}). More commonly, a tree may
be constructed in order when the data to be turned into XML is correct, but when it is
incorrect, the raw data should be stored into an 'error' node inside an 'errors' branch
later in the tree, or some other pattern involving: conditional creation of a branch
that must all be created at once or not at all, and possible creation of an alternate
branch later. Both of these cases can be handled by the use of Queues. Queues are
simple extensions of the "helper" interfaces, which instead of immediately building
a tree, store a set of {@link org.genxdm.creation.Event}s into a list, which can
be returned when the condition is fulfilled, or the point at which the deferred branch
is to be created in the output tree has been reached. This package does not include an
event driver, but the pattern is easily determined from the reference implementation
in {@link org.genxdm.bridgekit.content.EventQueueDriver}. The list of events is read
and fired into the helper, which has already been positioned to the proper location.</p>

<p>Finally, the 'branch copiers' are used when a portion of an XML tree has already
been instantiated as XML in-memory. Given a {@link org.genxdm.io.ContentGenerator}
(typically but not always a suitably positioned {@link org.genxdm.Cursor}) or a
{@link org.genxdm.typed.io.SequenceGenerator} (typically {@link org.genxdm.typed.TypedCursor}),
a given tree branch or fragment can be copied into the output tree with or without
type awareness (the reference implementations of the {@link org.genxdm.creation.ContentHelper}
and {@link org.genxdm.creation.BinaryContentHelper} implement the corresponding untyped
or typed copiers: {@link org.genxdm.creation.BranchCopier} and 
{@link org.genxdm.creation.TypeAwareBranchCopier}).</p>

 */
package org.genxdm.creation;
