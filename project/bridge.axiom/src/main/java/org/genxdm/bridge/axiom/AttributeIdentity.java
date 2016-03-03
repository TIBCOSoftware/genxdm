/*
 * Copyright (c) 2011-12 TIBCO Software Inc.
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
package org.genxdm.bridge.axiom;

import java.lang.ref.WeakReference;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public class AttributeIdentity
    implements OMAttribute
{
    
    AttributeIdentity(OMAttribute attribute)
    {
        this.attribute = new WeakReference<OMAttribute>(attribute);
    }

    // none of the OMAttribute methods should ever be called on this.
    // it's defined as OMAttribute so that we can generally use <N> in coremodeldecorator
    @Override
    public String getLocalName()
    {
        return attribute.get().getLocalName();
    }

    @Override
    public OMNamespace getNamespace()
    {
        return attribute.get().getNamespace();
    }

    @Override
    public String getNamespaceURI()
    {
        return attribute.get().getNamespaceURI();
    }

    @Override
    public String getPrefix()
    {
        return attribute.get().getPrefix();
    }

    @Override
    public QName getQName()
    {
        return attribute.get().getQName();
    }

    @Override
    public void setLocalName(String arg0)
    {
        // fuck that noise.
    }

    @Override
    public OMFactory getOMFactory()
    {
        return attribute.get().getOMFactory();
    }

    @Override
    public String getAttributeType()
    {
        return attribute.get().getAttributeType();
    }

    @Override
    public String getAttributeValue()
    {
        return attribute.get().getAttributeValue();
    }

    @Override
    public OMElement getOwner()
    {
        return attribute.get().getOwner();
    }

    @Override
    public void setAttributeType(String arg0)
    {
        // fuck that noise.
    }

    @Override
    public void setAttributeValue(String arg0)
    {
        // fuck that noise.
    }

    @Override
    public void setOMNamespace(OMNamespace arg0)
    {
        // fuck that noise.
    }

    // this is the reason for this to exist
    // we change the semantics of equality back to object identity.
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AttributeIdentity) 
            return hashCode() == o.hashCode();
        else if (o instanceof OMAttribute)
            return hashCode() == System.identityHashCode(o);
        return false;
    }
    
    // we also need hashcode semantics, but we need it to be object equality
    // at the attribute level. There's a method for that!
    @Override
    public int hashCode()
    {
        return System.identityHashCode(attribute.get());
    }

    // hold the attribute as a weak reference to avoid memory loss, when
    // we happen to have a strong reference to an attribute identity somewhere.
    private final WeakReference<OMAttribute> attribute;
}
