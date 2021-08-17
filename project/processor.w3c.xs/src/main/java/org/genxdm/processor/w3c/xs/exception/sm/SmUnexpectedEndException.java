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

import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class SmUnexpectedEndException extends SmComplexTypeException
{
	private List<QName> expectedElementsList;

	public SmUnexpectedEndException(final QName elementName, final LocationInSchema location)
    {
        super(PART_CONTENT_TYPE_AND_CHILD_SEQUENCE, elementName, location);
    }

    public SmUnexpectedEndException(final QName elementName, final LocationInSchema location, List<QName> expectedElementsList)
    {
        super(PART_CONTENT_TYPE_AND_CHILD_SEQUENCE, elementName, location);
        this.expectedElementsList = expectedElementsList;
    }

    @Override
    public String getMessage()
    {
        final String localMessage = "Unexpected end of content in element '" + getElementName() + "'.";

        final StringBuilder message = new StringBuilder();
        message.append(getOutcome().getSection());
        message.append(".");
        message.append(getPartNumber());
        message.append(": ");
        message.append(localMessage);
        message.append(getExpectedElementMessage());
        return message.toString();
    }

    private String getExpectedElementMessage()
    {
    	if(this.expectedElementsList == null)
    		return "";
    	int expectedListSize;
    	String expected = "";
    	if(this.expectedElementsList != null &&  (expectedListSize = this.expectedElementsList.size()) > 0)
    	{
    		if(expectedListSize > 1)
    		{
    			StringBuilder sb = new StringBuilder();
    			sb.append(" One of ");
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
    			sb.append("'");
    			sb.append(" is expected");
    			expected = sb.toString();
    		}
    		else
    			expected = "'"+this.expectedElementsList.get(0)+"' is expected.";
    	}
    	return expected;
    }
}
