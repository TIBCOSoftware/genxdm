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
package org.genxdm.processor.w3c.xs.exception.scc;

import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;

@SuppressWarnings("serial")
public class SccMaxExclusionRestrictionException extends SccLimitRestrictionException
{
    public SccMaxExclusionRestrictionException(final FacetKind parentFacetKind, final Limit restrictingLimit, final Limit parentLimit)
    {
        super(ValidationOutcome.SCC_MaxExclusiveValidRestriction, parentFacetKind == FacetKind.MaxExclusive ? "1" : parentFacetKind == FacetKind.MaxInclusive ? "2" : parentFacetKind == FacetKind.MinInclusive ? "3" : "4", FacetKind.MaxExclusive, parentFacetKind, restrictingLimit, parentLimit);
    }
}
