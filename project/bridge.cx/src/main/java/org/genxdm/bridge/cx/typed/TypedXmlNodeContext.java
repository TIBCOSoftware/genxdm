/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.typed;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.filters.FilteredSequenceBuilder;
import org.genxdm.bridgekit.filters.NamespaceFixupSequenceFilter;
import org.genxdm.bridgekit.names.DefaultNamespaceRegistrar;
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
import org.genxdm.typed.ValidatorFactory;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.SchemaComponentCache;

public class TypedXmlNodeContext
    implements TypedContext<XmlNode, XmlAtom>
{

    public TypedXmlNodeContext(XmlNodeContext context, SchemaComponentCache schema)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        if (schema == null)
            this.schema = new SchemaCacheFactory().newSchemaCache();
        else
        	this.schema = schema;
        this.types = new TypesBridgeImpl();
        this.atoms = new XmlAtomBridge(this.schema);
        this.model = new TypedXmlNodeModel(atoms);
        gator = new GenericValidator<XmlNode, XmlAtom>(this);
        ns2pReg = new DefaultNamespaceRegistrar(null);
    }
    
    @Override
    public AtomBridge<XmlAtom> getAtomBridge()
    {
        return atoms;
    }

    @Override
    public TypesBridge getTypesBridge()
    {
        return types;
    }

    @Override
    public SchemaComponentCache getSchema()
    {
    	return schema;
    }
    
    @Override
    public TypedModel<XmlNode, XmlAtom> getModel()
    {
        return model;
    }

    @Override
    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    @Override
    public TypedCursor<XmlNode, XmlAtom> newCursor(final XmlNode node)
    {
        return new TypedXmlNodeCursor(this, node);
    }

    @Override
    public SequenceBuilder<XmlNode, XmlAtom> newSequenceBuilder()
    {
        return newSequenceBuilder(true);
    }

    @Override
    public SequenceBuilder<XmlNode, XmlAtom> newSequenceBuilder(boolean namespaceFixup)
    {
        if (namespaceFixup)
        {
            // TODO: this is temporary; it enables namespace fixup that we
            // need, but does so by piling on the virtual calls.  fix is
            // either combining the filter and the wrapper, or pulling the
            // implementation into TypedXmlNodeBuilder.
            SequenceFilter<XmlAtom> filter = new NamespaceFixupSequenceFilter<XmlAtom>(this);
            filter.setAtomBridge(atoms);
            filter.setSchema(schema);
            return new FilteredSequenceBuilder<XmlNode, XmlAtom>(filter, new TypedXmlNodeBuilder(this));
        }
        return new TypedXmlNodeBuilder(this);
    }

    @Override
    public ValidatingDocumentHandler<XmlNode, XmlAtom> newDocumentHandler(final SAXValidator<XmlAtom> validator, final XMLReporter reporter, final Resolver resolver)
    {
        // deprecated, don't use
        return new ValidatingDocumentHandler<XmlNode, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public ValidatingDocumentHandler<XmlNode, XmlAtom> newDocumentHandler(final ValidatorFactory<XmlAtom> factory, final XMLReporter reporter, final Resolver resolver)
    {
        return new ValidatingDocumentHandler<XmlNode, XmlAtom>(this, factory, reporter, resolver);
    }

    @Override
    public XmlNode validate(ContentGenerator source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        // if the supplied cursor is ours, do in-tree validation
        if (source instanceof TypedXmlNodeCursor)
            return validate( ((TypedXmlNodeCursor)source).getNode(), validator, initialType);
        return gator.validate(source, validator, initialType);
    }

    @Override
    public XmlNode validate(XmlNode source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        gator.validateTree(new TreeCursor(this, source), validator, initialType);
        return source;
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

    private final XmlNodeContext context;
    private final TypedXmlNodeModel model;
    private final XmlAtomBridge atoms;
    private final TypesBridge types;
    private final SchemaComponentCache schema;
    private final GenericValidator<XmlNode, XmlAtom> gator;
    private final NamespaceRegistrar ns2pReg;
}
