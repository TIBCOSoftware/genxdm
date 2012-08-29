package org.genxdm.processor.w3c.xs.validationtest;

import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;

/** TestBase provides a set of factories for the things that are needed in
 * order to test validation, plus utility methods.
 *
 * By implementing ProcessingContextFactory, concrete subclasses will tie
 * to a bridge implementation.  By providing methods to obtain the two types
 * of validator, the test suite permits processors other than the original
 * proc-w3cxs-val to be tested.
 * 
 * Enabling a particular set of tests (encapsulated by an abstract class)
 * is thus a matter of instantiating a subclass and implementing the three
 * methods required.  If new validators are introduced, bridges can test
 * the conformance of the new processor(s) by adding another set of subclasses,
 * returning the new validators.
 *
 * @param <N> the node abstraction
 * @param <A> the atom abstraction
 */
public abstract class ValidatorTestBase<N, A>
    implements ProcessingContextFactory<N>
{
    
    public abstract SAXValidator<A> getSAXValidator();
    
    public abstract ValidationHandler<A> getValidationHandler();
}
