package org.genxdm.bridgetest.nodes;

import org.genxdm.bridgetest.typed.TypedTestBase;

public abstract class TypedNodeInformerBase<N, A>
    extends TypedTestBase<N, A>
{

}
/*
**
 * Returns the type name of the attribute node with the specified expanded-QName.
 * This is equivalent to retrieving the attribute node and then its type name.
 * 
 * @param parent
 *            The node that is the parent of the attribute node. Must not
 *            be null.
 * @param namespaceURI
 *            The namespace-uri part of the attribute name.  Must not be null,
 *            but may be the empty string (and typically is).
 * @param localName
 *            The local-name part of the attribute name.  Must not be null.
 * @return the equivalent of invoking getTypeName on the designated attribute
 * node, if that node exists; otherwise null. 
 *
QName getAttributeTypeName(N parent, String namespaceURI, String localName);

**
 * Returns the dm:typed-value of the attribute node with the specified expanded-QName.
 * This is equivalent to retrieving the attribute node and then its typed value.
 * 
 * @param parent
 *            The node that is the parent of the attribute node.
 *            Must not be null.
 * @param namespaceURI
 *            The namespace-uri part of the attribute name.  Must not be null,
 *            but may be the empty string (and typically is).
 * @param localName
 *            The local-name part of the attribute name.
 *            Must not be null.
 * @return the equivalent of invoking getValue on the attribute node designated,
 * if that node exists; otherwise null.
 *
Iterable<? extends A> getAttributeValue(N parent, String namespaceURI, String localName);

**
 * Return the dm:type-name of an element or attribute node.
 * Returns <code>null</code> for all other node kinds.
 * 
 * @param node
 *            The node for which the type name is required.  If null is
 *            supplied, null is returned.
 * 
 * @return the type name for the node, if it is valid and is an element
 * or attribute. An invalid or partially-validated node should return
 * xs:anyType (element) or xs:anySimpleType (attribute). An unvalidated
 * node should return xs:untyped (element) or xs:untypedAtomic (attribute).
 * Text nodes return xs:untypedAtomic.  Document, namespace, comment,
 * and processing instruction nodes return null.
 * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-type-name
 *
QName getTypeName(N node);

**
 * Returns the dm:typed-value of the node.
 * 
 * Applies to all node kinds.
 * 
 * If the node argument is <code>null</code>, then <code>null</code> is returned.
 * 
 * @param node
 *            The node for which dm:typed-value is required.  If null is supplied,
 *            null is returned.
 *
 * @return a sequence of atoms representing the typed-value of the supplied
 * node, for document, element, and attribute nodes. For namespace, comment,
 * and processing instruction nodes, returns the string value of the content.
 * For text nodes, returns the value as an xs:untypedAtomic. Invalid, unvalidated,
 * and partially-validated nodes tend to return string values variously
 * typed. null (the empty sequence) is possible in some circumstances.
 * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-typed-value
 * @see http://www.w3.org/TR/xpath-datamodel/#TypedValueDetermination
 *
Iterable<? extends A> getValue(N node); // TODO: should throw exception if
// called on an element with element-only content; required to 'raise an error'
*/