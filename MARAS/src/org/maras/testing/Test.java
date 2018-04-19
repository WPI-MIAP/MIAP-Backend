/**
 * 
 */
package org.maras.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * Analyzes the itemset generation and rule mining processes to determine whether the generated outputs
 * are correct, as determined by comparing them with outputs that are labeled as correct.
 * 
 * @author Brian Zylich
 */
public class Test {

    /**
     *
     *
     */
    public static void main(String[] args) {
        List<Tester> tests = new ArrayList();
        int numSuccessfulTests = 0;

        //test the itemsets files
        String cppItemsetPath = "D:/Documents/MQP/diva-cpp/diva-mqp/data/itemsets.txt";
        String javaItemsetPath = "D:/Documents/MQP/diva-mqp/data/itemsets.txt"; 
        String cppAdeMapPath = "D:/Documents/MQP/results/ade_map";
        String cppDrugMapPath = "D:/Documents/MQP/results/drug_map";
        String javaAdeMapPath = "D:/Documents/MQP/diva-mqp/data/ade_map";
        String javaDrugMapPath = "D:/Documents/MQP/diva-mqp/data/drug_map";

        ItemsetFile cppItemsetFile = new ItemsetFile(cppItemsetPath, ItemsetFile.FORMAT_SPACE_SEPARATED, cppAdeMapPath, cppDrugMapPath);
        ItemsetFile javaItemsetFile = new ItemsetFile(javaItemsetPath, ItemsetFile.FORMAT_SPMF, javaAdeMapPath, javaDrugMapPath);

        ItemsetTester itemsetTester = new ItemsetTester(javaItemsetFile, cppItemsetFile);
        tests.add(itemsetTester);

        // test the rules files
        String cppRulesPath = "D:/Documents/MQP/diva-cpp/diva-mqp/data/rules.txt";
        String javaRulesPath = "D:/Documents/MQP/diva-mqp/data/rules.txt"; 

        RulesFile cppRulesFile = new RulesFile(cppRulesPath, RulesFile.FORMAT_SPACE_SEPARATED, cppAdeMapPath, cppDrugMapPath);
        RulesFile javaRulesFile = new RulesFile(javaRulesPath, RulesFile.FORMAT_SPMF, javaAdeMapPath, javaDrugMapPath);

        RuleTester ruleTester = new RuleTester(javaRulesFile, cppRulesFile);
        tests.add(ruleTester);

        for(Tester t: tests){
            t.runTest();
            if (t.getSuccess()) {
                numSuccessfulTests++;
            }
            t.printAnalysis();
        }

        System.out.println("" + tests.size() + " test(s) completed. " + numSuccessfulTests + " test(s) were successful.");
        System.out.println("Test success rate: " + ((double) numSuccessfulTests / tests.size()));
    }
}