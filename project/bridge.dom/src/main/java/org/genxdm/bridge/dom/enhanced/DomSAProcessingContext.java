/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.bridge.dom.enhanced;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.filters.FilteredSequenceBuilder;
import org.genxdm.bridgekit.filters.NamespaceFixupSequenceFilter;
import org.genxdm.bridgekit.names.DefaultNamespaceRegistrar;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
import org.genxdm.bridgekit.validation.GenericValidator;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.bridgekit.xs.TypesBridgeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.Resolver;
import org.genxdm.names.NamespaceRegistrar;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.SchemaComponentCache;
import org.w3c.dom.Node;

public final class DomSAProcessingContext 
    implements TypedContext<Node, XmlAtom>
{
    public DomSAProcessingContext(DomProcessingContext parent, SchemaComponentCache schema)
    {
        if (schema == null)
            this.schema = new SchemaCacheFactory().newSchemaCache();
        else
            this.schema = schema;
        this.typesBridge = new TypesBridgeImpl();
        this.atomBridge = new XmlAtomBridge(this.schema);
        this.m_model = new DomSAModel(this);
        this.parent = PreCondition.assertNotNull(parent);
        gentour = new GenericValidator<Node, XmlAtom>(this);
        ns2pReg = new DefaultNamespaceRegistrar(null);
    }

    @Override
    public AtomBridge<XmlAtom> getAtomBridge()
    {
        return atomBridge;
    }

    @Override
    public TypesBridge getTypesBridge()
    {
        return typesBridge;
    }
    @Override
    public SchemaComponentCache getSchema()
    {
        return schema;
    }
    @Override
    public TypedModel<Node, XmlAtom> getModel()
    {
        return m_model;
    }

    @Override
    public ProcessingContext<Node> getProcessingContext()
    {
        return parent;
    }

    @Override
    public TypedCursor<Node, XmlAtom> newCursor(final Node node)
    {
        return new CursorOnTypedModel<Node, XmlAtom>(node, m_model);
    }

    @Override
    public SequenceBuilder<Node, XmlAtom> newSequenceBuilder()
    {
        return newSequenceBuilder(true);
    }
    
    @Override
    public SequenceBuilder<Node, XmlAtom> newSequenceBuilder(boolean namespaceFixup)
    {
        if (namespaceFixup)
        {
            // TODO: this is temporary; it enables namespace fixup that we
            // need, but does so by piling on the virtual calls.  fix is
            // either combining the filter and the wrapper, or pulling the
            // implementation into DomSequenceBuilder.
            SequenceFilter<XmlAtom> filter = new NamespaceFixupSequenceFilter<XmlAtom>(this);
            filter.setSchema(schema);
            filter.setAtomBridge(atomBridge);
            return new FilteredSequenceBuilder<Node, XmlAtom>(filter, new DomSequenceBuilder(parent.getDocumentBuilderFactory(), this));
        }
        return new DomSequenceBuilder(parent.getDocumentBuilderFactory(), this);
    }
    
    @Override
    public ValidatingDocumentHandler<Node, XmlAtom> newDocumentHandler(final SAXValidator<XmlAtom> validator, final XMLReporter reporter, final Resolver resolver)
    {
        return new ValidatingDocumentHandler<Node, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public Node validate(ContentGenerator source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        return gentour.validate(source, validator, initialType);
    }

    @Override
    public Node validate(Node source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        return gentour.validate(source, validator, initialType);
    }
    
    @Override
    public Map<String, String> getNamespaceRegistry()
    {
        return ns2pReg.getNamespaceRegistry();
    }

    @Override
    public String getRegisteredPrefix(String namespace)
    {
        return ns2pReg.getRegisteredPrefix(namespace);
    }

    @Override
    public void registerNamespace(String namespace, String prefix)
    {
        ns2pReg.registerNamespace(namespace, prefix);
    }

    @Override
    public void registerNamespaces(Map<String, String> nsToPrefixMap)
    {
        ns2pReg.registerNamespaces(nsToPrefixMap);
    }

    private final DomProcessingContext parent;
    private final XmlAtomBridge atomBridge;
    private final TypesBridge typesBridge;
    private final SchemaComponentCache schema;
    private final GenericValidator<Node, XmlAtom> gentour;
    private final NamespaceRegistrar ns2pReg;

    private final DomSAModel m_model;
}
