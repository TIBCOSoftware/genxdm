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
package org.genxdm.processor.xpath.v10.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.genxdm.Feature;
import org.genxdm.Model;
import org.genxdm.ProcessingContext;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.processor.xpath.v10.XPathToolkitFactoryImpl;
import org.genxdm.processor.xpath.v10.variants.BooleanVariant;
import org.genxdm.processor.xpath.v10.variants.NumberVariant;
import org.genxdm.processor.xpath.v10.variants.StringVariant;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.NodeDynamicContext;
import org.genxdm.xpath.v10.NodeDynamicContextBuilder;
import org.genxdm.xpath.v10.StaticContext;
import org.genxdm.xpath.v10.ExprParseException;
import org.genxdm.xpath.v10.NodeIterator;
import org.genxdm.xpath.v10.NodeSetExpr;
import org.genxdm.xpath.v10.NumberExpr;
import org.genxdm.xpath.v10.StringExpr;
import org.genxdm.xpath.v10.XPathCompiler;
import org.genxdm.xpath.v10.XPathToolkit;
import org.genxdm.xpath.v10.XPathToolkitFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

// TODO: this needs full review.  we should be more complete and
// comprehensive, and we need to check, for instance, those places
// where we use assertEquals(double, double, delta) because we've
// just slapped 0.1 deltas in.  Contract-based, remember.

