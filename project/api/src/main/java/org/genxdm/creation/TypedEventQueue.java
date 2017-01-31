package org.genxdm.creation;

import java.util.List;

public interface TypedEventQueue<A>
{
    List<TypedContentEvent<A>> getQueue();
}
