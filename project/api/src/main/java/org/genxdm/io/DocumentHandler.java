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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.XMLConstants;

import org.genxdm.exceptions.XdmMarshalException;
import org.xml.sax.InputSource;

/** Provides an interface for reading and writing XML.
 * 
 * The DocumentHandler interface provides a means to supply XML (as
 * readers or input streams) to be built into a target
 * tree model; it also permits tree models to "serialize" onto parallel output
 * abstractions (writers and output streams).
 * 
 * These are conveniences; it is not difficult to hook a parser to a
 * fragment builder or a writer to a content handler to model.stream() if
 * alternative behavior is desired.
 * 
 * DocumentHandler is <em>not</em> thread-safe, either for parsing or for
 * writing. Internal state is not protected by synchronization, and will be
 * affected by calls from another thread that begin before calls from the
 * original thread end. Do not pass the DocumentHandler to different threads
 * unless the threads themselves enforce synchronization.
 *
 * @param <N> the Node handle.
 */

public interface DocumentHandler<N>
    extends DocumentParser<N>, DocumentWriter<N>
{
    /**
     * A method that corresponds to the {@link XMLConstants#FEATURE_SECURE_PROCESSING} flag that can be
     * passed to various XML processors.
     * 
     * <p>Note that what "secure" means is parser implementation dependent.</p>
     * 
     * @return <code>true</code> if the processing will be done securely.
     */
    boolean isSecurelyProcessing();
}
