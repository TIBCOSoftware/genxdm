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
package org.genxdm.bridge.axiom.enhanced;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLReporter;

import org.genxdm.bridge.axiom.AxiomFragmentBuilder;
import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.tree.CoreModelDecorator;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
import org.genxdm.bridgekit.validation.GenericValidator;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.bridgekit.xs.TypesBridgeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.Resolver;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.TypedDocumentHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.xs.SchemaComponentCache;

public final class AxiomSAProcessingContext 
    implements TypedContext<Object, XmlAtom>
{
    public AxiomSAProcessingContext(final AxiomProcessingContext context, final SchemaComponentCache schema)
	{
	    this.context = PreCondition.assertNotNull(context, "context");
        if (schema == null)
            this.cache = new SchemaCacheFactory().newSchemaCache();
        else
            this.cache = schema;
	    
		this.metaBridge = new TypesBridgeImpl();
        this.atomBridge = new XmlAtomBridge(this.cache);
        this.model = new CoreModelDecorator<Object, XmlAtom>(context.getModel(), atomBridge, cache);
        genvalid = new GenericValidator<Object, XmlAtom>(this);
	}
	
	public AtomBridge<XmlAtom> getAtomBridge()
	{
		return atomBridge;
	}

    public TypesBridge getTypesBridge()
	{
		return metaBridge;
	}

    public SchemaComponentCache getSchema()
    {
    	return cache;
    }
	public TypedModel<Object, XmlAtom> getModel()
	{
		return model;
	}

	public AxiomProcessingContext getProcessingContext()
	{
	    return context;
	}

	public TypedCursor<Object, XmlAtom> newCursor(Object node)
	{
		return new CursorOnTypedModel<Object, XmlAtom>(node, model);
	}

    public SequenceBuilder<Object, XmlAtom> newSequenceBuilder()
    {
	    return new AxiomSequenceBuilder((AxiomFragmentBuilder)context.newFragmentBuilder(), atomBridge, context.getOMFactory(), model, true);
    }
    
    @Override
    public TypedDocumentHandler<Object, XmlAtom> newDocumentHandler(SAXValidator<XmlAtom> validator, XMLReporter reporter, Resolver resolver)
    {
        return new ValidatingDocumentHandler<Object, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public Object validate(ContentGenerator source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        return genvalid.validate(source, validator, initialType);
    }
    
    @Override
    public Object validate(Object source, ValidationHandler<XmlAtom> validator, QName initialType)
    {
        return genvalid.validate(source, validator, initialType);
    }
    
    private final AxiomProcessingContext context;
	private final AtomBridge<XmlAtom> atomBridge;
	private final SchemaComponentCache cache;
	private final GenericValidator<Object, XmlAtom> genvalid;
	
	@SuppressWarnings("unused")
	private boolean locked;
	private final TypesBridge metaBridge;
	private final CoreModelDecorator<Object, XmlAtom> model;
}
