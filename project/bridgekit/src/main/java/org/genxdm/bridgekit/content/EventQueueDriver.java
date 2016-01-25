package org.genxdm.bridgekit.content;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.io.ContentHandler;
import org.genxdm.typed.io.SequenceHandler;

public class EventQueueDriver
{
    private EventQueueDriver() {}
    
    public static void driveQueue(Iterable<ContentEvent> queue, ContentHandler untypedOutput)
    {
    }
    
    public static <A> void driveQueue(Iterable<TypedContentEvent<A>> queue, SequenceHandler<A> typedOutput)
    {
    }
    
    public static List<ContentEvent> newUntypedQueue()
    {
        return new ArrayList<ContentEvent>();
    }
    
    public static <A> List<TypedContentEvent<A>> newTypedQueue()
    {
        return new ArrayList<TypedContentEvent<A>>();
    }
}
