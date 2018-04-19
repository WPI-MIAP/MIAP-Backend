
package org.maras.testing;

/**
 * 
 * 
 * Defines an interface to facilitate the execution of tests and output of test results.
 * 
 * @author Brian Zylich
 */
public interface Tester {

    /**
     * Perform the test.
     */
    public void runTest();

    /**
     * Prints the detailed results of the test.
     */
    public void printAnalysis();

    /**
     * Report whether a test was successful or not.
     */
     public boolean getSuccess();
}