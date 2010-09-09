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
package org.gxml.exceptions;

import java.io.IOException;


/**
 * A wrapper class over an {@link IOException} that allows an IOEXception to tunnel through APIs with unchecked exceptions.
 */
@SuppressWarnings("serial")
public final class GxmlIOException extends RuntimeException
{
	public GxmlIOException(final IOException cause)
	{
		super(PreCondition.assertArgumentNotNull(cause, "cause"));
	}

	@Override
	public IOException getCause()
	{
		return (IOException)super.getCause();
	}
}
