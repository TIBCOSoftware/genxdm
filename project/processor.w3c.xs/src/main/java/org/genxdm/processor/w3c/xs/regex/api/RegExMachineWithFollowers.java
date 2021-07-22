package org.genxdm.processor.w3c.xs.regex.api;

import java.util.List;

public interface RegExMachineWithFollowers<E, T> extends RegExMachine<E, T>{

	List<E> getFollowers();

}
