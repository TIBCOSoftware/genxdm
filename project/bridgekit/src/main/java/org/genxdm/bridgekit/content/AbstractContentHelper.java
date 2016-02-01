package org.genxdm.bridgekit.content;

import java.util.Map;

import org.genxdm.exceptions.GenXDMException;

public abstract class AbstractContentHelper
    implements ContentHelper
{

    @Override
    public void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        start();
        if (name != null)
            startComplex(ns, name, bindings, attributes);
        // however, if the name is null or empty, *and* ...
        else if ( (ns != null) || (bindings != null) || (attributes != null) )
            throw new GenXDMException("Illegal start-document invocation: unnamed element has namespace(s) and/or attributes");
        // but null or empty name with everything else null is just ignorable, right?
    }

    @Override
    public void simpleElement(String ns, String name, String value)
    {
        simplexElement(ns, name, null, null, value);
    }

    @Override
    public void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value)
    {
        startComplex(ns, name, bindings, attributes);
        text(ns, name, value);
        endComplex();
    }

    @Override
    public Attrib newAttribute(String name, String value)
    {
        return new Attrib(name, value);
    }

    @Override
    public Attrib newAttribute(String ns, String name, String value)
    {
        return new Attrib(ns, name, value);
    }

    protected abstract void text(String ns, String name, String value);
}
