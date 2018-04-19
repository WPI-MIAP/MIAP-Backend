package org.maras.framework;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;

/**
 * This class represents a set of Interactions, where an Interaction is a 
 * set of items with an associated support count. Interactions are ordered by 
 * size. For example, interactionSet 1 is a set of interactions of size 1 (contains
 * 1 item).
 * 
 * Based on SPMF Itemsets data type by Philippe Fournier-Viger
 * @author Brian McCarthy
 * @author Andrew Schade
 *
 */

public class InteractionSets extends Itemsets{

	private String name;
	
	public InteractionSets(String name) {
		super(name);
		this.name = name;
	}

	/**
	 * This method finds all the interactions within this collection where for all k
	 * greater than the level of a given interaction, the interaction is a member of the
	 * closure if and only if the interaction is not a subset of any interaction with a
	 * level of k.
	 * 
	 * @return The K-interactionsets with only closed interaction contained inside.
	 */
	public InteractionSets getClosures()
	{
		InteractionSets closure = new InteractionSets(this.name + " Closure");
		for (int i = 1; i < this.getLevels().size() - 1; i++) {
			for (Itemset is : this.getLevels().get(i)) {
				boolean isClosure = true; // Assumption for proof by contradiction
				for (Itemset superIs : this.getLevels().get(i + 1)) {
					if (superIs.containsAll(is) && is.getAbsoluteSupport() == superIs.getAbsoluteSupport()) {
						isClosure = false;
						break;
					}
				}
				if (isClosure) {
					closure.addItemset(is, is.size());
				}
			}
		}
		for (Itemset is : this.getLevels().get(this.getLevels().size() - 1))
		// All itemsets of highest level are closures
		{
			closure.addItemset(is, this.getLevels().size() - 1);
		}
		return closure;
	}

}
