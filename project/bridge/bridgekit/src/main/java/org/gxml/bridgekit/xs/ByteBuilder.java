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
package org.gxml.bridgekit.xs;

import java.io.UnsupportedEncodingException;

/**
 * Variable size m_buffer of bytes.
 * <p/>
 * StringBuilder for ASCII, as it were. In fact, it can store whole bytes, whereas US-ASCII is a seven-bit encoding, but
 * so it goes.
 * 
 * @author Amelia A. Lewis &lt;alewis@apache.com>
 * @version $id$
 */

final class ByteBuilder
{
	private int m_count;
	private byte[] m_buffer;

	/**
	 * Standard constructor, allocates sixteen byte m_buffer.
	 */
	public ByteBuilder()
	{
		this(16);
	}

	/**
	 * Common constructor, allocates byte m_buffer of the specified size.
	 */
	public ByteBuilder(final int length)
	{
		m_buffer = new byte[length];
	}

	/**
	 * Uncommon constructor, sets m_buffer to be the supplied byte array.
	 */
	public ByteBuilder(final byte[] content)
	{
		if (content != null)
		{
			m_buffer = content;
			m_count = content.length;
		}
		else
		{
			m_count = 0;
		}
	}

	/**
	 * @return the number of valid bytes in the m_buffer.
	 */
	public int length()
	{
		return m_count;
	}

	/**
	 * @return the size of the internal m_buffer (in bytes).
	 */
	public int capacity()
	{
		return m_buffer.length;
	}

	/**
	 * Ensure that there's enough space in the m_buffer, as specified in the parameter. Note that this will not reduce
	 * m_buffer size.
	 * 
	 * @param minimumCapacity
	 *            the least size to which the m_buffer should expand.
	 */
	public void ensureCapacity(final int minimumCapacity)
	{
		if (minimumCapacity > m_buffer.length)
		{
			expandCapacity(minimumCapacity);
		}
	}

	/**
	 * Clear the m_buffer. Note that this does not reduce the m_buffer size. Also note that there is no setLength().
	 */
	public void clear()
	{
		m_count = 0;
	}

	/**
	 * Retrieve a byte at offset index, if it is in range for valid bytes.
	 * 
	 * @param index
	 *            the zero-based index of the byte to retrieve.
	 * @return the byte at that index
	 * @throws ArrayIndexOutOfBoundsException
	 *             if index is less than zero or greater than or equal to the number of valid bytes in the m_buffer.
	 */
	public byte get(final int index)
	{
		if ((index < 0) || (index >= m_count))
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return m_buffer[index];
	}

	/**
	 * Add a byte to the end of the m_buffer.
	 * 
	 * @param b
	 *            the byte to append
	 * @return this
	 */
	public ByteBuilder append(final byte b)
	{
		if ((m_count + 1) > m_buffer.length)
		{
			ensureCapacity(m_buffer.length * 2);
		}
		m_buffer[m_count] = b;
		m_count++;
		return this;
	}

	/**
	 * Add a byte to the end of the m_buffer. This is roughly the same as append((byte)(i & 0xFF)), except that it
	 * relies upon the VM behaving that way.
	 * 
	 * @param i
	 *            an integer which will be truncated into a byte that will be appended.
	 * @return this
	 */
	public ByteBuilder append(final int i)
	{
		return append((byte)i);
	}

	/**
	 * Extract the final portion of the m_buffer, beginning at a specified index.
	 * 
	 * @param i
	 *            the offset at which to start copying.
	 * @return a ByteBuffer in which the m_buffer contains only the bytes from the specified index to the end of the
	 *         valid range; an empty byte m_buffer if the range is empty.
	 */
	public ByteBuilder subBuffer(final int i)
	{
		if ((i < 0) || (i >= m_count))
		{
			return new ByteBuilder();
		}
		byte[] buf = new byte[m_count - i];
		System.arraycopy(m_buffer, i, buf, 0, m_count - i);
		return new ByteBuilder(buf);
	}

	/**
	 * @return the US-ASCII encoding of the bytes, as a String.
	 */
	public String toString()
	{
		try
		{
			return new String(getBytes(), "US-ASCII");
		}
		catch (final UnsupportedEncodingException uee)
		{
			// this is a total and complete never-happen; US-ASCII is as supported as it gets.
			return "";
		}
	}

	/**
	 * Retrieve the contents of the m_buffer as a String, in a specified encoding.
	 * 
	 * @param encoding
	 *            the encoding to use
	 * @return a String resulting from interpreting the bytes in the given encoding.
	 * @throws java.io.UnsupportedEncodingException
	 *             if the encoding is not recognized.
	 */
	public String toString(final String encoding) throws UnsupportedEncodingException
	{
		return new String(getBytes(), encoding);
	}

	/**
	 * Retrieve a copy of the internal byte array m_buffer, truncated to contain only valid values.
	 * 
	 * @return a copy of the m_buffer, or a zero-length array.
	 */
	public byte[] getBytes()
	{
		byte[] buf = new byte[m_count];
		System.arraycopy(m_buffer, 0, buf, 0, m_count);
		return buf;
	}

	/**
	 * Double the size of the m_buffer, in case that's needed. Handle special case of zero-length m_buffer (which
	 * shouldn't ever occur anyway).
	 * 
	 * @param minimumCapacity
	 *            the minimum expansion of the m_buffer to make (it will be doubled if minimum is less than double
	 *            current capacity).
	 */
	protected void expandCapacity(final int minimumCapacity)
	{
		int newCapacity = (m_buffer.length == 0 ? 1 : m_buffer.length) * 2;
		if (newCapacity < 0)
		{
			newCapacity = Integer.MAX_VALUE;
		}
		else if (minimumCapacity > newCapacity)
		{
			newCapacity = minimumCapacity;
		}

		byte newValue[] = new byte[newCapacity];
		System.arraycopy(m_buffer, 0, newValue, 0, m_count);
		m_buffer = newValue;
	}
}
