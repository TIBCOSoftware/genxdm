package org.genxdm.bridgekit.filters;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;

import org.genxdm.io.DtdAttributeKind;
import org.genxdm.NodeKind;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.SchemaComponentCache;

// initialize this thing and it spits out each created thing as it's encountered
public class SequenceTracingFilter<A> implements SequenceFilter<A>
{
	public SequenceTracingFilter(final SequenceHandler<A> handler, final PrintStream ps, final AtomBridge bridge)
	{
	    // handler and bridge should either be set here or in the mutator methods.
	    // ps must be set here.
		m_next = handler;
		m_ps = (ps == null) ? System.err : ps;
		m_atomBridge = bridge;
	}

    @Override
	public void attribute(final String namespaceURI, final String localName, String prefix, final List<? extends A> data, final QName type) 
	    throws GenXDMException
	{
		dump(NodeKind.ATTRIBUTE, namespaceURI, localName, type);
		dump(data);

		if (m_next != null)
			m_next.attribute(namespaceURI, localName, prefix, data, type);
	}

    @Override
	public void attribute(final String namespaceURI, final String localName, String prefix, final String untypedAtomic, DtdAttributeKind kind) 
	    throws GenXDMException
	{
		dump(NodeKind.ATTRIBUTE, namespaceURI, localName, null);
		dumpUV(untypedAtomic);

		if (m_next != null)
			m_next.attribute(namespaceURI, localName, prefix, untypedAtomic, kind);
	}

    @Override
	public void close() throws IOException
	{
		m_next.close();
	}

    @Override
	public void comment(final String value) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("comment(value=\"" + value + "\")");

		if (m_next != null)
			m_next.comment(value);
	}

    @Override
	public void endDocument() 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("endDocument:");
		m_ps.println("------------");

		if (m_next != null)
			m_next.endDocument();
	}

    @Override
	public void endElement() 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("endElement:");
		m_ps.println("-----------");

		// pop() is not available until Java 1.6
		final QName name = m_names.removeFirst();

		dump(NodeKind.ELEMENT, name.getNamespaceURI(), name.getLocalPart(), m_types.removeFirst());

		if (m_next != null)
			m_next.endElement();
	}

    @Override
	public void flush() throws IOException
	{
		m_next.flush();
	}

    @Override
	public void namespace(final String prefix, final String namespaceURI) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("namespace:");
		m_ps.println("--------------");
		m_ps.println("prefix      =" + formatValue(prefix));
		m_ps.println("namespaceURI=" + formatValue(namespaceURI.toString()));

		if (m_next != null)
			m_next.namespace(prefix, namespaceURI);
	}

    @Override
	public void processingInstruction(final String target, final String data) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("processingInstruction(target=\"" + target + "\", data=\"" + data + "\")");

		if (m_next != null)
			m_next.processingInstruction(target, data);
	}

    @Override
    public void setOutputSequenceHandler(SequenceHandler<A> output)
    {
        if (output != null)
            m_next = output;
    }
    
    @Override
    public void setAtomBridge(AtomBridge<A> bridge)
    {
        if (bridge != null)
            m_atomBridge = bridge;
    }
    
    @Override
    public void setSchema(SchemaComponentCache schema)
    {
        // not used?
    }

    @Override
	public void startDocument(final URI documentURI, final String doctypeDecl) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("startDocument:");
		m_ps.println("--------------");

		if (m_next != null)
			m_next.startDocument(documentURI, doctypeDecl);
	}
	
	@Override
	public void startElement(final String namespaceURI, final String localName, final String prefix)
	    throws GenXDMException
	{
	    startElement(namespaceURI, localName, prefix, null);
    }

    @Override
	public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("startElement:");
		m_ps.println("-------------");

		m_names.addFirst(new QName(namespaceURI, localName, prefix));
	    m_types.addFirst(type); // may be null

		dump(NodeKind.ELEMENT, namespaceURI, localName, type);

		if (m_next != null)
		{
			try
			{
				m_next.startElement(namespaceURI, localName, prefix, type);
			}
			catch (final RuntimeException e)
			{
				e.printStackTrace();

				throw e;
			}
		}
	}

    @Override
	public void text(final List<? extends A> value) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("text(Iterable<A> data):");
		m_ps.println("-----------------------");

		dump(NodeKind.TEXT, null, null, null);
		dump(value);

		if (m_next != null)
		{
			try
			{
				m_next.text(value);
			}
			catch (RuntimeException e)
			{
				e.printStackTrace();

				throw e;
			}
		}
	}

    @Override
	public void text(final String untypedAtomic) 
	    throws GenXDMException
	{
		m_ps.println("");
		m_ps.println("text(String value):");
		m_ps.println("-----------------------------");
		m_ps.println("text=" + untypedAtomic);

		if (m_next != null)
			m_next.text(untypedAtomic);
	}

	protected void dump(final A data)
	{
		if (data != null)
			m_ps.println("    dm:typed-value: " + m_atomBridge.getC14NForm(data));
	}

	private void dump(final List<? extends A> data)
	{
		if (data != null)
			m_ps.println("    dm:typed-value: " + m_atomBridge.getC14NString(data));
	}

	private void dump(final NodeKind kind, final String namespaceURI, final String localName, final QName type)
	{
		m_ps.println("");
		m_ps.println("    dm:node-kind  : " + kind);

		if (localName != null)
		{
			m_ps.println("    dm:namespace  : " + namespaceURI);
			m_ps.println("    dm:local-name : " + localName);
		}
		else
			m_ps.println("    dm:node-name  : ()");

		if (type != null)
			m_ps.println("    dm:type       : {" + type.getNamespaceURI() + "}" + type.getLocalPart());
	}

	private void dumpUV(final String data)
	{
		if (data != null)
			m_ps.println("    dm:string-value: " + data);
	}

	private static String formatValue(final String value)
	{
		if (value == null)
			return null;
        return '\'' + value + '\'';
	}

	protected AtomBridge<A> m_atomBridge;
	protected SequenceHandler<A> m_next;
	protected final PrintStream m_ps;

	private final LinkedList<QName> m_names = new LinkedList<QName>();
	private final LinkedList<QName> m_types = new LinkedList<QName>();

}
