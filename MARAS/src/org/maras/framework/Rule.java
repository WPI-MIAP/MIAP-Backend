package org.maras.framework;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.function.Consumer;

import org.maras.program.App;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class represent a MARAS association rule. A rule can be used to find a
 * group (a rule and all singleton association rules that can be derived from that rule).
 *
 * This class is an extension of the AssocRule class from the SPMF library put together by Philippe Fournier-Viger.
 * 
 * @see AssocRule
 * @see Item
 * @see Group
 * @see RuleSets
 * @author Brian Zylich
 */
public class Rule extends AssocRule implements Comparable<Rule> {
	
	/** Used to hold all known drug-drug interactions */
	private static Map<Rule, Boolean> knownRules = new HashMap<Rule, Boolean>();
	private static Object knownRuleMapLock = new Object();
	
//	public static void parseKnownRules(String fileName) {
//		InputStream fis = null;
//		BufferedReader br = null;
//		PrintWriter pw = null;
//		String line;
//		int count = 0;
//		try {
//			fis = new FileInputStream(fileName);
//			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
////			pw = new PrintWriter("D:/Documents/MQP/knownRules_standardized.txt", "UTF-8");
//			
//			while ((line = br.readLine()) != null) {
////				if(count > 0) {
////					String[] row = line.split("\t");
//					String[] row = line.split(",");
////					pw.println(row[2] + "," + row[3] + "," + row[5]);
//					List<Item> ante = new ArrayList<Item>();
//					List<Item> cons = new ArrayList<Item>();
//					try {
//						ante.add(Item.fromInt(Item.itemID(row[0], true, false)));
//						ante.add(Item.fromInt(Item.itemID(row[1], true, false)));
//						Collections.sort(ante);
//						cons.add(Item.fromInt(Item.itemID(row[2], false, false)));
////						pw.println(ante.get(0).getName() + "," + ante.get(1).getName() + "," + cons.get(0).getName());
//					}
//					catch(NoSuchElementException e) {
//						continue;
//					}
//					
//					Rule.addKnownRule(ante, cons);
////				}
//				if (count % 10000 == 0){
////					System.out.println("Known rules parsed: " + count);
////					System.out.println("Current rule: " + (new Rule(ante, cons)));
//				}
//				count++;
//			}
//		} catch (Exception e) {
//			System.err.println("Error reading known rules from " + fileName);
//			e.printStackTrace();
//		} finally {
//			try {
//				br.close();
//				fis.close();
////				pw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * Reads in a set of known rules that have already been converted to standardized forms. This function uses threading to accomplish this goal.
	 * 
	 * @param fileName
	 * 				The path of the file containing the standardized known rules.
	 */
	public static void parseStandardizedKnownRulesThreading(String fileName) {
		InputStream fis = null;
		BufferedReader br = null;
		String line;
		int count = 0;
		try {
			fis = new FileInputStream(fileName);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			List<String> lines = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			//create threads to process parts of the data
			final int numThreads = Runtime.getRuntime().availableProcessors()-1;
			List<Thread> threads = new ArrayList<Thread>();
			for(int i=0; i < numThreads; i++) {
				final int index = i;
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						int start = index * (lines.size() / numThreads);
						int nextStart = (index + 1) * (lines.size() / numThreads);
						int end = Math.min(nextStart, lines.size());
						int count = 0;
						for (int j=start; j < end; j++) {
							String[] row = lines.get(j).split(",");
							List<Item> ante = new ArrayList<Item>();
							List<Item> cons = new ArrayList<Item>();
							try {
								ante.add(Item.fromIntKnown(Item.itemIDKnown(row[0], true, true)));
								ante.add(Item.fromIntKnown(Item.itemIDKnown(row[1], true, true)));
								Collections.sort(ante);
								cons.add(Item.fromIntKnown(Item.itemIDKnown(row[2], false, true)));
							}
							catch(NoSuchElementException e) {
								continue;
							}
							
							Rule.addKnownRule(ante, cons);
							if (count % 10000 == 0){
//								System.out.println("Known rules parsed by thread " + index + ": " + count);
//								System.out.println("Current rule: " + (new Rule(ante, cons)));
							}
							count++;
						}
					}
				});
				threads.add(t);
				t.start();
			}
			for(Thread t : threads) {
				t.join();
			}
		} catch (Exception e) {
			System.err.println("Error reading known rules from " + fileName);
			App.handleException(e);
		} finally {
			try {
				br.close();
				fis.close();
			} catch (Exception e) {
				App.handleException(e);
			}
		}
	}
	
	/**
	 * Reads in a set of known rules that have not already been converted to standardized forms. Then, the standardized known rules
	 * are written to a file. This function uses threading to accomplish this goal. This function should only be run once or whenever
	 * the known rules file changes and a new standardized known rules file must be generated.
	 * 
	 * @param fileName
	 * 				The path of the file containing the known rules.
	 */
	public static void standardizeAllKnownRulesThreading(String fileName) {
		InputStream fis = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		String line;
		int count = 0;
		try {
			fis = new FileInputStream(fileName);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			pw = new PrintWriter("D:/Documents/MQP/knownRules_standardized.csv", "UTF-8");
			List<String> lines = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			System.out.println("Number of lines read: " + lines.size());
			
			//create threads to process parts of the data
			final int numThreads = Runtime.getRuntime().availableProcessors()-1;
			System.out.println("Number of available processors: " + (numThreads+1));
			System.out.println("Number of threads created: " + numThreads);
			List<Thread> threads = new ArrayList<Thread>();
			for(int i=0; i < numThreads; i++) {
				final int index = i;
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("Thread " + index + " started running!");
						int start = index * (lines.size() / numThreads);
						int nextStart = (index + 1) * (lines.size() / numThreads);
						int end = Math.min(nextStart, lines.size());
						int count = 0;
						for (int j=start; j < end; j++) {
							String[] row = lines.get(j).split(",");
							List<Item> ante = new ArrayList<Item>();
							List<Item> cons = new ArrayList<Item>();
							ante.add(Item.makeItem(row[0], true, false));
							ante.add(Item.makeItem(row[1], true, false));
							Collections.sort(ante);
							cons.add(Item.makeItem(row[2], false, false));
							Rule.addKnownRule(ante, cons);
							if (count % 10000 == 0){
								System.out.println("Known rules parsed by thread " + index + ": " + count);
								System.out.println("Current rule: " + (new Rule(ante, cons)));
							}
							count++;
						}
					}
				});
				threads.add(t);
				t.start();
			}
			for(Thread t : threads) {
				t.join();
			}
			
			//print all rules to file
			int i = 0;
			for(Rule r : Rule.knownRules.keySet()) {
				i++;
				pw.println(r.getAnte().get(0).getShortName() + "," + r.getAnte().get(1).getShortName() + "," + r.getCons().get(0).getShortName());
			}
			System.out.println(i);
			
		} catch (Exception e) {
			System.err.println("Error reading known rules from " + fileName);
			App.handleException(e);
		} finally {
			try {
				br.close();
				fis.close();				
				pw.close();
			} catch (Exception e) {
				App.handleException(e);
			}
		}
	}
	
	/**
	 * Adds a new rule, created from the given antecedent and consequent, to the map of known rules. This function is protected by a lock
	 * to allow for multithreading.
	 * 
	 * @param antecedent
	 * 				A list of items corresponding to the drugs involved in the rule.
	 * @param consequent
	 * 				A list of items corresponding to the reactions involved in the rule.
	 */
	private static void addKnownRule(List<Item> antecedent, List<Item> consequent) {
		synchronized (knownRuleMapLock) {
			knownRules.put(new Rule(antecedent, consequent), true);
		}
	}

    /** Used to track which FAERS reports support this rule */
    private List<Integer> reportIds;

    /** List of Item objects forming the antecedent */
    private final List<Item> antecedent;

    /** List of Item objects forming the consequent */
    private final List<Item> consequent;

    /** Boolean indicating if the rule corresponds to a known drug-drug interaction */
    private boolean known = false;
    
    /**
	 * Constructor
	 * 
	 * @param itemset1
	 *            the antecedent of the rule (an itemset)
	 * @param itemset2
	 *            the consequent of the rule (an itemset)
	 * @param supportAntecedent the coverage of the rule (support of the antecedent)
	 * @param transactionCount
	 *            the absolute support of the rule (integer)
	 * @param confidence
	 *            the confidence of the rule
	 * @param lift   the lift of the rule
	 */
    public Rule(List<Item> itemset1, List<Item> itemset2, int supportAntecedent, int transactionCount,
     double confidence, double lift) {
        super(getItemIds(itemset1), getItemIds(itemset2), supportAntecedent, transactionCount, confidence, lift);
        this.antecedent = itemset1;
        this.consequent = itemset2;
        Collections.sort(this.antecedent);
		Collections.sort(this.consequent);
    }
    
    /**
	 * Constructor - only to be used for known DDRs (don't care about their support, confidence, etc)
	 * 
	 * @param itemset1
	 *            the antecedent of the rule (an itemset)
	 * @param itemset2
	 *            the consequent of the rule (an itemset)
	 */
    private Rule(List<Item> itemset1, List<Item> itemset2) {
    	super(getItemIds(itemset1), getItemIds(itemset2), -1, -1, -1, -1);
        this.antecedent = itemset1;
        this.consequent = itemset2;
        Collections.sort(this.antecedent);
		Collections.sort(this.consequent);
		this.known = true;
    }

    /**
     * Helper function used to turn a list of Items into an array of integers representing those items.
     * @return the array of integer IDs.
     */
    private static int[] getItemIds(List<Item> itemList) {
        int[] intArray = new int[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            intArray[i] = itemList.get(i).getiD();
        }
        return intArray;
    }

    /**
     * Returns the list of item objects representing the antecedent of the rule.
     * @return the list of item objects.
     */
    public List<Item> getAnte() {
        return this.antecedent;
    }

    /**
     * Returns the list of item objects representing the consequent of the rule.
     * @return the list of item objects.
     */
    public List<Item> getCons() {
        return this.consequent;
    }

    /**
     * Creates a Group from this Rule that includes this Rule and all subrules that can be
     * derived from this Rule.
     * @return the Group created from this Rule.
     */
    public Group getGroup(RuleSets rules) {
        Group newGroup = new Group(this);
        for (AssocRule arule : rules.getRules()) {
            Rule rule = (Rule)arule;
            if(rule.getAnte().size() < antecedent.size() && antecedent.containsAll(rule.getAnte())) {
                if(rule.getCons().size() == consequent.size() && rule.getCons().containsAll(consequent)) {
                    newGroup.addSingle(rule);
                }
            }
        }
        return newGroup;
    }

    /** 
     * Used to find the interaction corresponding to this rule. 
     * @return InteractionSet containing all items from the antecedent and the consequent of the rule.
     */
    public Interaction getInteraction() {
        Set<Item> itemset = new HashSet();
        int i = 0;
        for (Item a : antecedent) {
            itemset.add(a);
        }
        for (Item c : consequent) {
            itemset.add(c);
        }
        return new Interaction(itemset, this.getAbsoluteSupport());
    }

    /** 
     * Used to find which FAERS reports support this association rule. 
     * @return list of report ids.
     */
    public List<Integer> getReports() {
        return reportIds;
    }
    
    /**
     * Used to check whether a rule is known or unknown, modifying the 'known' variable within the Rule object. 
     * This function should be run after loading all known rules.
     */
    public void checkKnown() {
    	boolean known = false;
    	
    	if (Rule.knownRules.containsKey(this.getSimpleRule())) {
    		this.known = true;
    	}
    }
    
    /**
     * Used to determine if this rule corresponds to a known drug-drug interaction
     * @return boolean indicating if rule is known
     */
    public boolean isKnown() {
    	return known;
    }
    
    @Override
	public int hashCode() {
		String hash = this.toString();
		return hash.hashCode();
	}
    
    /**
     * Used to compare a newly generated rule against known rules by simplifying the rule for hashing.
     * @return Simplified version of the rule containing only the antecedent and consequent
     */
    private Rule getSimpleRule(){
    	return new Rule(this.antecedent, this.consequent);
    }
    
    /**
     * Check whether two rules have the same items in their antecedent and consequent
     * @param other The other rule to compare to
     * @return boolean indicating whether the antecedents and consequents match
     */
    public boolean sameRule(Rule other) {
    	//check antecedent
		List<Item> otherAnte = other.getAnte();
		List<Item> ante = this.getAnte();
		for (int i = 0; i < ante.size(); i++) {
			String currentItem = ante.get(0).getShortName();
			boolean found = false;
			for (int j = 0; j < otherAnte.size(); j++) {
				if(currentItem.equals(otherAnte.get(j).getShortName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}

		//check consequent
		List<Item> otherCons = other.getCons();
		List<Item> cons = this.getCons();
		for (int i = 0; i < cons.size(); i++) {
			String currentItem = cons.get(0).getShortName();
			boolean found = false;
			for (int j = 0; j < otherCons.size(); j++) {
				if(currentItem.equals(otherCons.get(j).getShortName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
    }
    
    /**
     * Check whether two rules have the same items in their antecedent and consequent, as well as the same support and confidence values.
     * @param other The other rule to compare to
     * @return boolean indicating whether the rules are the same
     */
    @Override 
	public boolean equals(Object other) {
		if (other instanceof Rule) {
			Rule otherRule = (Rule) other;

			if(this.known || otherRule.isKnown()) {
				return sameRule(otherRule);
			}

			DecimalFormat decimalFormat;
			if(this.getConfidence() > 1){
				decimalFormat = new DecimalFormat("#00.0");
			}
			else{
				decimalFormat = new DecimalFormat("#0.000");
			}
			String formattedSupport = "" + this.getAbsoluteSupport();
			String formattedConfidence = decimalFormat.format(this.getConfidence());
			String otherFormattedSupport = "" + otherRule.getAbsoluteSupport();
			String otherFormattedConfidence = decimalFormat.format(otherRule.getConfidence());
			if(this.antecedent.size() != otherRule.antecedent.size() || this.consequent.size() != otherRule.consequent.size() || 
				!formattedSupport.equals(otherFormattedSupport) || !formattedConfidence.equals(otherFormattedConfidence)) {
				return false;
			}
			else {
				return sameRule(otherRule);
			}
		}
		else {
			return false;
		}
	}
    
    /**
     * Get a string representation of the rule, including the rule's antecedent, consequent, support, and confidence.
     * @return string representation of the rule
     */
    @Override
    public String toString() {
    	String str = "";
		for (Item a : this.antecedent) {
			str += a.getShortName() + " ";
		}
		str += "==> ";
		for (Item c : this.consequent) {
			str += c.getShortName() + " ";
		}
		DecimalFormat decimalFormat;
		if(this.getConfidence() > 1) {
			decimalFormat = new DecimalFormat("#00.0");
		}
		else {
			decimalFormat = new DecimalFormat("#0.000");
		}
		String formattedSupport = "" + this.getAbsoluteSupport();
		String formattedConfidence = decimalFormat.format(this.getConfidence());
		str += "Supp: " + formattedSupport + " Conf: " + formattedConfidence;
    	
    	return str;
    }
    
    /**
     * Comparator used to sort rules in groups for hashing and to facilitate testing.
     */
    @Override
    public int compareTo(Rule r) {
    	int i = 0;
    	for(Item a : r.getAnte()) {
    		int compare = a.compareTo(this.antecedent.get(i));
    		if(compare != 0) {
    			return compare;
    		}
    		i++;
    	}
    	i = 0;
    	for(Item c : r.getCons()) {
    		int compare = c.compareTo(this.consequent.get(i));
    		if(compare != 0) {
    			return compare;
    		}
    		i++;
    	}
    	
    	return 0;
    }
    
}