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
package org.gxml.bridge.axiom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNamespaceImpl;
import org.gxml.base.mutable.MutableModel;

public class AxiomMutableModel
    extends AxiomModel
    implements MutableModel<Object>
{
    
    public Object adoptNode(Object target, Object source)
    {
    	throw new UnsupportedOperationException();
    }

    public Object appendChild(Object parent, Object newChild)
    {
    	OMContainer parentNode = AxiomSupport.dynamicDowncastContainer(parent);
    	OMNode childNode = AxiomSupport.staticDowncastNode(newChild);
    	parentNode.addChild(childNode);
        return null;
    }

    public Object cloneNode(Object source, boolean deep)
    {
    	throw new UnsupportedOperationException();
    }

    /**
     * TODO - this is a horribly inefficient way of implementing this function...
     */
    public Object getOwner(Object node)
    {
    	if (node instanceof OMDocument) {
    		return node;
    	}
    	else if (node instanceof OMAttribute) {
    		OMAttribute attr = AxiomSupport.staticDowncastAttribute(node);
    		return getOwner(attr.getOwner());
    	}
    	else {
    		OMNode omNode = AxiomSupport.staticDowncastNode(node);
    		return getOwner(omNode.getParent());
    	}
    }

    public Object importNode(Object target, Object source, boolean deep)
    {
    	throw new UnsupportedOperationException();
    }

    public Object insertBefore(Object parent, Object newChild, Object refChild)
    {
    	throw new UnsupportedOperationException();
    }

    public void normalize(Object node)
    {
    	throw new UnsupportedOperationException();
    }

    public void removeAttribute(Object element, String namespaceURI, String localName)
    {
    	throw new UnsupportedOperationException();
    }

    public Object removeChild(Object parent, Object oldChild)
    {
    	throw new UnsupportedOperationException();
    }

    public void removeNamespace(Object element, String prefix)
    {
    	throw new UnsupportedOperationException();
    }

    public Object replaceChild(Object parent, Object newChild, Object oldChild)
    {
    	throw new UnsupportedOperationException();
    }

    public void setAttribute(Object element, Object attribute)
    {
    	throw new UnsupportedOperationException();
    }

    public Object setAttribute(Object element, String namespaceURI, String localName, String prefix, String value)
    {
    	throw new UnsupportedOperationException();
    }

    public void setNamespace(Object element, Object namespace)
    {
    	throw new UnsupportedOperationException();
    }

    public Object setNamespace(Object element, String prefixString, String uriSymbol)
    {
    	OMElement omElem = AxiomSupport.staticDowncastElement(element);
    	OMNamespace ns = new OMNamespaceImpl(uriSymbol, prefixString);
    	omElem.setNamespace(ns);
    	return ns;
    }

}
