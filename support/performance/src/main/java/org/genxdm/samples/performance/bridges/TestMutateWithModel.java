package org.genxdm.samples.performance.bridges;

import java.util.ArrayList;
import java.util.Properties;

import org.genxdm.Feature;
import org.genxdm.mutable.MutableContext;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

public class TestMutateWithModel <N,A> extends BaseBridgePerfTest<N,A>
implements MutationPerfTestConstants
{
	private MutableContext<N> m_mutablePcx;
	private MutableModel<N> m_mutableModel;
	private NodeFactory<N> m_nodeFactory;
	private static final ArrayList<String> REQUIRED_FEATURES = new ArrayList<String>();
	static
	{
		REQUIRED_FEATURES.add(Feature.MUTABILITY);
	}
	
    @Override
    public Iterable<String> getRequiredFeatures()
    {
    	return REQUIRED_FEATURES;
    }
	@Override
	public String getName() {
		return "Mutate via model";
	}
	@Override
	public void initialSetup(Properties props)	{
		m_mutablePcx = getPcx().getMutableContext();
		m_mutableModel = m_mutablePcx.getModel();
		m_nodeFactory = m_mutablePcx.getNodeFactory();
	}
	
	@Override
	public void iterativeSetup() {
	}
	@Override
	public void execute() {
		/* // Create a new document. */
		final N documentNode = m_nodeFactory.createDocument(null, null);

		final N documentElement = m_nodeFactory.createElement(MUTATE_NS, MUTATE_ROOT_NAME, MUTATE_PREFIX);

		// Append the document element to the documentNode.
		m_mutableModel.appendChild(documentNode, documentElement);

		appendChildren(documentElement, MUTATE_NS, MUTATE_PREFIX, MUTATE_CHILD_NAMES, MUTATE_ATTS, MUTATE_TEXT_VALUES, MUTATATION_LEVELS);
	}
	private void appendChildren(N parent, String ns, String prefix, String[] childNames, 
			String[][] atts, String textValues[], int levels)
	{
		// Add children
		for(String childName : childNames)
		{
			final N childElement = m_nodeFactory.createElement(ns, childName, prefix);
			m_mutableModel.insertNamespace(childElement, MUTATE_PREFIX, ns);
			
			// Add attributes
			for(String[] att : atts)
			{
				N attribute = m_nodeFactory.createAttribute(ns, att[0], prefix, att[1]);
				m_mutableModel.insertAttribute(childElement, attribute);
			}
			// If not leaf node, add children
			if(levels > 0)
			{
				appendChildren(childElement, ns, prefix, childNames, atts, textValues, levels - 1);
			}
			// Else if leaf node, add text values
			else
			{
				for(String textValue : textValues)
				{
					m_mutableModel.appendChild(childElement, m_nodeFactory.createText(textValue));
				}
			}
		}
	}
	@Override
	public Iterable<String> iterativeTeardown() { return null; }
	
	@Override
	public void finalTeardown() {
		super.finalTeardown();
		m_mutableModel = null;
		m_mutablePcx = null;
		m_nodeFactory = null;
	}
	
}
