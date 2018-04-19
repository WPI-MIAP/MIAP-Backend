/**
 * 
 */
package org.maras.testing;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.maras.framework.Item;
import org.maras.framework.Rule;
import org.maras.framework.RuleSets;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

/**
 * 
 * 
 * Analyzes two sets of rules to determine whether they contain the same content.
 * 
 * @author Brian Zylich
 */
public class RuleTester implements Tester {

    private String testPath;
    private String correctPath;

    private Map<Rule, Boolean> testRules;
    private List<Rule> correctRules;

    private List<Rule> missingRules;
    private List<Rule> extraRules;
    private int numCorrect;
    private int numTestRules;
    private int numCorrectRules;
    private int numExtraRules;

    private boolean success;

    public RuleTester(RulesFile testFile, RulesFile correctFile) {
        testRules = testFile.getRulesAsMap();
        testPath = testFile.getPath();
        correctRules = correctFile.getRules();
        correctPath = correctFile.getPath();

        missingRules = new ArrayList<Rule>();
        extraRules = new ArrayList<Rule>();
        numCorrect = 0;
        numExtraRules = 0;
        numTestRules = testRules.size();
        numCorrectRules = correctRules.size();
        success = false;
    }

    public RuleTester(RuleSets testRules, RulesFile correctFile) {
        this.testRules = getRulesAsMap(testRules.getRules());
        
        String newTestPath = "D:/Documents/MQP/results/testRules.txt";
        String newCorrectPath = "D:/Documents/MQP/results/correctRules.txt";
        try {
			writeRulesToFile(newTestPath, testRules.getRules());
		} catch (Exception e) {
			System.err.println("Error writing rules to file: " + newTestPath);
			e.printStackTrace();
		}
        try {
			writeRulesToFile(newCorrectPath, correctFile.getAssocRules());
		} catch (Exception e) {
			System.err.println("Error writing rules to file: " + newCorrectPath);
			e.printStackTrace();
		}
        
        testPath = "Test Rules";
        this.correctRules = correctFile.getRules();
        correctPath = correctFile.getPath();

        missingRules = new ArrayList<Rule>();
        extraRules = new ArrayList<Rule>();
        numCorrect = 0;
        numExtraRules = 0;
        numTestRules = this.testRules.size();
        numCorrectRules = this.correctRules.size();
        success = false;
    }
    
    private void writeRulesToFile(String path, List<AssocRule> rules) throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter pw = new PrintWriter(path, "UTF-8");
    	for (AssocRule r: rules) {
    		Rule rule = (Rule) r;
    		List<Item> ante = rule.getAnte();
    		Collections.sort(ante);
    		List<Item> cons = rule.getCons();
    		Collections.sort(cons);
    		for(Item a: ante) {
    			pw.write(a.getShortName() + " ");
    		}
    		pw.write("==> ");
    		for(Item c: cons) {
    			pw.write(c.getShortName() + " ");
    		}
    		pw.write("SUP: " + rule.getAbsoluteSupport() + " CONF: " + rule.getConfidence() + "\n");
    	}
    	pw.close();
    }
    
    private Map<Rule, Boolean> getRulesAsMap(List<AssocRule> rules) {
    	Map<Rule, Boolean> map = new HashMap<Rule, Boolean>();
        for (AssocRule r : rules) {
        	Rule rule = (Rule) r;
            map.put(rule, true);
        }
        return map;
    }

    public boolean getSuccess() {
        return success;
    }

    public void runTest() {
        //verify that testRules contains all rules from correctRules
        int count = 1;
        for (Rule c : correctRules) {
            //boolean found = false;
            // for (Rule t : testRules) {
            //     if (t.equals(c)) {
            //         found = true;
            //         this.numCorrect++;
            //         break;
            //     }
            // }
            if (testRules.containsKey(c)){
                this.numCorrect++;
            }
            else {
                missingRules.add(c);
            }
            //System.out.print("\rRule " + count + " of " + numCorrectRules + " Percent Complete: " + (100 * count)/numCorrectRules);
            count++;
        }

        //if both files contain the same number of rules and the number of correctly matched rules
        // equals the number of rules in the correct file, they are equivalent
        if(numTestRules == numCorrectRules && numCorrect == numCorrectRules){
            success = true;
        }
        else {
            numExtraRules = numTestRules - numCorrect;
            // //if the test file has rules not found in the correct file, find them
            // if (numExtraRules > 0) {
            //     for (Rule t : testRules) {
            //         boolean found = false;
            //         for (Rule c : correctRules) {
            //             if (t.equals(c)) {
            //                 found = true;
            //                 break;
            //             }
            //         }
            //         if (!found) {
            //             extraRules.add(t);
            //         }
            //     }
            // }
        }
    }

    public void printAnalysis() {
        String status = "FAILED";
        if(success)
            status = "SUCCESS";
        System.out.println("-----Rule Test: " + status + "---------------------------------------");
        System.out.println("File tested: " + testPath + " Correct File: " + correctPath);
        System.out.println("Number of rules in correct file: " + correctRules.size());
        System.out.println("Number of rules in test file: " + numTestRules);
        System.out.println("Correctly matched rules: " + numCorrect);
        System.out.println("Number of missing rules: " + missingRules.size());
        System.out.println("Missing rules: ");
        for(int i = 0; i < 5 && i < missingRules.size(); i++) {
            System.out.println(missingRules.get(i));
        }
        System.out.println("Number of extra rules: " + numExtraRules);
        System.out.println("Extra rules: ");
        // for(Rule r : extraRules) {
        //     System.out.println(r);
        // }
    }

}