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
package org.genxdm.processor.w3c.xs;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.SmMetaBridge;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.types.SmAttributeNodeType;
import org.genxdm.xs.types.SmChoiceType;
import org.genxdm.xs.types.SmCommentNodeType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmConcatType;
import org.genxdm.xs.types.SmDocumentNodeType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmEmptyType;
import org.genxdm.xs.types.SmInterleaveType;
import org.genxdm.xs.types.SmListSimpleType;
import org.genxdm.xs.types.SmMultiplyType;
import org.genxdm.xs.types.SmNamespaceNodeType;
import org.genxdm.xs.types.SmNodeUrType;
import org.genxdm.xs.types.SmNoneType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmProcessingInstructionNodeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmTextNodeType;
import org.genxdm.xs.types.SmUnionSimpleType;

final class PrintSequenceTypeVisitor<A> implements SmSequenceTypeVisitor<A>
{
	private final StringBuilder m_sb = new StringBuilder();
	private final SmMetaBridge<A> m_metaBridge;
	private final NameSource m_nameBridge;
	private final SmNamespaceResolver m_namespaces;
	private final String m_defaultElementAndTypeNamespace;

	public PrintSequenceTypeVisitor(final SmMetaBridge<A> metaBridge, final SmNamespaceResolver namespaces, final String defaultElementAndTypeNamespace)
	{
		m_metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		m_nameBridge = m_metaBridge.getNameBridge();
		m_namespaces = PreCondition.assertArgumentNotNull(namespaces, "namespaces");
		m_defaultElementAndTypeNamespace = PreCondition.assertArgumentNotNull(defaultElementAndTypeNamespace, "defaultElementAndTypeNamespace");
	}

	public void visit(final SmNodeUrType<A> nodeType)
	{
		m_sb.append("node()");
	}

	public void visit(final SmDocumentNodeType<A> documentNodeType)
	{
		if (!m_metaBridge.sameAs(documentNodeType, m_metaBridge.documentType(null)))
		{
			m_sb.append("document-node(");
			documentNodeType.getContentType().accept(this);
			m_sb.append(")");
		}
		else
		{
			m_sb.append("document-node()");
		}
	}

	public void visit(final SmCommentNodeType<A> commentNodeType)
	{
		m_sb.append("comment()");
	}

	public void visit(final SmProcessingInstructionNodeType<A> processingInstructionNodeType)
	{
		final String name = processingInstructionNodeType.getName();
		if (null != name)
		{
			m_sb.append("processing-instruction('").append(name).append("')");
		}
		else
		{
			m_sb.append("processing-instruction()");
		}
	}

	public void visit(final SmNamespaceNodeType<A> namespaceNodeType)
	{
		m_sb.append("namespace()");
	}

	public void visit(SmTextNodeType<A> textNodeType)
	{
		m_sb.append("text()");
	}

	public void visit(final SmElementNodeType<A> elementNodeType)
	{
		m_sb.append("element(");
		m_sb.append(getLexicalQName(elementNodeType.getName(), m_namespaces, m_defaultElementAndTypeNamespace, m_nameBridge));
		final SmSequenceType<A> dataType = elementNodeType.getType();
		if (!(dataType instanceof SmComplexUrType<?>))
		{
			if (null != dataType)
			{
				m_sb.append(", ");
				dataType.accept(this);
				if (elementNodeType.isNillable())
				{
					m_sb.append(", ?");
				}
			}
		}
		m_sb.append(")");
	}

	public void visit(final SmElement<A> schemaElement)
	{
		final QName name = schemaElement.getName();
		m_sb.append("schema-element(");
		m_sb.append(getLexicalQName(name, m_namespaces, null, m_nameBridge));
		m_sb.append(")");
	}

	private static String getLexicalQName(final QName name, final SmNamespaceResolver namespaces, final String defaultNamespace, final NameSource nameBridge)
	{
		if (null != name)
		{
			final String namespaceURI = name.getNamespaceURI();
			final String localPart = name.getLocalPart();
			if (null != namespaceURI)
			{
				if (namespaceURI == defaultNamespace)
				{
					return localPart.toString();
				}
				else
				{
					final String prefix = namespaces.getPrefix(namespaceURI, "", true);
					if (null != prefix)
					{
						if (null != localPart)
						{
							final String localName = localPart.toString();
							if (prefix.length() > 0)
							{
								return prefix.concat(":").concat(localName);
							}
							else
							{
								return localName;
							}
						}
						else
						{
							return prefix.concat(":*");
						}
					}
					else
					{
						return name.getPrefix();
					}
				}
			}
			else
			{
				if (null != localPart)
				{
					final String localName = localPart.toString();
					return "*:".concat(localName);
				}
				else
				{
					return "*";
				}
			}
		}
		else
		{
			return "*";
		}
	}

