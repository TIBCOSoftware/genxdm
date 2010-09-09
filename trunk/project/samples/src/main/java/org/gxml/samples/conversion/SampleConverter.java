/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.gxml.samples.conversion;

import java.io.FileReader;
import java.io.StringWriter;

import org.gxml.base.Cursor;
import org.gxml.base.ProcessingContext;
import org.gxml.base.io.DocumentHandler;
import org.gxml.base.io.FragmentBuilder;

/**
 * This sample illustrates a simple, untyped conversion from an untyped source 
 * <N> node to an untyped target <No> node.  For typed conversions, make use of 
 * the proc-convert module, which also contains a convenience class for the 
 * untyped conversion performed in this sample class.  
 * 
 * @param <Nsrc> the untyped source node type
 * @param <Ntrgt> the untyped target node type
 */
public class SampleConverter<Nsrc, Ntrgt>
{
	/**
	 * Parses the document into a node.
	 * 
	 * @param pcx processing context for resulting node
	 * @param filepath the path to the source document
	 * @throws Exception so that try/catch blocks don't distract from illustration
	 */
	final static public <N> N parse(final ProcessingContext<N> pcx, final String filepath) throws Exception
	{
        final DocumentHandler<N> ihandler = pcx.newDocumentHandler();
        return ihandler.parse(new FileReader(filepath), null);
	}
	/**
	 * Serializes a node to a String.
	 * 
	 * @param pcx processing context for node to be serialized
	 * @param node the node to be serialized
	 * @throws Exception so that try/catch blocks don't distract from illustration
	 */
	final static public <N> String serialize(final ProcessingContext<N> pcx, N node) throws Exception
	{
        final DocumentHandler<N> ihandler = pcx.newDocumentHandler();
		final StringWriter iwriter = new StringWriter();
        ihandler.write(iwriter, node);
		return iwriter.toString();
	}
	/**
	 * Demonstrates an untyped conversion.  The steps are as follows: <br>
	 * <ul>
	 * <li> Parse the input source file into a source <N> node.
	 * <li> Convert that <N> node into a target <No> node.
	 * <li> Serialize both the source & target nodes to Strings.
	 * <li> Print the results.
	 * </ul> 
	 * 
	 * @param filepath the path to the source document
	 * @param srcPcx processing context for source node
	 * @param trgtPcx processing context for target node
	 * @throws Exception so that try/catch blocks don't distract from illustration
	 */
	final static public <Nsrc, Ntrgt> void convertSample(String filepath, final ProcessingContext<Nsrc> ipcx, final ProcessingContext<Ntrgt> opcx) throws Exception
	{
        final Nsrc inode = parse(ipcx, filepath);
        Ntrgt onode = untypedConversion(inode, ipcx, opcx);
		final String oString = serialize(opcx, onode);
		System.out.println(oString);
	}
	/**
	 * Performs an untyped conversion, like this: <br>
	 * <ul>
	 * <li> Create a cursor over the source node.
	 * <li> Create a builder for the target node type.
	 * <li> Write the cursor to the builder.
	 * </ul>
	 * 
	 * @param srcNode node to be converted
	 * @param srcPcx processing context for source node
	 * @param trgtPcx processing context for target node
	 */
	final static public <Nsrc, Ntrgt> Ntrgt untypedConversion(final Nsrc srcNode, final ProcessingContext<Nsrc> srcPcx, final ProcessingContext<Ntrgt> trgtPcx)
	{
		// Convert by writing a source cursor to a target builder.
		final Cursor<Nsrc> srcCursor = srcPcx.newCursor(srcNode);
		final FragmentBuilder<Ntrgt> trgtBuilder = trgtPcx.newFragmentBuilder();
        srcCursor.write(trgtBuilder);
        return trgtBuilder.getNode();
	}
}
