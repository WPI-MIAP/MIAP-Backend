/**
 * 
 */
package org.maras.testing;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import org.maras.framework.Interaction;
import org.maras.framework.InteractionSets;
import org.maras.framework.Item;
import org.maras.framework.Rule;

import java.util.List;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 * 
 * Analyzes two sets of itemsets to determine whether they contain the same content.
 * 
 * @author Brian Zylich
 */
public class ItemsetTester implements Tester {

    private InteractionSets testItemsets;
    private InteractionSets correctItemsets;

    private List<List<Itemset>> testLevels;
    private List<List<Itemset>> correctLevels;

    private String testPath;
    private String correctPath;

    private List<Itemset> missingItemsets;
    private List<Itemset> extraItemsets;
    private int numCorrect;
    private int numTestItemsets;
    private int numCorrectItemsets;
    private int numExtraItemsets;

    private boolean success;

    public ItemsetTester(ItemsetFile testFile, ItemsetFile correctFile) {
        testItemsets = testFile.getItemsets();
        testPath = testFile.getPath();
        correctItemsets = correctFile.getItemsets();
        correctPath = correctFile.getPath();

        testLevels = testItemsets.getLevels();
        correctLevels = correctItemsets.getLevels();

        missingItemsets = new ArrayList<Itemset>();
        extraItemsets = new ArrayList<Itemset>();
        numCorrect = 0;
        numExtraItemsets = 0;
        numTestItemsets = this.testItemsets.getItemsetsCount();
        numCorrectItemsets = this.correctItemsets.getItemsetsCount();
        success = false;
    }

    public ItemsetTester(InteractionSets testItemsets, ItemsetFile correctFile) {
        this.testItemsets = testItemsets;
        testPath = "Test Itemsets";
        this.correctItemsets = correctFile.getItemsets();
        correctPath = correctFile.getPath();
        testLevels = testItemsets.getLevels();
        correctLevels = correctItemsets.getLevels();
        
        String newTestPath = "D:/Documents/MQP/results/testItemsets.txt";
        String newCorrectPath = "D:/Documents/MQP/results/correctItemsets.txt";
        try {
			writeItemsetsToFile(newTestPath, testLevels);
		} catch (Exception e) {
			System.err.println("Error writing itemsets to file: " + newTestPath);
			e.printStackTrace();
		}
        try {
			writeItemsetsToFile(newCorrectPath, correctLevels);
		} catch (Exception e) {
			System.err.println("Error writing itemsets to file: " + newCorrectPath);
			e.printStackTrace();
		}

        missingItemsets = new ArrayList<Itemset>();
        extraItemsets = new ArrayList<Itemset>();
        numCorrect = 0;
        numExtraItemsets = 0;
        numTestItemsets = this.testItemsets.getItemsetsCount();
        numCorrectItemsets = this.correctItemsets.getItemsetsCount();
        success = false;
    }
    
    private void writeItemsetsToFile(String path, List<List<Itemset>> itemsets) throws FileNotFoundException, UnsupportedEncodingException {
    	PrintWriter pw = new PrintWriter(path, "UTF-8");
    	for (List<Itemset> i: itemsets) {
    		for (Itemset j: i) {
    			Interaction itemset = (Interaction) j;
        		List<Item> items = setToListItems(itemset.getInteractions());
        		Collections.sort(items);
        		for(Item a: items) {
        			pw.write(a.getShortName() + " ");
        		}
        		pw.write("SUP: " + itemset.getSupport() + "\n");
    		}
    	}
    	pw.close();
    }
    
    private List<Item> setToListItems(Set<Item> set) {
    	List<Item> list = new ArrayList<Item>();
    	for(Item i : set) {
    		list.add(i);
    	}
    	return list;
    }

    public boolean getSuccess() {
        return success;
    }

    public void runTest() {
        //verify that the test file has every itemset found in the correct file
        for ( int level = 0; level < correctLevels.size(); level++) {
            for (Itemset i : correctLevels.get(level)) {
                boolean found = false;
                for (Itemset j : testLevels.get(level)) {
                    if (itemsetsEquivalent(i, j)) {
                        found = true;
                        this.numCorrect++;
                        break;
                    }
                }
                if (!found) {
                    missingItemsets.add(i);
                }
            }
        }
        //if both files contain the same number of itemsets and the number of correctly matched itemsets
        // equals the number of itemsets in the correct file, they are equivalent
        if(numTestItemsets == numCorrectItemsets && numCorrectItemsets == numCorrect){
            success = true;
        }
        else {
            numExtraItemsets = numTestItemsets - numCorrect;
            //if the test file has itemsets not found in the correct file, find them
            if (numExtraItemsets > 0) {
                for ( int level = 0; level < testLevels.size(); level++) {
                    for (Itemset i : testLevels.get(level)) {
                        boolean found = false;
                        for (Itemset j : correctLevels.get(level)) {
                            if (itemsetsEquivalent(i, j)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            extraItemsets.add(i);
                        }
                    }
                }
            }
        }
    }
    
    /** 
     * Helper function used to determine if two itemsets are equivalent.
     * */
    private boolean itemsetsEquivalent(Itemset itemset1, Itemset itemset2) {
    	Interaction i1 = (Interaction) itemset1;
    	Interaction i2 = (Interaction) itemset2;
    	if(i1.getSupport() != i2.getSupport()) {
    		return false;
    	}
    	Set<Item> items1 = i1.getInteractions();
    	Set<Item> items2 = i2.getInteractions();
    	if(items1.size() != items2.size()) {
    		return false;
    	}
    	else {
    		for(Item i : items1) {
    			boolean found = false;
    			for(Item j : items2) {
    				if(i.getShortName().equals(j.getShortName())) {
    					found = true;
    					break;
    				}
    			}
    			if(!found) {
    				return false;
    			}
    		}
    	}
    	return true;
    }

    public void printAnalysis() {
        String status = "FAILED";
        if(success)
            status = "SUCCESS";
        System.out.println("-----Itemset Test: " + status + "---------------------------------------");
        System.out.println("File tested: " + testPath + " Correct File: " + correctPath);
        System.out.println("Number of itemsets in correct file: " + numCorrectItemsets);
        System.out.println("Number of itemsets in test file: " + numTestItemsets);
        System.out.println("Correctly matched itemsets: " + numCorrect);
        System.out.println("Number of missing itemsets: " + missingItemsets.size());
        System.out.println("Missing itemsets: ");
        for(int i = 0; i < 5 && i < missingItemsets.size(); i++) {
            System.out.println(((Interaction)missingItemsets.get(i)));
        }
        System.out.println("Number of extra itemsets: " + numExtraItemsets);
        System.out.println("Extra itemsets: ");
        for(Itemset i : extraItemsets) {
            //System.out.println(i);
        }
    }

}