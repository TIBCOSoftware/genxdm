/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.nodes.Bookmark;

/** Base class for deriving contract-based test cases.
 *
 * TestBase should be extended by a conformance test case.  Because
 * TestBase implements TestCase, every method beginning with "test"
 * will be tested.  Each method should test some portion of the contract
 * established by the interface which it is testing, and be appropriately
 * named.
 *
 * Because TestBase implements ProcessingContextFactory, each test, or the
 * test setup, should start by retrieving a ProcessingContext, calling
 * newProcessingContext.  As a consequence, it is trivially easy to specialize
 * the test case for each new bridge; each bridge adds a simple implementation
 * in which the only added method is the implementation of newProcessingContext().
 *
 * Otherwise, TestBase provides some useful utility methods.
 *
 * This is based on the original GxTestBase implementation, simplified.
 *
 */
abstract public class TestBase<N>
    implements ProcessingContextFactory<N>
{
    
    public N createSimpleAllKindsDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        // create a simple document via the fragment builder.
        // this very simple document contains precisely one node of each node kind.

        // <doc att="value" xmlns:ns="ns"><!-- comment -->text<?target data?></doc>
        
        URI uri = null;
        try { uri = new URI(URI_PREFIX + SIMPLE_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        builder.startDocument(uri, null);
        builder.startElement(XMLConstants.NULL_NS_URI, "doc", XMLConstants.DEFAULT_NS_PREFIX);
        builder.namespace("ns", "ns");
        builder.attribute(XMLConstants.NULL_NS_URI, "att", XMLConstants.DEFAULT_NS_PREFIX, "value", null);
        builder.comment("comment");
        builder.text("text");
        builder.processingInstruction("target", "data");
        builder.endElement();
        builder.endDocument();
        
        // return the root node.
        return builder.getNode();
    }
    
    public N createComplexTestDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        URI uri = null;
        try { uri = new URI(URI_PREFIX + COMPLEX_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        final String retTab = "\n    ";
        final String tab = "    ";
        builder.startDocument(uri, null);
/* create this ant buildfile.  note the pain of programmatic creation. *sigh*
At the end of the file is something that *isn't* valid ant, but that does have
a lot of interesting namespace fun.  It's also got the text nodes.

<?xml version="1.0"?>
<project name="Hello" default="compile" xml:lang="en">
    <path id="project.class.path">
        <pathelement location="lib/" />
        <pathelement path="${java.class.path}" />
    </path>
    <fileset dir="classes" id="project.output">
        <include name="** /*.java" />
    </fileset>
    <target name="clean" description="remove intermediate files">
        <delete dir="classes"/>
    </target>
    <target name="clobber" depends="clean" description="remove all artifact files">
        <delete file="hello.jar"/>
    </target>
    <target name="compile" description="compile the Java source code to class files">
        <mkdir dir="classes"/>
        <javac srcdir="." destdir="classes">
            <classpath refid="project.class.path" />
        </javac>
    </target>
    <target name="jar" depends="compile" description="create a Jar file for the application">
        <jar destfile="hello.jar">
            <fileset refid="project.output" />
            <manifest>
                <attribute name="Main-Class" value="HelloProgram"/>
            </manifest>
        </jar>
    </target>
    <nstest xmlns="http://www.genxdm.org/nonsense" xmlns:gue="http://great.underground.empire/adventure">
        <gue:zork xmlns="http://great.underground.empire/adventure" xmlns:grue="http://great.underground.empire/adventure/eaten">
            <grue:light>It is dark.  You might be eaten by a grue.</grue:light>
            <magicword word="xyzzy">Nothing happens.</magicword>
            <!-- this is a comment node, with text siblings -->
            <?magicword plugh?>
        </gue:zork>
    </nstest>
</project>
 */
        builder.startElement(XMLConstants.NULL_NS_URI, "project", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "Hello", DtdAttributeKind.CDATA);
        builder.attribute(XMLConstants.NULL_NS_URI, "default", XMLConstants.DEFAULT_NS_PREFIX, "compile", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.XML_NS_URI, "lang", XMLConstants.XML_NS_PREFIX, "en", DtdAttributeKind.NMTOKEN);
        
        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "path", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "id", XMLConstants.DEFAULT_NS_PREFIX, "project.class.path", DtdAttributeKind.ID);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "pathelement", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "location", XMLConstants.DEFAULT_NS_PREFIX, "lib/", DtdAttributeKind.CDATA);
        builder.endElement(); // pathelement with location
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "pathelement", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "path", XMLConstants.DEFAULT_NS_PREFIX, "${java.class.path}", DtdAttributeKind.CDATA);
        builder.endElement(); // pathelement with path
        builder.text(retTab);
        builder.endElement(); // path

        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "fileset", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "dir", XMLConstants.DEFAULT_NS_PREFIX, "classes", DtdAttributeKind.CDATA);
        builder.attribute(XMLConstants.NULL_NS_URI, "id", XMLConstants.DEFAULT_NS_PREFIX, "project.output", DtdAttributeKind.ID);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "include", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "**/*.java", DtdAttributeKind.CDATA);
        builder.endElement(); // include
        builder.text(retTab);
        builder.endElement(); // fileset

        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "target", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "clean", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.NULL_NS_URI, "description", XMLConstants.DEFAULT_NS_PREFIX, "remove intermediate files", DtdAttributeKind.CDATA);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "delete", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "dir", XMLConstants.DEFAULT_NS_PREFIX, "classes", DtdAttributeKind.CDATA);
        builder.endElement(); // delete
        builder.text(retTab);
        builder.endElement(); // target clean

        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "target", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "clobber", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.NULL_NS_URI, "description", XMLConstants.DEFAULT_NS_PREFIX, "remove build artifacts", DtdAttributeKind.CDATA);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "delete", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "file", XMLConstants.DEFAULT_NS_PREFIX, "hello.jar", DtdAttributeKind.CDATA);
        builder.endElement(); // delete
        builder.text(retTab);
        builder.endElement(); // target clobber

        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "target", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "compile", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.NULL_NS_URI, "description", XMLConstants.DEFAULT_NS_PREFIX, "compile the java source code", DtdAttributeKind.CDATA);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "mkdir", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "dir", XMLConstants.DEFAULT_NS_PREFIX, "classes", DtdAttributeKind.CDATA);
        builder.endElement(); // mkdir
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "javac", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "srcdir", XMLConstants.DEFAULT_NS_PREFIX, ".", DtdAttributeKind.CDATA);
        builder.attribute(XMLConstants.NULL_NS_URI, "destdir", XMLConstants.DEFAULT_NS_PREFIX, "classes", DtdAttributeKind.CDATA);
        builder.text(retTab + tab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "classpath", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "refid", XMLConstants.DEFAULT_NS_PREFIX, "project.class.path", DtdAttributeKind.IDREF);
        builder.endElement(); // classpath
        builder.text(retTab + tab);
        builder.endElement(); // javac
        builder.text(retTab);
        builder.endElement(); // target compile

        builder.text(retTab);
        builder.startElement(XMLConstants.NULL_NS_URI, "target", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "jar", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.NULL_NS_URI, "depends", XMLConstants.DEFAULT_NS_PREFIX, "compile", DtdAttributeKind.NMTOKENS);
        builder.attribute(XMLConstants.NULL_NS_URI, "description", XMLConstants.DEFAULT_NS_PREFIX, "create the jar artifact for distribution", DtdAttributeKind.CDATA);
        builder.text(retTab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "jar", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "destfile", XMLConstants.DEFAULT_NS_PREFIX, "hello.jar", DtdAttributeKind.CDATA);
        builder.text(retTab + tab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "fileset", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "refid", XMLConstants.DEFAULT_NS_PREFIX, "project.output", DtdAttributeKind.IDREF);
        builder.endElement(); // fileset
        builder.text(retTab + tab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "manifest", XMLConstants.DEFAULT_NS_PREFIX);
        builder.text(retTab + tab + tab + tab);
        builder.startElement(XMLConstants.NULL_NS_URI, "attribute", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "name", XMLConstants.DEFAULT_NS_PREFIX, "Main-Class", DtdAttributeKind.NMTOKEN);
        builder.attribute(XMLConstants.NULL_NS_URI, "value", XMLConstants.DEFAULT_NS_PREFIX, "HelloProgram", DtdAttributeKind.CDATA);
        builder.endElement(); // attribute
        builder.text(retTab + tab + tab);
        builder.endElement(); // manifest
        builder.text(retTab + tab);
        builder.endElement(); // jar
        builder.text(retTab);
        builder.endElement(); // target jar

        final String nsgenx = "http://www.genxdm.org/nonsense";
        final String nsadv = "http://great.underground.empire/adventure";
        final String nsgrue = "http://great.underground.empire/adventure/eaten";
        builder.text(retTab);
        builder.startElement(nsgenx, "nstest", XMLConstants.DEFAULT_NS_PREFIX);
        builder.namespace(XMLConstants.DEFAULT_NS_PREFIX, nsgenx);
        builder.namespace("gue", nsadv);
        builder.text(retTab + tab);
        builder.startElement(nsadv, "zork", "gue");
        builder.namespace(XMLConstants.DEFAULT_NS_PREFIX, nsadv);
        builder.namespace("grue", nsgrue);
        builder.text(retTab + tab + tab);
        builder.startElement(nsgrue, "light", "grue");
        builder.text("It is dark. You might be eaten by a grue.");
        builder.endElement(); // grue:light
        builder.text(retTab + tab + tab);
        builder.startElement(nsadv, "magicword", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "word", XMLConstants.DEFAULT_NS_PREFIX, "xyzzy", DtdAttributeKind.CDATA);
        builder.text("Nothing happens.");
        builder.endElement(); // magicword
        builder.text(retTab + tab + tab);
        builder.comment("this is a comment node, with text siblings");
        builder.text(retTab + tab + tab);
        builder.processingInstruction("magicword", "plugh");
        builder.text(retTab + tab);
        builder.endElement(); // gue:zork
        builder.text(retTab);
        builder.endElement(); // nstest

        builder.text("\n");
        builder.endElement(); // project
        builder.endDocument();
        return builder.getNode();
    }
    
    public N createIdsAndRefsTestDocument(FragmentBuilder<N> builder)
    {
        PreCondition.assertNotNull(builder);
        URI uri = null;
        try { uri = new URI(URI_PREFIX + IDS_REFS_DOC); }
        catch (URISyntaxException urise) { /* do nothing */}
        
        final String intsubset = "<!DOCTYPE doc [\n<!ATTLIST e3 id ID #IMPLIED>\n<!ATTLIST e4 ref IDREF #IMPLIED>\n]>";
        /*
         <!DOCTYPE doc [
         <!ATTLIST e3 id ID #IMPLIED>
         <!ATTLIST e4 ref IDREF #IMPLIED>
         ]>
         <doc>
            <e1><e2><e3 id="E3" /><e4 ref="E3" /><e5 xml:id="E5" /></e2></e1>
         </doc>
         */
        
        builder.startDocument(uri, intsubset);
        builder.startElement(XMLConstants.NULL_NS_URI, "doc", XMLConstants.DEFAULT_NS_PREFIX);
        builder.startElement(XMLConstants.NULL_NS_URI, "e1", XMLConstants.DEFAULT_NS_PREFIX);
        builder.startElement(XMLConstants.NULL_NS_URI, "e2", XMLConstants.DEFAULT_NS_PREFIX);
        builder.startElement(XMLConstants.NULL_NS_URI, "e3", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "id", XMLConstants.DEFAULT_NS_PREFIX, "value", DtdAttributeKind.ID);
        builder.endElement();

        builder.startElement(XMLConstants.NULL_NS_URI, "e4", XMLConstants.DEFAULT_NS_PREFIX);
        builder.attribute(XMLConstants.NULL_NS_URI, "ref", XMLConstants.DEFAULT_NS_PREFIX, "value", DtdAttributeKind.IDREF);
        builder.endElement();

        builder.startElement(XMLConstants.NULL_NS_URI, "e5", XMLConstants.DEFAULT_NS_PREFIX);
        // this *should* work without the DtdAttributeKind being set to anything useful.
        builder.attribute(XMLConstants.XML_NS_URI, "id", XMLConstants.XML_NS_PREFIX, "value", null);
        builder.endElement();

        builder.endElement(); //e2
        builder.endElement(); //e1
        builder.endElement(); //doc
        builder.endDocument();
        return builder.getNode();
    }
    
    // this is an internal helper method to get a namespace node for testing.
    // it's got all sorts of checks to make it fail, so *don't* do that.
    // also, it *will not work* if the namespace axis isn't supported,
    // but it's the responsibility of the caller to check that first!
    protected N getNamespaceNode(Model<N> model, N element, String prefix)
    {
        PreCondition.assertNotNull(model);
        PreCondition.assertNotNull(element);
        PreCondition.assertTrue(model.isElement(element));
        PreCondition.assertNotNull(prefix);
        
        Iterable<N> namespaces = model.getNamespaceAxis(element, false);
        for (N namespace : namespaces)
        {
            if (model.getLocalName(namespace).equals(prefix))
                return namespace;
        }
        return null;
    }
    
    protected void moveToNamespace(Cursor<N> cursor, String prefix)
    {
        PreCondition.assertNotNull(cursor);
        PreCondition.assertTrue(cursor.isElement());
        Bookmark<N> bm = cursor.bookmark();
        // should we fail somehow if the namespace does not exist?
        // well, we'll get a null node, and the cursor.moveTo will blow chunks.
        cursor.moveTo(getNamespaceNode(bm.getModel(), bm.getNode(), prefix));
    }
    
    protected static final String URI_PREFIX = "http://www.genxdm.org/sample/";
    protected static final String SIMPLE_DOC = "simple";
    protected static final String COMPLEX_DOC = "complex";
    protected static final String IDS_REFS_DOC = "ids_refs";
}
