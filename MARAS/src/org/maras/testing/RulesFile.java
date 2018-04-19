/**
 * 
 */
package org.maras.testing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

import org.maras.framework.Item;
import org.maras.framework.Rule;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

/**
 * 
 * 
 * Analyzes the itemset generation and rule mining processes to determine whether the generated outputs
 * are correct, as determined by comparing them with outputs that are labeled as correct.
 * 
 * @author Brian Zylich
 */
public class RulesFile {
    public static final int FORMAT_SPACE_SEPARATED = 0;
    public static final int FORMAT_SPMF = 1;
    
    private String path;
    private int format;

    private List<AssocRule> rules;
    private Map<Rule, Boolean> ruleMap;
    
    private Map<String, String> ade_map;
	private Map<String, String> drug_map;

    /**
     *
     */
    public RulesFile(String path, int format, String ade_map_path, String drug_map_path) {
    	ade_map = new HashMap<String, String>();
		drug_map = new HashMap<String, String>();
		
		try {
            readMap(ade_map_path, drug_map_path);
        }
        catch (IOException e){
            System.err.println("Error reading ade and drug maps!");
        }
		
        this.path = path;
        this.format = format;
        this.rules = new ArrayList<AssocRule>();
        this.ruleMap = new HashMap<Rule, Boolean>();
        try {
            readFile();
        }
        catch (IOException e){
            System.err.println("Error reading rule file: " + path);
        }
    }
    
    /**
     * Helper function to read in the maps for drugs and reactions in order to allow us to
     * compare the rules held in memory to rules in a file even if the mapping is different.
     * 
     * @param ade_file
     * @param drug_file
     * @throws IOException
     */
    private void readMap(String ade_file, String drug_file) throws IOException {

		InputStream fis = new FileInputStream(ade_file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis,
				"UTF-8"));
		String line;

		while ((line = br.readLine()) != null) {
			String[] item_id = line.split("\01");
			ade_map.put(item_id[1].trim(), item_id[0].trim());
		}

		br.close();
		fis.close();

		fis = new FileInputStream(drug_file);
		br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

		while ((line = br.readLine()) != null) {
			String[] item_id = line.split("\01");
			drug_map.put(item_id[1].trim(), item_id[0].trim());
		}

		br.close();
		fis.close();

		// for(String key : drug_map.keySet()){
		// System.out.println(key);
		// }

		System.out.println("The # of drugs is " + drug_map.size());
		System.out.println("The # of ade is " + ade_map.size());

	}

    private void readFile() throws IOException {
        InputStream fis = new FileInputStream(path);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

        Rule rule;

        String line;

        while ((line = br.readLine()) != null) {
            switch(format) {
                case FORMAT_SPACE_SEPARATED:
                    String[] rule_info = line.split("\\|");

                    //System.out.println("Support: \"" + rule_info[1] + "\" Confidence: \"" + rule_info[2] + "\"");
                    int supp = Integer.parseInt(rule_info[1]);
                    double conf = Double.valueOf(rule_info[2]);

                    String[] ruleString = rule_info[0].split(">");
                    String[] ante = ruleString[0].replaceAll(",", " ").trim().split(" ");
                    String[] cons = ruleString[1].replaceAll(",", " ").trim().split(" ");

                    rule = new Rule(stringArrayToItems(ante), stringArrayToItems(cons), 0, supp, conf, 0);
                    rules.add(rule);
                    break;
                case FORMAT_SPMF:
                    rule_info = line.split("#SUP:");
                    String[] descriptors = rule_info[1].replaceAll(" ", "").trim().split("#CONF:");

                    supp = Integer.parseInt(descriptors[0]);
                    conf = Double.valueOf(descriptors[1]);

                    ruleString = rule_info[0].split("==>");
                    ante = ruleString[0].trim().split(" ");
                    cons = ruleString[1].trim().split(" ");

                    rule = new Rule(stringArrayToItems(ante), stringArrayToItems(cons), 0, supp, conf, 0);
                    rules.add(rule);
                    break;
                default:
                    break;
            }
        }
        
        fis.close();
        br.close();
    }

    private List<Item> stringArrayToItems(String[] array) {
        List<Item> itemList = new ArrayList<Item>();
        for (int i=0; i < array.length; i++) {
            itemList.add(Item.fromInt(Item.itemID(getNameFromMap(array[i]),isDrug(array[i]), false)));
        }
        return itemList;
    }
    
    private boolean isDrug(String id) {
    	if(ade_map.containsKey(id)) {
    		return false;
    	}
    	else if(drug_map.containsKey(id)) {
    		return true;
    	}
    	
    	//not found
    	System.err.println("(Rule) Item not found in map!");
    	throw new NoSuchElementException();
    }
    
    private String getNameFromMap(String id) {
    	if(ade_map.containsKey(id)) {
    		return ade_map.get(id);
    	}
    	else if(drug_map.containsKey(id)) {
    		return drug_map.get(id);
    	}
    	
    	//not found
    	System.err.println("(Rule) Item not found in map!");
    	return null;
    }

    public List<Rule> getRules() {
    	List<Rule> newRules = new ArrayList<Rule>();
    	for(AssocRule ar: rules) {
    		newRules.add((Rule) ar);
    	}
        return newRules;
    }
    
    public List<AssocRule> getAssocRules() {
    	return rules;
    }
    
    public Map<Rule, Boolean> getRulesAsMap() {
        for (AssocRule r : rules) {
            ruleMap.put((Rule)r, true);
        }
        return ruleMap;
    }

    public String getPath(){
        return path;
    }

}