public abstract class XPathBase<N>
    implements ProcessingContextFactory<N>
{
    @Test
    public void errorHandling()
        throws Exception
    {
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        try
        {
            compiler.compileStringExpr("1+", sargs);

            fail();
        } 
        catch (final ExprParseException e)
        {

        }

        try
        {
            compiler.compileStringExpr("(1", sargs);

            fail();
        } 
        catch (final ExprParseException e)
        {

        }
    }

    @Test
    public void booleanAPI()
        throws Exception
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        final N contextNode = null;

        sargs.declareVariable(new QName("http://www.example.com", "x"));
        sargs.declareNamespace("p", "http://www.example.com");

        final StringExpr stringExpr = compiler.compileStringExpr(
                "concat('Hello',', ',$p:x,'!')", sargs);

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.bindVariableValue(new QName("http://www.example.com", "x"),
                new BooleanVariant<N>(true));

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final String s = stringExpr.stringFunction(pcx.getModel(), contextNode,
                dynEnv);

        assertEquals("Hello, true!", s);
    }

    @Test
    public void booleanVariable()
        throws Exception
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        sargs.declareVariable(new QName("http://www.example.com", "x"));
        sargs.declareNamespace("p", "http://www.example.com");

        final BooleanExpr compiledExpr = compiler.compileBooleanExpr("$p:x",
                sargs);

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.bindVariableValue(new QName("http://www.example.com", "x"),
                new BooleanVariant<N>(true));

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final boolean value = compiledExpr.booleanFunction(pcx.getModel(),
                null, dynEnv);

        assertEquals(true, value);
    }

    @Test
    public void concatFunction()
        throws Exception
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        sargs.declareVariable(new QName("http://www.example.com", "x"));
        sargs.declareNamespace("p", "http://www.example.com");

        final StringExpr stringExpr = compiler.compileStringExpr(
                "concat('Hello',', ',$p:x,'!')", sargs);

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.bindVariableValue(new QName("http://www.example.com", "x"),
                new StringVariant<N>("World"));

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final String s = stringExpr
                .stringFunction(pcx.getModel(), null, dynEnv);

        assertEquals("Hello, World!", s);
    }

    @Test
    public void doubleAPI()
        throws Exception
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        sargs.declareVariable(new QName("http://www.example.com", "x"));
        sargs.declareNamespace("p", "http://www.example.com");

        final StringExpr stringExpr = compiler.compileStringExpr(
                "concat('Hello',', ',$p:x,'!')", sargs);

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.bindVariableValue(new QName("http://www.example.com", "x"),
                new NumberVariant<N>(23));

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final String s = stringExpr
                .stringFunction(pcx.getModel(), null, dynEnv);

        assertEquals("Hello, 23!", s);
    }

    @Test
    public void operators()
        throws Exception
    {
        final ProcessingContext<N> pcx = newProcessingContext();

        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        final NumberExpr expr = compiler.compileNumberExpr("1 + 2 * 3", sargs);

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final double result = expr.numberFunction(pcx.getModel(), null, dynEnv);
        assertEquals(7.0d, result, 0.1d);
    }

    @Test
    public void lastFunction()
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        final NumberExpr numberExpr;
        try
        {
            numberExpr = compiler.compileNumberExpr("last()", sargs);
        } 
        catch (final ExprParseException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.setContextSize(7);

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final double x = numberExpr.numberFunction(pcx.getModel(), null,
                dynEnv);

        assertEquals(7.0, x, 0.1d);
    }

    @Test
    public void namespaceAxis()
        throws IOException
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final Model<N> model = pcx.getModel();
        final FragmentBuilder<N> builder = pcx.newFragmentBuilder();

        builder.startDocument(null, null);
        try
        {
            builder.startElement("http://a.example", "foo", "n0");
            try
            {
                builder.namespace("n0", "http://a.example");
                builder.startElement("http://b.example", "bar", "n1");
                try
                {
                    builder.namespace("n1", "http://b.example");
                    builder.text("content");
                } 
                finally
                {
                    builder.endElement();
                }
            } 
            finally
            {
                builder.endElement();
            }
        } 
        finally
        {
            builder.endDocument();
        }

        final N documentNode = builder.getNode();
        final N fooElement = model.getFirstChildElement(documentNode);
        final N barElement = model.getFirstChildElement(fooElement);

        // final GxSerializerFactory<N> sf = pcx.newSerializerFactory();

        // sf.setIndent(true);

        // final StringWriter sw = new StringWriter();

        // final GxSerializer<N> serializer = sf.newSerializer(sw);

        // serializer.write(documentNode);

        // System.out.println(sw);

        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        sargs.declareNamespace("nsb", "http://b.example");

        final NodeSetExpr expr;
        try
        {
            expr = compiler.compileNodeSetExpr("namespace::*", sargs);
        } 
        catch (final ExprParseException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.setContextPosition(5);

        final NodeDynamicContext<N> dynEnv = dargs.build();

        if (pcx.isSupported(Feature.NAMESPACE_AXIS))
        {
            final NodeIterator<N> x = expr.nodeIterator(model, barElement,
                    dynEnv);

            final HashMap<String, String> mappings = new HashMap<String, String>();
            final ArrayList<N> nodes = new ArrayList<N>();

            N namespace = x.next();
            while (null != namespace)
            {
                final String prefix = model.getLocalName(namespace);
                final String uri = model.getStringValue(namespace);
                // TODO: This should be reinstated...
                // assertTrue("The element must be the parent of the namespace node.",
                // model.isSameNode(barElement,
                // model.getParent(namespace)));
                assertNotNull("prefix", prefix);
                assertNotNull("uri", uri);

                mappings.put(prefix, uri);
                nodes.add(namespace);
                namespace = x.next();
            }
            assertEquals(3, mappings.size());
            assertEquals(mappings.size(), nodes.size());
            assertNotNull(mappings.get("n0"));
            assertNotNull(mappings.get("n1"));
            assertNotNull(mappings.get("xml"));
        }
    }

    @Test
    public void positionFunction()
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        final N contextNode = null;

        final NumberExpr numberExpr;
        try
        {
            numberExpr = compiler.compileNumberExpr("position()", sargs);
        } 
        catch (final ExprParseException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.setContextPosition(5);
        dargs.setContextSize(10);

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final double x = numberExpr.numberFunction(pcx.getModel(),
                contextNode, dynEnv);

        assertEquals(5.0, x, 0.1d);
    }

    @Test
    public void stringAPI()
    {
        final ProcessingContext<N> pcx = newProcessingContext();
        final XPathToolkitFactory factory = new XPathToolkitFactoryImpl();

        final N contextNode = null;

        final XPathToolkit tools = factory.newXPathToolkit();

        final XPathCompiler compiler = tools.newXPathCompiler();

        final StaticContext sargs = tools.newExprContextStaticArgs();

        sargs.declareVariable(new QName("http://www.example.com", "x"));
        sargs.declareNamespace("p", "http://www.example.com");

        final StringExpr stringExpr;
        try
        {
            stringExpr = compiler.compileStringExpr(
                    "concat('Hello',', ',$p:x,'!')", sargs);
        } 
        catch (final ExprParseException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
            return;
        }

        final NodeDynamicContextBuilder<N> dargs = tools
                .newExprContextDynamicArgs();

        dargs.bindVariableValue(new QName("http://www.example.com", "x"),
                new StringVariant<N>("World"));

        final NodeDynamicContext<N> dynEnv = dargs.build();

        final String s = stringExpr.stringFunction(pcx.getModel(),
                contextNode, dynEnv);

        assertEquals("Hello, World!", s);
    }
}
