/**
 * 
 */
package org.maras.testing;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.maras.framework.Interaction;
import org.maras.framework.InteractionSets;
import org.maras.framework.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * 
 * 
 * Reads itemsets from a file and stores them in a map so they can be easily matched.
 * 
 * @author Brian Zylich
 */
public class ItemsetFile {
    public static final int FORMAT_SPACE_SEPARATED = 0;
    public static final int FORMAT_SPMF = 1;
    
    private Map<String, String> ade_map;
	private Map<String, String> drug_map;
    
    private String path;
    private int format;
    private InteractionSets itemsets;

    /**
     *
     */
    public ItemsetFile(String path, int format, String ade_map_path, String drug_map_path) {
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
        itemsets = new InteractionSets("Frequent Itemsets");
        try {
            readFile();
        }
        catch (IOException e){
            System.err.println("Error reading itemset file: " + path);
        }
    }
    
    /**
     * Helper function to read in the maps for drugs and reactions in order to allow us to
     * compare the itemsets held in memory to itemsets in a file even if the mapping is different.
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

        Set<Item> items;
        Interaction itemset;

        String line;
        String[] str;

        while ((line = br.readLine()) != null) {
            switch(format) {
                case FORMAT_SPACE_SEPARATED:
                    str = line.split(" ");
                    items = new HashSet<Item>();
                    if (str.length > 1) {
                        for (int i = 0; i < str.length - 1; i++) {
                            items.add(Item.fromInt(Item.itemID(getNameFromMap(str[i]), isDrug(str[i]), false)));
                        }
                        int support = Integer.valueOf(str[str.length - 1].replaceAll("[()]", ""));
                        itemset = new Interaction(items);
                        itemset.setSupport(support);
                        itemsets.addItemset(itemset, str.length - 1);
                    }
                    break;
                case FORMAT_SPMF:
                    str = line.split(" ");
                    items = new HashSet<Item>();
                    if (str.length > 2) {
                        for (int i = 0; i < str.length-2; i++) {
                        	items.add(Item.fromInt(Item.itemID(getNameFromMap(str[i]), isDrug(str[i]), false)));
                        }
                        int support = Integer.valueOf(str[str.length - 1]);
                        itemset = new Interaction(items);
                        itemset.setSupport(support);
                        itemsets.addItemset(itemset, str.length - 2);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    
    private boolean isDrug(String id) {
    	if(ade_map.containsKey(id)) {
    		return false;
    	}
    	else if(drug_map.containsKey(id)) {
    		return true;
    	}
    	
    	//not found
    	System.err.println("(Itemset) Item not found in map!");
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
    	System.err.println("(Itemset) Item not found in map!");
    	return null;
    }

    public InteractionSets getItemsets() {
        return itemsets;
    }

    public String getPath(){
        return path;
    }

}