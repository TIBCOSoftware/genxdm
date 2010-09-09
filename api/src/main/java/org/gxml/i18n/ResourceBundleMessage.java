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
package org.gxml.i18n;

import java.text.Format;
import java.util.ResourceBundle;

/**
 * This {@link ResourceBundleMessage} interface guarantees that a message can be localized
 * using a locale. This interface provides no more and no less than what is required for
 * performing standard internationalization as described in the official Java Tutorials.
 * This interface also performs no translation of the arguments. It merely collects together
 * what is required for composite messages (the most general case).
 */
public interface ResourceBundleMessage
{
    /**
     * Returns the key that will be used to uniquely identify the message.
     * <br/>
     * This will be used as the key into a resource bundle.
     *
     * @return The resource bundle pattern key.
     */
    String getPatternKey();

    /**
     * Returns an argument array that can be passed to {@link java.text.MessageFormat#format}.
     *
     * @param messages The resource bundle name.
     * @return Argument parameters.
     */
    Object[] getArguments(ResourceBundle messages);

    /**
     * Returns an {@link Format} array that can be passed to {@link java.text.MessageFormat#setFormats}.
     *
     * @param messages The resource bundle name.
     * @return Format array used by MessageFormat.setFormats(). May be <code>null</code>.
     */
    Format[] getFormat(ResourceBundle messages);
}