	public void visit(final SmSimpleType<A> simpleType)
	{
		m_sb.append(getLexicalQName(simpleType.getName(), m_namespaces, m_defaultElementAndTypeNamespace, m_nameBridge));
	}

	public void visit(final SmAttributeNodeType<A> attributeNodeType)
	{
		m_sb.append("attribute(");
		m_sb.append(getLexicalQName(attributeNodeType.getName(), m_namespaces, null, m_nameBridge));
		final SmSequenceType<A> dataType = attributeNodeType.getType();
		if (!(dataType instanceof SmSimpleUrType<?>))
		{
			if (null != dataType)
			{
				m_sb.append(", ");
				dataType.accept(this);
			}
		}
		m_sb.append(")");
	}

	public void visit(final SmAttribute<A> schemaAttribute)
	{
		final QName declaration = schemaAttribute.getName();
		m_sb.append("schema-attribute(");
		m_sb.append(getLexicalQName(declaration, m_namespaces, null, m_nameBridge));
		m_sb.append(")");
	}

	public void visit(SmNoneType<A> noneType)
	{
		m_sb.append("none");
	}

	public void visit(final SmEmptyType<A> emptyType)
	{
		m_sb.append("empty-sequence()");
	}

	public void visit(final SmChoiceType<A> choiceType)
	{
		if (m_metaBridge.sameAs(m_metaBridge.itemType(), choiceType))
		{
			m_sb.append("item()");
		}
		else if (m_metaBridge.sameAs(m_metaBridge.nodeType(), choiceType))
		{
			m_sb.append("node()");
		}
		else
		{
			choiceType.getLHS().accept(this);
			m_sb.append(" | ");
			choiceType.getRHS().accept(this);
		}
	}

	public void visit(final SmConcatType<A> concatType)
	{
		concatType.getLHS().accept(this);
		m_sb.append(" , ");
		concatType.getRHS().accept(this);
	}

	public void visit(final SmInterleaveType<A> interleaveType)
	{
		interleaveType.getLHS().accept(this);
		m_sb.append(" & ");
		interleaveType.getRHS().accept(this);
	}

	public void visit(final SmUnionSimpleType<A> unionType)
	{
		throw new UnsupportedOperationException("visit");
	}

	public void visit(final SmMultiplyType<A> multiplyType)
	{
		final SmPrimeType<A> primeType = multiplyType.prime();
		switch (multiplyType.quantifier())
		{
			case EXACTLY_ONE:
			{
				primeType.accept(this);
			}
			break;
			case OPTIONAL:
			{
				acceptWithParens(primeType);
				m_sb.append("?");
			}
			break;
			case ONE_OR_MORE:
			{
				acceptWithParens(primeType);
				m_sb.append("+");
			}
			break;
			case ZERO_OR_MORE:
			{
				acceptWithParens(primeType);
				m_sb.append("*");
			}
			break;
			case NONE:
			{
				m_sb.append("none");
			}
			break;
			default:
			{
				throw new AssertionError(multiplyType.quantifier());
			}
		}
	}

	private void acceptWithParens(final SmPrimeType<A> primeType)
	{
		switch (primeType.getKind())
		{
			case CHOICE:
			{
				if (m_metaBridge.sameAs(m_metaBridge.itemType(), primeType))
				{
					m_sb.append("item()");
				}
				else if (m_metaBridge.sameAs(m_metaBridge.nodeType(), primeType))
				{
					m_sb.append("node()");
				}
				else
				{
					m_sb.append("(");
					primeType.accept(this);
					m_sb.append(")");
				}
			}
			break;
			default:
			{
				primeType.accept(this);
			}
		}
	}

	public void visit(final SmComplexType<A> complexType)
	{
		m_sb.append(getLexicalQName(complexType.getName(), m_namespaces, m_defaultElementAndTypeNamespace, m_nameBridge));
	}

	public void visit(final SmSimpleUrType<A> simpleUrType)
	{
		m_sb.append(getLexicalQName(simpleUrType.getName(), m_namespaces, m_defaultElementAndTypeNamespace, m_nameBridge));
	}

	public void visit(final SmComplexUrType<A> complexUrType)
	{
		m_sb.append(getLexicalQName(complexUrType.getName(), m_namespaces, m_defaultElementAndTypeNamespace, m_nameBridge));
	}

	public void visit(final SmListSimpleType<A> atomicType)
	{
		throw new UnsupportedOperationException("visit");
	}

	@Override
	public String toString()
	{
		return m_sb.toString();
	}
}
