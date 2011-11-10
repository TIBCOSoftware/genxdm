/*
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
package org.genxdm.processor.w3c.xs.impl;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.ChoiceType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.ConcatType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.InterleaveType;
import org.genxdm.xs.types.ListSimpleType;
import org.genxdm.xs.types.MultiplyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NodeUrType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.TextNodeType;
import org.genxdm.xs.types.UnionSimpleType;

final class PrintSequenceTypeVisitor implements SequenceTypeVisitor
{
    private final StringBuilder m_sb = new StringBuilder();
    private final TypesBridge m_metaBridge;
    private final NamespaceResolver m_namespaces;
    private final String m_defaultElementAndTypeNamespace;

    public PrintSequenceTypeVisitor(final TypesBridge metaBridge, final NamespaceResolver namespaces, final String defaultElementAndTypeNamespace)
    {
        m_metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
        m_namespaces = PreCondition.assertArgumentNotNull(namespaces, "namespaces");
        m_defaultElementAndTypeNamespace = PreCondition.assertArgumentNotNull(defaultElementAndTypeNamespace, "defaultElementAndTypeNamespace");
    }

    public void visit(final NodeUrType nodeType)
    {
        m_sb.append("node()");
    }

    public void visit(final DocumentNodeType documentNodeType)
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

    public void visit(final CommentNodeType commentNodeType)
    {
        m_sb.append("comment()");
    }

    public void visit(final ProcessingInstructionNodeType processingInstructionNodeType)
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

    public void visit(final NamespaceNodeType namespaceNodeType)
    {
        m_sb.append("namespace()");
    }

    public void visit(TextNodeType textNodeType)
    {
        m_sb.append("text()");
    }

    public void visit(final ElementNodeType elementNodeType)
    {
        m_sb.append("element(");
        m_sb.append(getLexicalQName(elementNodeType.getName(), m_namespaces, m_defaultElementAndTypeNamespace));
        final SequenceType dataType = elementNodeType.getType();
        if (!(dataType instanceof ComplexUrType))
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

    public void visit(final ElementDefinition schemaElement)
    {
        final QName name = schemaElement.getName();
        m_sb.append("schema-element(");
        m_sb.append(getLexicalQName(name, m_namespaces, null));
        m_sb.append(")");
    }

    private static String getLexicalQName(final QName name, final NamespaceResolver namespaces, final String defaultNamespace)
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

    public void visit(final SimpleType simpleType)
    {
        m_sb.append(getLexicalQName(simpleType.getName(), m_namespaces, m_defaultElementAndTypeNamespace));
    }

    public void visit(final AttributeNodeType attributeNodeType)
    {
        m_sb.append("attribute(");
        m_sb.append(getLexicalQName(attributeNodeType.getName(), m_namespaces, null));
        final SequenceType dataType = attributeNodeType.getType();
        if (!(dataType instanceof SimpleUrType))
        {
            if (null != dataType)
            {
                m_sb.append(", ");
                dataType.accept(this);
            }
        }
        m_sb.append(")");
    }

    public void visit(final AttributeDefinition schemaAttribute)
    {
        final QName declaration = schemaAttribute.getName();
        m_sb.append("schema-attribute(");
        m_sb.append(getLexicalQName(declaration, m_namespaces, null));
        m_sb.append(")");
    }

    public void visit(NoneType noneType)
    {
        m_sb.append("none");
    }

    public void visit(final EmptyType emptyType)
    {
        m_sb.append("empty-sequence()");
    }

    public void visit(final ChoiceType choiceType)
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

    public void visit(final ConcatType concatType)
    {
        concatType.getLHS().accept(this);
        m_sb.append(" , ");
        concatType.getRHS().accept(this);
    }

    public void visit(final InterleaveType interleaveType)
    {
        interleaveType.getLHS().accept(this);
        m_sb.append(" & ");
        interleaveType.getRHS().accept(this);
    }

    public void visit(final UnionSimpleType unionType)
    {
        throw new UnsupportedOperationException("visit");
    }

    public void visit(final MultiplyType multiplyType)
    {
        final PrimeType primeType = multiplyType.prime();
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

    private void acceptWithParens(final PrimeType primeType)
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

    public void visit(final ComplexType complexType)
    {
        m_sb.append(getLexicalQName(complexType.getName(), m_namespaces, m_defaultElementAndTypeNamespace));
    }

    public void visit(final SimpleUrType simpleUrType)
    {
        m_sb.append(getLexicalQName(simpleUrType.getName(), m_namespaces, m_defaultElementAndTypeNamespace));
    }

    public void visit(final ComplexUrType complexUrType)
    {
        m_sb.append(getLexicalQName(complexUrType.getName(), m_namespaces, m_defaultElementAndTypeNamespace));
    }

    public void visit(final ListSimpleType atomicType)
    {
        throw new UnsupportedOperationException("visit");
    }

    @Override
    public String toString()
    {
        return m_sb.toString();
    }
}
