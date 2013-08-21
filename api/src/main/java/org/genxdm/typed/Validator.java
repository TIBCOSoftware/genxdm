/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.typed;

import javax.xml.namespace.QName;

import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;

/** Validator provides an interface for preparing a validation processor.
 * 
 * @param <A> the atom abstraction.
 */
public interface Validator<A>
{
    /** Generally called by the user after validation, to check the errors. 
     * 
     * @return the SchemaExceptionHandler which is used to catch validation errors.
     * May return null if no handler has been set.
     */
    SchemaExceptionHandler getSchemaExceptionHandler();
    
    /** Resets any internal state in the validator.  Also passes the reset
     * to the SequenceHandler, if it happens to be a SequenceBuilder,
     * and may clear the contents of the SchemaExceptionHandler as
     * well.
     */
    void reset();
    
    /** Allows a caller to provide a list of elements, by QName, which are to
     * be treated as if their type definitions were annotated, in the schema,
     * with processContents='skip'. This is primarily useful for handling, e.g.
     * attachments in the form of XOP, where base64Binary text nodes are replaced
     * by child elements.
     * 
     * @param elementNames the QNames of the elements to be ignored.
     */
    void setIgnores(Iterable<QName> elementNames);
    
    // TODO: should this be componentprovider instead of schema?
    /** Provide the schema component provider which is used during validation.
     * In general, users should <em>not</em> call this method, though they
     * may; the TypedContext on which the appropriate validate or parsed-validate
     * method is called will call the method, passing its own schema cache.
     * 
     * @param cache the cache of schema components, accessible by name.
     */
    void setSchema(SchemaComponentCache cache);
    
    /** Provide the schema exception handler.  As a rule, the user
     * supplies this handler before passing the validator to a validation
     * API.
     * 
     * @param errors the schema exception handler.  May not be null.
     */
    void setSchemaExceptionHandler(SchemaExceptionHandler errors);
    
    /** Provide the SequenceHandler which the Validator will invoke
     * as it validates the document.  In general, users should <em>not</em>
     * call this method, though they may with no ill effects (except that
     * the nominated SequenceHandler will be ignored); the TypedContext
     * on which the appropriate validate or parsed-validate method is called
     * will invoke this method, passing an appropriate SequenceHandler.
     * 
     * @param handler the SequenceHandler that adapts the validator to
     * a particular bridge; may not be null.
     */
    void setSequenceHandler(SequenceHandler<A> handler);

}
