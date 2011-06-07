/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.w3c.xs.impl.xmlrep;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.enums.ProcessContentsMode;

public final class XMLWildcard implements XMLParticleTerm
{
	public String id;
	private ProcessContentsMode m_processContents;
	private NamespaceConstraint m_namespaceConstraint;

	public XMLWildcard(final ProcessContentsMode processContents, final NamespaceConstraint namespaceConstraint)
	{
		m_processContents = PreCondition.assertArgumentNotNull(processContents);
		m_namespaceConstraint = PreCondition.assertArgumentNotNull(namespaceConstraint);
	}

	public ProcessContentsMode getProcessContents()
	{
		return m_processContents;
	}

	public void setProcessContents(final ProcessContentsMode processContents)
	{
		m_processContents = PreCondition.assertArgumentNotNull(processContents);
	}

	public NamespaceConstraint getNamespaceConstraint()
	{
		return m_namespaceConstraint;
	}
}