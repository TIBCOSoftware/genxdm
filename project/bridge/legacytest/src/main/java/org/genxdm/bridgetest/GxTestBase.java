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
package org.genxdm.bridgetest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.genxdm.NodeKind;
import org.genxdm.Model;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.bridgekit.names.QNameComparator;

abstract public class GxTestBase<N>
    extends TestCase
    implements ProcessingContextFactory<N>
{
    protected N getAt(final Iterable<? extends N> axis, final int index)
    {
        int distance = index;

        for (final N uuid : axis)
        {
            if (0 == distance)
            {
                return uuid;
            }

            distance--;
        }

        throw new ArrayIndexOutOfBoundsException(index);
    }

    protected N getAttributeAt(final N origin, final Model<N> model,
            final int index)
    {
        return getAt(model.getAttributeAxis(origin, false), index);
    }

    protected N getChildAt(final N origin, final Model<N> model,
            final int index)
    {
        return getAt(model.getChildAxis(origin), index);
    }

    protected String getMoniker(final N node, final Model<N> model)
    {
        if (null != node)
        {
            final NodeKind nodeKind = model.getNodeKind(node);

            switch (nodeKind)
            {
                case ELEMENT:
                {
                    return "element("
                            + new QName(model.getNamespaceURI(node), model
                                    .getLocalName(node), model.getPrefix(node))
                                    .toString() + ")";
                }
                case ATTRIBUTE:
                {
                    return "attribute("
                            + new QName(model.getNamespaceURI(node), model
                                    .getLocalName(node), model.getPrefix(node))
                                    .toString() + ")";
                }
                case NAMESPACE:
                {
                    return "namespace("
                            + new QName(model.getNamespaceURI(node), model
                                    .getLocalName(node), model.getPrefix(node))
                                    .toString() + ")";
                }
                case PROCESSING_INSTRUCTION:
                {
                    return "processing-instruction("
                            + new QName(model.getNamespaceURI(node), model
                                    .getLocalName(node), model.getPrefix(node))
                                    .toString() + ")";
                }
                case TEXT:
                {
                    return "text(" + model.getStringValue(node) + ")";
                }
                case DOCUMENT:
                {
                    return "document";
                }
                case COMMENT:
                {
                    return "comment";
                }
                default:
                {
                    throw new AssertionError(model.getNodeKind(node));
                }
            }
        }
        else
        {
            return "";
        }
    }

    protected QName getName(final N node, final Model<N> model)
    {
        return new QName(model.getNamespaceURI(node), model.getLocalName(node),
                model.getPrefix(node));
    }

    protected String getNames(final Axis axis,
            final Iterable<? extends N> nodes, final Model<N> model)
    {
        switch (axis)
        {
            // Sort attributes and namespaces into QName order.
            case ATTRIBUTE:
            case NAMESPACE:
            {
                final List<QName> names = new ArrayList<QName>();
                final HashMap<QName, String> monikers = new HashMap<QName, String>();

                for (final N node : nodes)
                {
                    final QName name = getName(node, model);
                    names.add(name);
                    monikers.put(name, getMoniker(node, model));
                }
                Collections.sort(names, new QNameComparator());
                final Iterator<QName> it = names.iterator();

                if (it.hasNext())
                {
                    final QName first = it.next();

                    if (it.hasNext())
                    {
                        final StringBuilder sb = new StringBuilder();

                        sb.append(monikers.get(first));

                        while (it.hasNext())
                        {
                            sb.append(",");

                            sb.append(monikers.get(it.next()));
                        }

                        return sb.toString();
                    }
                    else
                    {
                        return monikers.get(first);
                    }
                }
                else
                {
                    return "";
                }
            }
            case ANCESTOR:
            case ANCESTOR_OR_SELF:
            case CHILD:
            case DESCENDANT:
            case DESCENDANT_OR_SELF:
            case FOLLOWING:
            case FOLLOWING_SIBLING:
            case PRECEDING:
            case PRECEDING_SIBLING:
            {
                final Iterator<? extends N> it = nodes.iterator();

                if (it.hasNext())
                {
                    final N first = it.next();

                    if (it.hasNext())
                    {
                        final StringBuilder sb = new StringBuilder();

                        sb.append(getMoniker(first, model));

                        while (it.hasNext())
                        {
                            sb.append(",");

                            sb.append(getMoniker(it.next(), model));
                        }

                        return sb.toString();
                    }
                    else
                    {
                        return getMoniker(first, model);
                    }
                }
                else
                {
                    return "";
                }
            }
            default:
            {
                throw new AssertionError(axis);
            }
        }
    }
}
