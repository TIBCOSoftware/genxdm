# GenXDM Proposal #

This was submitted to the Apache Software Foundation's incubator, but failed
to attract sufficient interest (in the form of mentors) to be taken to a vote.
So, retrenching, we want to preserve the overview and such here (in part because
we'll consider returning, if we build more momentum around the project).

## Contents ##



## Abstract ##

GenXDM will provide Java™ APIs, based on the XQuery Data Model, for tree
model-independent processing of untyped and typed XML, with concrete
implementations of bridges to noted tree models such as DOM and Axiom, and
processors for noted tasks such as XPath evaluation.

## Proposal ##

The GenXDM API is an implementation of the Handle/Body (or Bridge) design
pattern, leveraging and unifying existing XML tree model APIs as
implementations of the XQuery Data Model (XDM). XML tree model processing in
Java faces several problems related to the primacy of DOM in Java's history.
The design of GenXDM is intended to address these problems and to enable the
state of the art for XML processing in Java to advance.

GenXDM can operate over any tree model for which a bridge exists, and promotes
runtime injection of the model, enabling XML processors to select the model
most suitable for the task without loss of interoperability. The API is
specified by the XDM, removing the variability in properties and behaviors
characteristic of the underlying tree models.

GenXDM introduces several new processing paradigms:
  * in which XML is regarded as immutable: enables a degree of performance enhancement for existing tree models, enables more significant enhancement via custom models, and facilitates multi-thread/multi-core processing
  * in which XML can be traversed with a "cursor": enables more flexibility in what is kept in memory and how, for example facilitating large document handling
  * in which XML is "typed" with XML Schema validation and the XDM: facilitating the implementation of newer specifications like XQuery 1.0, XSLT 2.0, and XPath 2.0.
  * in which underlying tree models can be substituted at runtime: enables assessment of design trade-offs appropriate to a particular task

This proposal hopes to establish an Apache project around these new XML APIs.
Specifically we aim for this project to improve with exposure to a broader
community. We expect that the project will refine the existing APIs developed
so far, enhance the documentation, enhance existing implementations of these
APIs, and explore some of the key opportunities that these APIs open up.

## Background ##

The eXtensible Markup Language (XML) is a structured format for the textual
representation of data. Because of its utility and simplicity, XML has been
widely adopted for computer-to-computer communication over the Internet. APIs
for processing XML in Java™ were developed early, particularly the Document
Object Model, defined in Interface Definition Language and known to present a
number of challenges for developers. Many tree models have been developed to
address these challenges: JDOM, DOM4J, AxiOM, XOM, and a number of less
well-known and even proprietary models.

W3C XML Schema provides authors of XML documents for interchange with a
language for specifying the content of those documents, both structure
(elements and attribute) and datatypes (text content in elements and
attributes). XPath 1.0 provides navigation and selection of nodes in XML
trees; XSLT 1.0 a transformation language for these trees. XPath 2.0 and XSLT
2.0, together with XQuery 1.0, are based on the XPath 2.0 and XQuery 1.0 Data
Model ("XQuery Data Model," or "XDM" for short), providing a rigorous
definition of properties and behaviors for XML trees, and adding optional
schema-awareness based on W3C XML Schema.

The developers behind this contribution identified an approach that leverages
the value of existing tree models, while enabling new uses opened up by the
specifications just identified. Specifically, we thought to apply the Bridge,
or Handle/Body design pattern.

As applied to XML, these design patterns may be contrasted with the Facade, or
Wrapper pattern: where a wrapper has at least one object instance for each
node wrapped, a bridge increases weight by only a single class instance for
all trees of a given type. To ensure type-safety, GenXDM applies the Bridge
pattern by making extensive use of Java™ generics.

## Rationale ##

GenXDM was originally conceived in 2006 to address a number of fundamental issues facing the XML community:
  * multiplicity of models
  * leverage existing investment
  * performance
  * foundation for building for new specifications

### Manage the multiplicity of tree model APIs ###

Appropriately, over the years, Java has accumulated multiple XML tree APIs to
address one or more design trade-offs present in existing models. Those models
include DOM, JDOM, DOM4J, AxiOM, XOM, and even Xalan's internal Document Table
Model. However, all the other models have had to overcome the network effects
of DOM - the standards-based implementation first on the scene. This network
effect creates problems for any processing engine that chooses to work with a
model other than DOM.

By way of the handle/body pattern, and by providing a common API over all
supported tree models and by encouraging run-time injection of an appropriate
tree model, GenXDM enables application developers to choose the model best
suited to the application, or even best suited to a particular portion of an
application.

### Leverage investment in XML processor development ###

An XML processor can be loosely defined as any library of code that processes
XML. The GenXDM contributed code includes five processors, partly as exemplars
and partly to provide commonly-required functionality independent of tree
model. New processors built over the GenXDM API can be independent of the
underlying tree model. Existing processors can be incrementally refactored to
use the GenXDM API, gaining tree model independence (and often additional
functionality) in the process.

A key benefit of GenXDM is that existing processors targeted to a particular
tree model for which a bridge exists can be used as-is; a processor over DOM,
such as Xalan, can work seamlessly with a GenXDM-based processor using the DOM
bridge, with no conversion cost. Similarly, SOAP or WS-**GenXDM-based processors
can interoperate with Axis using the AxiOM bridge. This is potentially very
powerful; new and refactored processors can gain mindshare without being
forced to choose among tree models.**

GenXDM also includes a conversion processor, though its use is discouraged,
which uses the GenXDM input and output APIs to permit conversion between any
supported pair of tree models, which potentially extends the reach of
processors (at a cost; conversion is not cheap).

### Improve XML performance ###

GenXDM defines its base API as an untyped and immutable XML tree. As the API
reflects the XDM specification, it includes a standard extension to support
types (including types and typed values for instance documents, and a complete
W3C XML Schema model). This extension preserves the principle of the immutable
tree. Treating XML trees as immutable is potentially quite valuable: it
permits multiple threads to safely analyze the tree, and reduces the footprint
of the objects in memory. It also allows for the treatment of non-XML such as
tabular data and JSON as if it is XML format, while eliminating a conversion
step for use with an XML processor.

All existing tree models are mutable; GenXDM also contains a standard extension
that permits mutability, to ease the refactoring of processors and
applications. GenXDM bridges over mutable tree models cannot take full advantage
of the promise inherent in immutable trees, but can achieve some
optimizations. The encouragement of model injection also suggests that it may
become possible to design immutable tree models which can achieve further
performance gains.

Similarly, the possibility exists, in the typed extension API, to pass objects
optimized for type-aware processing, rather than performing schema validation
at each step.

Critically, once a bridge implementation is created for a particular
representation, that new implementation can be tried side-by-side with
existing tree models, to assess which is most appropriate for a particular set
of circumstances.

A "cursor" based processor may be able to work with XML document trees that
don't fit in the available RAM (depending upon the underlying tree model's
flexibility in this respect).

### Establish a foundation API for new and future XML Specifications ###

In January 2007, the W3C moved eight specification to Recommendation status
(XQuery 1.0, XQueryX 1.0, XSLT 2.0, Data Model, Functions and Operators,
Formal Semantics, Serialization and XPath 2.0). These specifications are built
upon a new data model that sports atomic values as leaf nodes on the data
model tree structure, and they depend on W3C XML Schema to provide the type
system foundation. GenXDM provides an API for the XDM.

The XDM defines seven node types, as against eleven in the XML Infoset, or
twelve in the DOM. The XDM is simpler, more consistent, and better suited for
processor/application use, and it provides the foundation for more advanced
use cases.

GenXDM opens the door to APIs and technologies both "underneath" and "above" the
bridges it defines. "Underneath" GenXDM you can find different forms of APIs to
represent data as XML. Above GenXDM, you can find some existing processors like
XPath, but perhaps also some new and unanticipated ones which would otherwise
struggle with which tree model is the least-worst compromise.

### Looking Forward ###

Given all the opportunities that GenXDM opens up for exploration, we anticipate
that the GenXDM APIs will dramatically improve the life of a developer using
XML.