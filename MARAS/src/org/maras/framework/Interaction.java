package org.maras.framework;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;

/**
 * This class represents a set of items and a support count.
 * This implementation is based on the SPMF Itemset data type by Philippe Fournier-Viger.
 * @author Brian McCarthy
 *
 */
public class Interaction extends Itemset {

	private final Set<Item> items;
	
	public Interaction(Set<Item> items, int support) {
		super();
		int[] itemIds = new int[items.size()];
		int i = 0;
		for(Item item : items) {
			itemIds[i] = item.getiD();
			i++;
		}
		this.itemset = itemIds;
		this.items = items;
		this.support = support;
		
	}
	
	public Interaction(Set<Item> items) {
		super();
		int[] itemIds = new int[items.size()];
		int i = 0;
		for(Item item : items) {
			itemIds[i] = item.getiD();
			i++;
		}
		this.itemset = itemIds;
		this.items = items;
		this.support = 0;
	}
	
	public Interaction() {
		this.items = new HashSet<Item>();
		this.support = 0;
	}

	public Interaction(Itemset itemset) {
		this.items = new HashSet<Item>();
		for(int id : itemset.itemset) {
			this.items.add(Item.fromInt(id));
		}
		this.support = itemset.support;
		this.itemset = itemset.itemset;
	}
	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	
	
	public Set<Item> getInteractions() {
		return items;
	}

	/**
	 * Add an item to the list
	 * @param item The item to add
	 */
	public void addItem(Item item) {
		this.items.add(item);
		this.itemset = new int[this.items.size()];
		int i = 0;
		for(Item it : this.items) {
			this.itemset[i] = it.getiD();
			i++;
		}
	}
	
	public int size() {
		return this.items.size();
	}
	
	public boolean contains(Item item) {
		return this.items.contains(item);
	}
	/**
	 * Get an interaction with the list of items excluding the given one
	 * @param item The item to exclude
	 * @return The cloned interaction
	 */
	public Interaction cloneMinusOne(Item item) {
		Interaction newInteraction = new Interaction();
		for(Item i : this.items) {
			if(!i.equals(item)) {
				newInteraction.addItem(i);
			}
		}
		return newInteraction;
	}
	
	/**
	 * Get an Interaction with all of the items NOT contained in the given interaction
	 * @param interaction The interaction to exclude items from
	 * @return The cloned interaction
	 */
	public Interaction cloneMinusInteraction(Interaction interaction) {
		Interaction newInteraction = new Interaction();
		for(Item i : this.items) {
			if(!interaction.contains(i)) {
				newInteraction.addItem(i);
			}
		}
		return newInteraction;
	}
	
	@Override
	public String toString() {
		String str = "";
		List<Item> itemList = new ArrayList<Item>();
		itemList.addAll(this.getInteractions());
		Collections.sort(itemList);
		for(Item i : items) {
			str += i.getShortName() + " ";
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("#0.000");
		String formattedSupport = "" + this.getSupport();
		str += "Supp: " + formattedSupport;
		
		return str;
	}
	
	
	
	
}
