package org.maras.framework;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Group contains a complete ADR rule and its singleton component
 * associations.
 */
public class Group implements Comparable<Group> {
	private Rule comb;
	private ArrayList<HashSet<Rule>> singles;

	// What is this for?
	private double[] kl_divergences;

	private double score;

	/**
	 * Creates a group from a combined rule. Populates the bins for singletons, but
	 * does not find them. Does not calculate the group score.
	 *
	 * @param comb
	 *            The rule to use as a basis for the group.
	 */
	public Group(Rule comb) {
		this.comb = comb;
		singles = new ArrayList<HashSet<Rule>>();
		for (int i = 0; i < comb.getAnte().size() - 1; i++) {
			singles.add(new HashSet<>());
		}

		score = 0;
	}

	/**
	 * Set the kl divergences to a new array.
	 *
	 * @param klds
	 *            A list of KL divergence values.
	 */
	public void setKLDs(double[] klds) {
		kl_divergences = klds;
	}

	/**
	 *
	 * @return the array of kl divergences.
	 */
	public double[] getKLDs() {
		return kl_divergences;
	}

	/**
	 * The algorithm for calculating the score. Updates the score in the group but
	 * not the composite rule.
	 *
	 * <code>score = ((overall_confidence - average_confidence) * (1 -
	 * (conf_std_dev / avg))) / number_of_comb</code>
	 */
	public void calcScore() {
		double alpha = 1; // 0-1
		double improvement = 0;
		double linear_weight = 1;

		double decrease = 1 / singles.size();

		for (int i = 0; i < singles.size(); i++) {
			// if (singles.get(i).size() == 0) {
			// 	improvement += 0;
			// 	linear_weight -= decrease;
			// 	continue;
			// }
			double avg = 0;
			double deviation = 0;
			for (Rule r : singles.get(i)) {
				avg += r.getConfidence();
			}
			avg /= singles.get(i).size();

			for (Rule r : singles.get(i)) {
				deviation += Math.pow((r.getConfidence() - avg), 2);
			}
			deviation = Math.sqrt(deviation / singles.get(i).size());

			improvement += linear_weight * (comb.getConfidence() - avg) * (1 - alpha * deviation / avg);
			linear_weight -= decrease;
		}
		// if(singles.size() == 0){
		// 	score = 0;
		// }
		// else {
			score = improvement / singles.size();
		// }
	}

	/**
	 * @return The group score.
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * Set the score of a group. Used for testing to ensure that 
	 * the groups generated match those from another file.
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return The composite rule for the group.
	 */
	public Rule getCompositeRule() {
		return comb;
	}

	/**
	 * @return An arraylist of the Hashset for singleton rules.
	 */
	public ArrayList<HashSet<Rule>> getSingletonRules() {
		return singles;
	}

	/**
	 * Comparator for descending sorting of groups. This comparator behaves opposite
	 * to convention.
	 */
	@Override
	public int compareTo(Group g) {
		double score_comp = g.getScore();

		if (score < score_comp) {
			return 1;
		} else if (score == score_comp) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * Adds a singleton to the appropriate bin.
	 * 
	 * @param rule
	 *            The singleton rule to add.
	 */
	public void addSingle(Rule rule) {
		singles.get(rule.getAnte().size() - 1).add(rule);
	}
	
	@Override
	public int hashCode() {
		String hash = this.toString();	
		
		return hash.hashCode();
	}
	
	@Override
	public String toString() {
		String str = "";
		
		str += "Main rule: ";
		str += getCompositeRule().toString();
		str += "\nSingle rules: ";
		for (HashSet<Rule> level : singles) {
			List<Rule> levelList = new ArrayList<Rule>();
			levelList.addAll(level);
			Collections.sort(levelList);
			for (Rule r : levelList) {
				str += r.toString() + "\n";
			}
		}
		DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		String formattedScore = decimalFormat.format(this.score);
		str += "Score: " + formattedScore;
		
		return str;
	}
    
    @Override 
	public boolean equals(Object other) {
    	if (other instanceof Group) {
    		Group otherGroup = (Group) other;
    		if(Math.abs(this.score-otherGroup.getScore()) < 0.001) {
    			if(getCompositeRule().toString().equals(otherGroup.getCompositeRule().toString())){
    				ArrayList<HashSet<Rule>> thisSingles = this.singles;
    				ArrayList<HashSet<Rule>> otherSingles = otherGroup.getSingletonRules();
    				if(thisSingles.size() == otherSingles.size()){
    					for(int i=0; i < thisSingles.size(); i++) {
    						if(thisSingles.get(i).size() == otherSingles.get(i).size()) {
    							for(Rule r: thisSingles.get(i)){
    								boolean found = false;
    								for(Rule z: otherSingles.get(i)) {
    									if(r.toString().equals(z.toString())){
    										found = true;
    										break;
    									}
    								}
    								if(!found) {
    									return false;
    								}
    							}
    						}
    						else {
    							return false;
    						}
    					}
    					return true;
    				}
    				else{
    					return false;
    				}
    			}
    			else {
    				return false;
    			}
    		}
    		else {
    			return false;
    		}
    	}
    	else {
    		return false;
    	}
    }
}