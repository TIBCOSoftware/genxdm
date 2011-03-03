package org.genxdm.samples.performance.bridges;

import java.net.URI;
import java.net.URISyntaxException;

import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;

public class TestMutateWithBuilder <N,A> extends BaseBridgePerfTest<N,A> implements MutationPerfTestConstants
{
	private final String DOC_TYPE_DECL = null;
	private final DtdAttributeKind DTD_ATT_KIND = null;
    FragmentBuilder<N> m_fragBuilder;
	
	@Override
	public String getName() {
		return "Mutate via builder";
	}
	
	@Override
	public void iterativeSetup() {
		m_fragBuilder = getPcx().newFragmentBuilder();
	}
	@Override
	public void execute() {
		/* // Create a new document. */
		try {
			m_fragBuilder.startDocument(new URI(MUTATE_NS), DOC_TYPE_DECL);
			{
				m_fragBuilder.startElement(MUTATE_NS, MUTATE_ROOT_NAME, MUTATE_PREFIX);
				{
					appendChildren(MUTATE_NS, MUTATE_PREFIX, MUTATE_CHILD_NAMES, MUTATE_ATTS, MUTATE_TEXT_VALUES, MUTATATION_LEVELS);
				}
				m_fragBuilder.endElement();
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		m_fragBuilder.endDocument();
	}
	private void appendChildren(String ns, String prefix, String[] childNames, String[][] atts, String textValues[], int levels)
	{
		// Add children
		for(String childName : childNames)
		{
			m_fragBuilder.startElement(ns, childName, prefix);
			{
				// Add attributes
				for(String[] att : atts)
				{
					m_fragBuilder.attribute(ns, att[0], prefix, att[1], DTD_ATT_KIND);
				}
				// If not leaf node, add children
				if(levels > 0)
				{
					appendChildren(ns, prefix, childNames, atts, textValues, levels - 1);
				}
				// Else if leaf node, add text values
				else
				{
					for(String textValue : textValues)
					{
						m_fragBuilder.text(textValue);
					}
				}
			}
			m_fragBuilder.endElement();
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
}
