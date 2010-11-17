package org.genxdm.compat;

import org.genxdm.base.Model;
import org.genxdm.base.io.FragmentBuilder;

public class NodeCopier 
{
	public static <N> N copyNode(N node, Model<N> model, FragmentBuilder<N> handler, boolean deep)
	{
		handler.reset();
		if ( model.getNodeKind(node).isContainer() && deep )
		{
			model.stream(node, true, handler);
		}
		else
		{
			switch (model.getNodeKind(node))
			{
				case DOCUMENT :
				{
					handler.startDocument(model.getDocumentURI(node), null); // there's no way to get the internal subset?
					handler.endDocument();
				}
				case ELEMENT :
				{
					handler.startElement(model.getNamespaceURI(node), model.getLocalName(node), model.getPrefix(node));
					handler.endElement();
				}
				case ATTRIBUTE :
				{
					handler.attribute(model.getNamespaceURI(node), model.getLocalName(node), model.getPrefix(node), model.getStringValue(node), null);
				}
				case NAMESPACE :
				{
					handler.namespace(model.getLocalName(node), model.getStringValue(node));
				}
				case TEXT :
				{
					handler.text(model.getStringValue(node));
				}
				case COMMENT :
				{
					handler.comment(model.getStringValue(node));
				}
				case PROCESSING_INSTRUCTION :
				{
					handler.processingInstruction(model.getLocalName(node), model.getStringValue(node));
				}
				default :
				{
					throw new IllegalStateException("Unknown node kind");
				}
			}
		}
		return handler.getNode();
	}
}
