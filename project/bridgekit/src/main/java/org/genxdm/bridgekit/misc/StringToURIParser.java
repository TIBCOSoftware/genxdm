package org.genxdm.bridgekit.misc;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToURIParser
{
    private StringToURIParser() {} // can't be instantiated
    
    // will throw illegalargumentexception if it can't get something useful.
    public static URI parse(final String input)
    {
        if (input == null)
            throw new IllegalArgumentException("'null' is not a valid URI");
        String scheme = null;
        String authority = null;
        String path = null;
        String query = null;
        String fragment = null;
        try
        {
            // algorithm defined in rfc 3986. pretty slick, too ....
            if (URI_PATTERN == null)
                URI_PATTERN = Pattern.compile(URI_REGEX);
            if (input.length() == 0)
                return new URI(null, input, null); // a really stupid (but legal) relative URI.
            Matcher matcher = URI_PATTERN.matcher(input);
            matcher.matches();
            // remove all of the encoding in the components, because otherwise the URI
            // constructor will replace each '%' with %25, which is beyond stupid.
            // here, we also treat '+' as an encoding in the query and fragment parts.
            // there's someone out there on teh interwebz who says that a %2f in a path
            // (an encoded '/' in a path component) ought to be left encoded, but ...
            // that won't work, so far as I can tell (URI() will turn it into %252f,
            // but won't turn '/' into %2f). So ... this is as right as I can get it, for now
            scheme = matcher.group(2);
            authority = decode(matcher.group(4), false);
            path = decode(matcher.group(5), false);
            query = decode(matcher.group(7), true);
            fragment = decode(matcher.group(9), true);
            // if scheme is non-null, then path *must* start with a slash for hierarchical
            if ( (scheme != null) && (path != null) && !path.startsWith(SLASH) ) // not hierarchical
            {
                // if authority is non-null and non-empty, this is supposed to be
                // hierarchical, but is broken beyond retrieval. we don't know how
                // to put together the pieces here for a schema-specific part.
                if ( (authority != null) && (authority.length() > 0) )
                    throw new IllegalArgumentException("Invalid hierarchical URI: "+input);
                // otherwise (no need for else because that throw implies it), we treat
                // path + query as the schema-specific part. non-null query is weird, here, but
                // wtf, eh?
                String schemeSpecific = (query == null) ? path : path + QUERY + query;
                return new URI(scheme, schemeSpecific, fragment);
            }
            // again, else is implied because it returns or throws. we either
            // have no scheme (which must be relative, which must be hierarchical),
            // or we have scheme and authority and a path starting with a slash.
            // or we have scheme and authority and a null path, which requires that
            // we replace the null path with an empty path, because the constructor
            // is too stupid to handle even so bloody simple a bloody case.
            // query and fragment we care less about, so long as they don't
            // cause the uselessly fragile URI constructors to puke on our
            // shoes.
            return new URI(scheme, authority, (path == null) ? "" : path, query, fragment);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Malformed input: "+input, e);
        }
    }
    
    private static String decode(final String component, boolean decodePlus)
    {
        // assume utf-8, and if someone doesn't like it, tell them
        // to pretty much go perform anatomically unlikely acts upon themselves.
        if (component == null)
            return component;
        if (component.indexOf(PERCENT) == -1)
            return component;
        if (component.length() == 0)
            return null;
        
        StringBuilder builder = new StringBuilder();
        int bytePattern, sumOfBytes = 0;
        char actualChar;
        
        for (int i = 0, moreBytes = -1; i < component.length(); i++)
        {
            actualChar = component.charAt(i);

            if (actualChar == PERCENT)
            {
                actualChar = component.charAt(++i);
                int high = (Character.isDigit(actualChar) ? actualChar - ZERO : 10 + Character.toLowerCase(actualChar) - A) & 0xF;
                actualChar = component.charAt(++i);
                int low = (Character.isDigit(actualChar) ? actualChar - ZERO : 10 + Character.toLowerCase(actualChar) - A) & 0xF;
                bytePattern = (high << 4) | low;
            }
            else if ((actualChar == PLUS) && decodePlus)
                bytePattern = SPACE;
            else
                bytePattern = actualChar;

            if ((bytePattern & 0xc0) == 0x80) // 10xxxxxx (last of a multibyte pattern)
            {
                sumOfBytes = (sumOfBytes << 6) | (bytePattern & 0x3f);
                if (--moreBytes == 0)
                    builder.append((char) sumOfBytes);
            } 
            else if ((bytePattern & 0x80) == 0x00) // 0xxxxxxx (ascii subset)
                builder.append((char) bytePattern);
            else if ((bytePattern & 0xe0) == 0xc0) // 110xxxxx (non-last of multibyte pattern)
            {
                sumOfBytes = bytePattern & 0x1f;
                moreBytes = 1;
            } 
            else if ((bytePattern & 0xf0) == 0xe0) // 1110xxxx (non-last of multibyte pattern)
            {
                sumOfBytes = bytePattern & 0x0f;
                moreBytes = 2;
            } 
            else if ((bytePattern & 0xf8) == 0xf0) // 11110xxx (non-last of multibyte pattern)
            {
                sumOfBytes = bytePattern & 0x07;
                moreBytes = 3;
            } 
            else if ((bytePattern & 0xfc) == 0xf8) // 111110xx (non-last of multibyte pattern)
            {
                sumOfBytes = bytePattern & 0x03;
                moreBytes = 4;
            } 
            else // 1111110x (first of a multibyte pattern (the longest pattern))
            {
                sumOfBytes = bytePattern & 0x01;
                moreBytes = 5;
            }
        }
        return builder.toString();
    }
    
    private static Pattern URI_PATTERN = null;
    
    private static final String URI_REGEX = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";
//  match group start positions:              12            3  4          5       6   7        8 9
    // constants for use in replacement (probably safe to use literals, but clearer-perhaps to use constants)
    private static final String SLASH = "/";
    private static final char QUERY = '?';
    private static final char PERCENT = '%';
    private static final char PLUS = '+';
    private static final char SPACE = ' ';
    private static final char ZERO = '0';
    private static final char A = 'a';
}
