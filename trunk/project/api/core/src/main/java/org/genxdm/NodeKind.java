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
package org.genxdm;

/**
 * Enumeration representing possible types of nodes in the XQuery Data Model.
 */
public enum NodeKind
{
    /**
     * The <code>element</code> node kind.
     */
    ELEMENT
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return true;
        }
        
        public boolean isContainer()
        {
            return true;
        }

        public boolean isNamespace()
        {
            return false;
        }
    },

    /**
     * The <code>text</code> node kind.
     */
    TEXT
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return true;
        }
        
        public boolean isContainer()
        {
            return false;
        }

        public boolean isNamespace()
        {
            return false;
        }
    },

    /**
     * The <code>attribute</code> node kind.
     */
    ATTRIBUTE
    {
        public boolean isAttribute()
        {
            return true;
        }

        public boolean isChild()
        {
            return false;
        }
        
        public boolean isContainer()
        {
            return false;
        }

        public boolean isNamespace()
        {
            return false;
        }
    },

    /**
     * The <code>namespace</code> node kind.
     */
    NAMESPACE
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return false;
        }
        
        public boolean isContainer()
        {
            return false;
        }

        public boolean isNamespace()
        {
            return true;
        }
    },

    /**
     * The <code>document</code> node kind.
     */
    DOCUMENT
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return false;
        }
        
        public boolean isContainer()
        {
            return true;
        }

        public boolean isNamespace()
        {
            return false;
        }
    },

    /**
     * The <code>processing-instruction</code> node kind.
     */
    PROCESSING_INSTRUCTION
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return true;
        }
        
        public boolean isContainer()
        {
            return false;
        }

        public boolean isNamespace()
        {
            return false;
        }
    },

    /**
     * The <code>comment</code> node kind.
     */
    COMMENT
    {
        public boolean isAttribute()
        {
            return false;
        }

        public boolean isChild()
        {
            return true;
        }

        public boolean isContainer()
        {
            return false;
        }
        
        public boolean isNamespace()
        {
            return false;
        }
    };

    /**
     * Are nodes of this kind attributes?
     * @return true for attribute nodes, false for all other kinds.
     */
    public abstract boolean isAttribute();

    /**
     * Are nodes of this kind able to be children of container nodes?
     * @return true for comment, element, processing instruction, and text nodes,
     * false for document, attribute, and namespace nodes.
     */
    public abstract boolean isChild();

    /**
     * Are nodes of this kind namespaces?
     * @return true for namespace nodes, false for all other kinds.
     */
    public abstract boolean isNamespace();
    
    /**
     * Are nodes of this kind able to contain child nodes?
     * @return true for document and element nodes, false for all other kinds.
     */
    public abstract boolean isContainer();
    
}
