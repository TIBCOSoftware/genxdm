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
package org.genxdm.xs.types;

/**
 * The content model kind defines the combinations of elements and text nodes that may be included in the child axis of a type.
 */
public enum ContentTypeKind
{
    /**
     * Indicates that a complex type has a complex type {content type} property; only elements are accepted.
     */
    ElementOnly
    {
        public boolean isComplex()
        {
            return true;
        }

        public boolean isElementOnly()
        {
            return true;
        }
    },

    /**
     * No child elements or text nodes are allowed.
     */
    Empty
    {
        public boolean isComplex()
        {
            return false;
        }

        public boolean isElementOnly()
        {
            return false;
        }
    },

    /**
     * Indicates that a complex type has a complex type {content type} property and mixed is enabled; both text nodes and element nodes are accepted.
     */
    Mixed
    {
        public boolean isComplex()
        {
            return true;
        }

        public boolean isElementOnly()
        {
            return false;
        }
    },

    /**
     * Indicates that a complex type has a simple type {content type} property.
     */
    Simple
    {
        public boolean isComplex()
        {
            return false;
        }

        public boolean isElementOnly()
        {
            return false;
        }
    };

    public abstract boolean isComplex();

    public abstract boolean isElementOnly();

    public boolean isEmpty()
    {
        return this == Empty;
    }

    public boolean isMixed()
    {
        return this == Mixed;
    }

    public boolean isSimple()
    {
        return this == Simple;
    }
}
