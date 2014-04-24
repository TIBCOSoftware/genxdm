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

@SuppressWarnings("serial")
public final class SccAllGroupAppearsException extends SccAllGroupLimitedException
{
    public SccAllGroupAppearsException()
    {
        super(PART_APPEARS);
    }

    @Override
    public String getMessage()
    {
        return "A model group with {compositor} all may only appear as the {model group} property of a model group definition or as the {term} property of a particle with {max occurs}=1 which is part of a pair which constitutes the {content type} of a complex type definition.";
    }
}
