/**
 * 
 */
package org.maras.testing;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.maras.framework.Group;
import org.maras.framework.Item;
import org.maras.framework.Rule;
import org.maras.framework.RuleSets;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

/**
 * 
 * 
 * Analyzes two sets of groups to determine whether they contain the same content.
 * 
 * @author Brian Zylich
 */
public class GroupTester implements Tester {

    private String testPath;
    private String correctPath;

    private Map<Group, Boolean> testGroups;
    private List<Group> correctGroups;

    private List<Group> missingGroups;
    private List<Group> extraGroups;
    private int numCorrect;
    private int numTestGroups;
    private int numCorrectGroups;
    private int numExtraGroups;

    private boolean success;

    public GroupTester(String testFile, String correctFile) {
    	
    	try {
    		testGroups = getGroupsAsMap(testFile);
    	}
    	catch(Exception e) {
    		System.err.println("Error reading groups from file: " + testFile);
    		e.printStackTrace();
    	}
        testPath = testFile;
        try {
        	correctGroups = getGroups(correctFile);
        }
    	catch(Exception e) {
    		System.err.println("Error reading groups from file: " + correctFile);
    		e.printStackTrace();
    	}
        correctPath = correctFile;

        missingGroups = new ArrayList<Group>();
        extraGroups = new ArrayList<Group>();
        numCorrect = 0;
        numExtraGroups = 0;
        numTestGroups = testGroups.size();
        numCorrectGroups = correctGroups.size();
        success = false;
    }
    
    private List<Group> getGroups(String groupFile) throws Exception {
    	List<Group> groups = new ArrayList<Group>();
    	InputStream fis = new FileInputStream(groupFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        Group group = null;
        String line;
        
        double score = Double.MIN_VALUE;
        boolean singleRules = false;
        Rule rule = null;
        
        
        while ((line = br.readLine()) != null) {
        	//parse group
        	if (line.contains("===Group")){
        		score = Double.parseDouble(line.split(":")[2].trim());
        	}
        	else if(line.contains("->") && !singleRules){
        		rule = getRule(line);
        		if(score != Double.MIN_VALUE && rule != null) {
        			group = new Group(rule);
        			group.setScore(score);
        		}
        		else {
        			System.err.println("Error reading group!");
        			throw new Exception();
        		}
        	}
        	else if(line.contains("*")) {
        		singleRules = true;
        	}
        	else if(line.contains("->") && singleRules){
        		rule = getRule(line);
        		group.addSingle(rule);
        	}
        	else{
        		//group is done start over
        		if(group != null){
        			groups.add(group);
        			//reset all group values
        			score = Double.MIN_VALUE;
        	        singleRules = false;
        	        rule = null;
        	        group = null;
        		}
        	}
        }
        
        br.close();
    	
        return groups;
    }
    
    private Rule getRule(String line) {
    	List<Item> antecedent = new ArrayList<Item>();
        List<Item> consequent = new ArrayList<Item>();
        int support = Integer.MIN_VALUE;
        double confidence = Double.MIN_VALUE;
    	
        String[] rule_info = line.split("\\|");
		String[] rule = rule_info[0].trim().split("->");
		String[] ante = rule[0].trim().split(" ");
		String[] cons = rule[1].trim().split(" ");
		antecedent = stringArrayToItems(ante, true);
		consequent = stringArrayToItems(cons, false);
		
		String[] rule_stats = rule_info[1].trim().split(",");
		support = (int)(Double.parseDouble(rule_stats[0].split("=")[1].trim()));
		confidence = Double.parseDouble(rule_stats[1].split("=")[1].trim().split("%")[0]);
        
		return new Rule(antecedent, consequent, 0, support, confidence, 0);
    }
    
    private Map<Group, Boolean> getGroupsAsMap(String groupFile) throws Exception {
    	Map<Group, Boolean> map = new HashMap<Group, Boolean>();
        List<Group> groups = getGroups(groupFile);
    	
        for(Group g: groups) {
        	map.put(g, true);
        }
        
    	return map;
    }
    
    private List<Item> stringArrayToItems(String[] array, boolean isDrug) {
        List<Item> itemList = new ArrayList<Item>();
        for (int i=0; i < array.length; i++) {
        	String itemName = array[i].substring(1, array[i].length()-1);
            itemList.add(Item.fromInt(Item.itemID(itemName,isDrug, false)));
        }
        return itemList;
    }

    public boolean getSuccess() {
        return success;
    }

    public void runTest() {
        //verify that testGroups contains all rules from correctGroups
        int count = 0;
        System.out.println("Testing correct groups...	");
        for (Group c : correctGroups) {
//        	if(count < 5) {
//        		System.out.println(c);
//        	}
            if (testGroups.containsKey(c)){
                this.numCorrect++;
            }
            else {
                missingGroups.add(c);
            }
            //System.out.print("\rRule " + count + " of " + numCorrectRules + " Percent Complete: " + (100 * count)/numCorrectRules);
            count++;
        }

        //if both files contain the same number of rules and the number of correctly matched rules
        // equals the number of rules in the correct file, they are equivalent
        if(numTestGroups == numCorrectGroups && numCorrect == numCorrectGroups){
            success = true;
        }
        else {
            numExtraGroups = numTestGroups - numCorrect;
        }
    }

    public void printAnalysis() {
        String status = "FAILED";
        if(success)
            status = "SUCCESS";
        System.out.println("-----Group Test: " + status + "---------------------------------------");
        System.out.println("File tested: " + testPath + " Correct File: " + correctPath);
        System.out.println("Number of groups in correct file: " + correctGroups.size());
        System.out.println("Number of groups in test file: " + numTestGroups);
        System.out.println("Correctly matched groups: " + numCorrect);
        System.out.println("Number of missing groups: " + missingGroups.size());
        System.out.println("Missing groups: ");
        for(int i = 0; i < 5 && i < missingGroups.size(); i++) {
            System.out.println(missingGroups.get(i));
        }
        System.out.println("Number of extra groups: " + numExtraGroups);
        System.out.println("Extra groups: ");
        for(int j = 0; j < 5 && j < extraGroups.size(); j++) {
            System.out.println(extraGroups.get(j));
        }
    }

}