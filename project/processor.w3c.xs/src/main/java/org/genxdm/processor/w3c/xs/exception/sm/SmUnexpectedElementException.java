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
package org.genxdm.processor.w3c.xs.exception.sm;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class SmUnexpectedElementException extends SmComplexTypeException
{
    private final QName m_childName;
    private List<QName> expectedElementsList;

    public SmUnexpectedElementException(final QName elementName, final LocationInSchema location, final QName childName, final LocationInSchema childLocation)
    {
        super(PART_CONTENT_TYPE_AND_CHILD_SEQUENCE, elementName, location);
        m_childName = PreCondition.assertArgumentNotNull(childName, "childName");
    }

    public SmUnexpectedElementException(final QName elementName, final LocationInSchema location, final QName childName, final LocationInSchema childLocation, List<QName> expectedElementsList)
    {
    	this(elementName, location, childName, childLocation);
        this.expectedElementsList = expectedElementsList;
    }

    public QName getChildName()
    {
        return m_childName;
    }

    @Override
    public String getMessage()
    {
    	String errMessage = "Unexpected element sequence, got '" + m_childName + "' in " + getElementName() + ". ";
    	return errMessage += getExpectedElementMessage();
    }

    private String getExpectedElementMessage()
    {
    	if(this.expectedElementsList == null)
    		return "";
    	int expectedListSize;
    	String expected = "";
    	if((expectedListSize = this.expectedElementsList.size()) > 0)
    	{
    		if(expectedListSize > 1)
    		{
    			StringBuilder sb = new StringBuilder();
    			sb.append("One of ");
    			int ctr = expectedListSize-1;
    			while(ctr > 0)
    			{
    				QName current = this.expectedElementsList.get(ctr);
    				sb.append("'");
    				sb.append(current);
    				sb.append("', ");
    				ctr--;
    			}
    			sb.append("'");
    			sb.append(this.expectedElementsList.get(0));
    			sb.append("' is expected");
    			expected = sb.toString();
    		}
    		else
    			expected = "'"+this.expectedElementsList.get(0)+"' is expected.";
    	}
    	/** AMBW-41937 : expectedElementsList will not be null while
    	 * throwing out exception from validator. However, Schema parser
    	 * uses this exception too. However, we are not changing its
    	 * behavior for this fix. Need to continue to work as before.
    	 * Hence this following check.
    	 */
    	else
    		expected = "No child element is expected at this point.";
    	return expected;
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof SmUnexpectedElementException)
        {
            final SmUnexpectedElementException other = (SmUnexpectedElementException)obj;
            return getElementName().equals(other.getElementName());
        }
        else
        {
            return false;
        }
    }
}
