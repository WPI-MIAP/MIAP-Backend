package org.maras.framework;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * A report maps to a single entry in the FARAS data recovered for use.
 * 
 * @author Andrew Schade
 *
 */
public class Report implements Comparable<Report>, Iterable<Item> {
	private String id;
	private final Set<Drug> drugs;
	private final Set<Item> reactions;
	private int transactionLength;
	private final TreeSet<Item> combined;
	
	public Report(Set<Drug> drugs, Set<Item> reactions, String id) {
		this.drugs = drugs;
		this.reactions = reactions;
		this.id = id;
		this.transactionLength = this.drugs.size() + this.reactions.size(); // TODO: Check the accuracy of this

		combined = new TreeSet<>();
		combined.addAll(drugs);
		combined.addAll(reactions);
	}

	public Set<Drug> getDrugs() {
		return drugs;
	}

	public Set<Item> getReactions() {
		return reactions;
	}

	public int getTransactionLength() {
		return transactionLength;
	}

	/**
	 * This function compares two reports to each other, allowing sorting by
	 * ascending transaction length. In cases where the transaction length is the
	 * same, the ordering will use the ordering determined by the drugs and
	 * reactions in the reports, starting with the first.
	 */
	@Override
	public int compareTo(Report other) {
		if (other.transactionLength < transactionLength)
			return 1;
		else if (other.transactionLength > transactionLength)
			return -1;
		else {
			Item[] thisDrugs = drugs.toArray(new Item[0]);
			Item[] otherDrugs = other.drugs.toArray(new Item[0]);

			int thisDrugLength = thisDrugs.length;
			int otherDrugLength = otherDrugs.length;

			for (int i = 0; i < Math.min(thisDrugLength, otherDrugLength); i++) {
				int returnVal = thisDrugs[i].compareTo(otherDrugs[i]);
				if (returnVal != 0)
					return returnVal;
			}
			if (thisDrugLength < otherDrugLength) return -1;
			else if (thisDrugLength > otherDrugLength) return 1;
			else 
			{
				Item[] thisReaction = reactions.toArray(new Item[0]);
				Item[] otherReaction = other.reactions.toArray(new Item[0]);

				//Lengths guaranteed same.
				for (int i = 0; i < thisReaction.length; i++) {
					int returnVal = thisReaction[i].compareTo(otherReaction[i]);
					if (returnVal != 0)
						return returnVal;
				}
				return 0;
			}
		}
	}
	
	@Override
	public String toString() {
		String result = "";
		
		int count = 0;
		for(Item drug : this.drugs) {
			result += drug.getShortName();
			if(count < this.drugs.size()-1){
				result += " ";
			}
			count++;
		}
		result += " => ";
		count = 0;
		for(Item reaction : this.reactions) {
			result += reaction.getShortName();
			if(count < this.reactions.size()-1){
				result += " ";
			}
			count++;
		}
		
		return result;
	}

	@Override
	public Iterator<Item> iterator() {
		
		return combined.iterator();
	}
}
