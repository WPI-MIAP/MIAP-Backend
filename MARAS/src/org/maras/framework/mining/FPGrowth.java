package org.maras.framework.mining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.maras.framework.Interaction;
import org.maras.framework.InteractionSets;
import org.maras.framework.Item;
import org.maras.framework.Report;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FPNode;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.FPTree;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.tools.MemoryLogger;

public class FPGrowth extends AlgoFPGrowth {
	/**
	 * TODO Refactor to move to extension class of original library.
	 * 
	 * @param database
	 * @param output
	 * @param minsupp
	 * @return
	 * @throws IOException
	 */
	public InteractionSets runAlgorithm(List<Report> database, double minsupp){
		startTimestamp = System.currentTimeMillis();
		itemsetCount = 0;
		// initialize tool to record memory usage
		MemoryLogger.getInstance().reset();
		MemoryLogger.getInstance().checkMemory();

		// (1) PREPROCESSING: Initial database scan to determine the frequency
		// of each item
		// The frequency is stored in a map:
		// key: item value: support
		final Map<Integer, Integer> mapSupport = scanDatabaseToDetermineFrequencyOfSingleItems(database);
		patterns = new Itemsets("FREQUENT ITEMSETS");
		// convert the minimum support as percentage to a
		// relative minimum support
		this.minSupportRelative = (int) minsupp;//(int) Math.ceil(minsupp * transactionCount);

		// (2) Scan the database again to build the initial FP-Tree
		// Before inserting a transaction in the FPTree, we sort the items
		// by descending order of support. We ignore items that
		// do not have the minimum support.
		FPTree tree = new FPTree();

		for (Report row : database) {
			List<Integer> transaction = new ArrayList<Integer>();
			for (Item item : row) {
				if (mapSupport.get(item.getiD()) >= minSupportRelative) {
					transaction.add(item.getiD());
				}
			}

			Collections.sort(transaction, new Comparator<Integer>() {
				public int compare(Integer item1, Integer item2) {
					// compare the frequency
					int compare = mapSupport.get(item2) - mapSupport.get(item1);
					// if the same frequency, we check the lexical ordering!
					if (compare == 0) {
						return (item1 - item2);
					}
					// otherwise, just use the frequency
					return compare;
				}
			});
			// add the sorted transaction to the fptree.
			tree.addTransaction(transaction);
		}

		// We create the header table for the tree using the calculated support
		// of single items
		tree.createHeaderList(mapSupport);

		// (5) We start to mine the FP-Tree by calling the recursive method.
		// Initially, the prefix alpha is empty.
		// if at least an item is frequent
		if (tree.getHeaderList().size() > 0) {
			// initialize the buffer for storing the current itemset
			itemsetBuffer = new int[BUFFERS_SIZE];
			// and another buffer
			fpNodeTempBuffer = new FPNode[BUFFERS_SIZE];
			// recursively generate frequent itemsets using the fp-tree
			// Note: we assume that the initial FP-Tree has more than one path
			// which should generally be the case.
			try {
				fpgrowth(tree, itemsetBuffer, 0, transactionCount, mapSupport);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		// record the execution end time
		endTime = System.currentTimeMillis();

		// check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// return the result (if saved to memory)
		return makeInteractionSets(patterns);
	}

	private InteractionSets makeInteractionSets(Itemsets patterns) {
		List<Interaction> interactions = new ArrayList<>();
		for(List<Itemset> level : patterns.getLevels())
		{
			for(Itemset itemset: level)
			{
				Set<Item> items = new TreeSet<>();
				for(int i: itemset.itemset)
				{
					items.add(Item.fromInt(i));
				}
				Interaction interaction = new Interaction(items,itemset.support);
				interactions.add(interaction);
				
			}
		}
		InteractionSets itemsets = new InteractionSets("Mined Interactions");
		for (Itemset i : interactions)
		{
			itemsets.addItemset(i, i.size());
		}
		return itemsets;
	}

	/**
	 * An Overload of the default scanDatabaseToDetermineFrequencyOfSingleItems to
	 * use an in-memory list of entries.
	 * 
	 * TODO: Refactor to move to extension class of original library.
	 * 
	 * @param database
	 * @return
	 */
	public Map<Integer, Integer> scanDatabaseToDetermineFrequencyOfSingleItems(List<Report> database) {
		Map<Integer, Integer> mapSupport = new HashMap<Integer, Integer>();
		for (Report row : database) {
			for (Item item : row) {
				Integer count = mapSupport.get(item.getiD());
				if (count == null) {
					mapSupport.put(item.getiD(), 1);
				} else {
					mapSupport.put(item.getiD(), ++count);
				}
			}
			// increase the transaction count
			transactionCount++;
		}
		return mapSupport;

	}
}
