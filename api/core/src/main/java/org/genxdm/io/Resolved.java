/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.io;

import java.net.URI;

import org.genxdm.exceptions.IllegalNullArgumentException;


/**
 * A triple consisting of a location that gets resolved to a resource, the resource that is resolved and the associated
 * systemId.
 * <p>
 * The systemId may be more specific that the original location that was provided to a resolver and may be used by
 * applications as a base-uri for accessing child resources.
 * </p>
 */
public final class Resolved<E>
{
	private final URI location;
	private final E resource;
	private final URI systemId;

	/**
	 * Initializer.
	 * 
	 * @param location
	 *            The original location specified for the resource to be resolved.
	 * @param resource
	 *            The resource that has been resolved.
	 * @param systemId
	 *            The systemId of the resolved resource.
	 */
	public Resolved(final URI location, final E resource, final URI systemId)
	{
		this.location = IllegalNullArgumentException.check(location, "location");
		this.resource = IllegalNullArgumentException.check(resource, "resource");
		this.systemId = IllegalNullArgumentException.check(systemId, "systemId");
	}

	/**
	 * Returns the original location specified for the resource.
	 */
	public URI getLocation()
	{
		return location;
	}

	/**
	 * Returns the resource that has been resolved.
	 */
	public E getResource()
	{
		return resource;
	}

	/**
	 * Returns the systemId of the resolved resource. This may be used as a base-uri for resolving nested resource
	 * references.
	 */
	public URI getSystemId()
	{
		return systemId;
	}
}
