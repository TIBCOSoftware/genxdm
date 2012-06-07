package org.genxdm.processor.w3c.xs.validationtest.xsts;

import org.genxdm.processor.w3c.xs.validationtest.ValidatorTestBase;

public abstract class XSTSBase<N, A>
    extends ValidatorTestBase<N, A>
{

    // this is a placeholder, for the moment.
    // we have the xml schema test suite, and are working on how to
    // make it available (without overwhelming the codebase; it's ten
    // times the size), and how to chunk it up and make it usable by
    // bridge developers.
    
    // test sets define test groups, which generally include one 'schema test'
    // and a number of instance tests.  we may want to enable tests by group
    // or even by set; we do want to permit bridge developers to disable
    // instances within a group or a set, if they cannot support something.
    // ideally, we'd have a way of noting that, too.
}
