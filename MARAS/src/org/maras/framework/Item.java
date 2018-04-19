package org.maras.framework;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.maras.program.App;

import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class defining the Item structure. It also contains a map for associating
 * Items with their unique IDs.
 * 
 * @author Andrew Schade
 *
 */
public class Item implements Comparable<Item> {
//	private static Map<String, ADR> adrMap = new HashMap<String, ADR>();
	private static MetaMapApi metaMapApi = new MetaMapApiImpl();
	private static Map<String, Item> adrFormattedMap = new HashMap<String, Item>();
	private static Object adrMapLock = new Object();
	private static Object itemMapLock = new Object();
	private static Object metaMapLock = new Object();
	private static Map<Integer, Item> itemMap = new HashMap<Integer, Item>();
	private static int nextID = 0;
	
	private List<String> reportIDs = new ArrayList<String>();
	private final boolean isDrug;
	private static int numADRs = 0;
	private static int metaMapCallCount = 0;
	
	private static Boolean metaMapInitialized = false;

	public static void setMetaMapInitialized(Boolean metaMapInitialized) {
		Item.metaMapInitialized = metaMapInitialized;

	}

	/**
	 * Method to relaunch the MetaMap API
	 */
	public static void restartMetaMap() {
		metaMapApi = new MetaMapApiImpl();
	}
	/**
	 * Finds the Item defined by the specified unique ID.
	 * 
	 * @param i
	 *            The unique ID query to find an Item.
	 * @return The item matching the unique ID query. If the ID is not matched a
	 *         NoSuchElementException will be thrown.
	 */
	public static Item fromInt(int i) {
//		for (Item item : itemMap) {
//			if (item.getiD() == i) {
//				return item;
//			}
//		}
		synchronized(itemMapLock) {
			if(itemMap.containsKey(i)){
				return itemMap.get(i);
			}
		}
		throw new java.util.NoSuchElementException();
	}
	
	/**
	 * Finds the Item defined by the specified unique ID. Doesn't have a lock because no
	 * new data is being added, only look-ups are happening.
	 * 
	 * @param i
	 *            The unique ID query to find an Item.
	 * @return The item matching the unique ID query. If the ID is not matched a
	 *         NoSuchElementException will be thrown.
	 */
	public static Item fromIntKnown(int i) {
		if(itemMap.containsKey(i)){
			return itemMap.get(i);
		}
		throw new java.util.NoSuchElementException();
	}

	/**
	 * Gets the item ID for a specified string.
	 * 
	 * @param name
	 *            the string name of the item. Will be matched ignoring case.
	 * @return The integer ID matched for the string. If multiple items have the
	 *         same name, the lowest ID match will be returned. A
	 *         NoSuchElementException will be thrown if there is no matched string.
	 */

	public static int itemID(String name, boolean isDrug, boolean standardized) {
		String formattedName;
		if(!isDrug && !standardized && metaMapInitialized) {
			return Item.checkADRName(name, false).getiD();
		}
		else if (isDrug)
		{
			int id = Drug.match(name).getiD();
			synchronized (itemMapLock) {
				if(itemMap.containsKey(id)) {
					return id;
				}
				else {
					throw new NoSuchElementException();
				}
			}
		}
		else {
			formattedName = name.toLowerCase().replaceAll("\\W", "");
		}
		synchronized (itemMapLock) {
			for (Item item : itemMap.values()) {
				if (item.getShortName().equals(formattedName) && isDrug == item.isDrug()) {
					return item.getiD();
				}
			}
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * Gets the item ID for a specified string. Doesn't have a lock because no
	 * new data is being added, only look-ups are happening.
	 * 
	 * @param name
	 *            the string name of the item. Will be matched ignoring case.
	 * @return The integer ID matched for the string. If multiple items have the
	 *         same name, the lowest ID match will be returned. A
	 *         NoSuchElementException will be thrown if there is no matched string.
	 */
	public static int itemIDKnown(String name, boolean isDrug, boolean standardized) {
		String formattedName;
		if(!isDrug) {
			if (adrFormattedMap.containsKey(name.toLowerCase().replaceAll("\\W", ""))) {
				return adrFormattedMap.get(name.toLowerCase().replaceAll("\\W", "")).getiD();
			}
			throw new NoSuchElementException();
		}
		else if (isDrug)
		{
			int id = Drug.match(name).getiD();
			if(itemMap.containsKey(id)) {
				return id;
			}
			else {
				throw new NoSuchElementException();
			}
		}
		else {
			formattedName = name.toLowerCase().replaceAll("\\W", "");
		}
		for (Item item : itemMap.values()) {
			if (item.getShortName().equals(formattedName) && isDrug == item.isDrug()) {
				return item.getiD();
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * This is the public constructor for creating an Item. It loads an item in from
	 * a name. This can be updated in the future to ensure ID consistency. If a name
	 * already maps to an item, then it doesn't create a new one.
	 * 
	 * @param name
	 *            The name of the drug or reaction, etc.
	 * @return A new item created, which is added to the data structure tracking all
	 *         items.
	 */
	public static Item makeItem(String name, boolean isDrug, boolean standardized) {
		String formattedName;
		if(!isDrug && !standardized && metaMapInitialized) {
			Item i = Item.checkADRName(name, true);
			return i;
		}
		else if (isDrug)
		{
			Item i = Drug.match(name);
			synchronized (itemMapLock) {
				itemMap.put(i.getiD(), i);	
			}
			return i;
		}
		else {
			formattedName = name.toLowerCase().replaceAll("\\W", "");
		}
		synchronized(itemMapLock) {
			try {
				return fromInt(itemID(formattedName, isDrug, standardized));
			} catch (NoSuchElementException e) {

				Item i = new Item(formattedName, name, nextID++, isDrug);
				
				itemMap.put(i.getiD(), i);
				if(!isDrug) {
					numADRs++;
				}
				return i;
			}
		}
	}
	
	/**
	 * Checks to see if an adr corresponds to an alternate name for a known adr. If so,
	 * returns the preferred name for the adr.
	 * @param name
	 * 			The unformatted name of an ADR	
	 * @param createNew
	 * 			Boolean flag indicating whether a new ADR item should be created if it does not already exist. If false,
	 * 			a NoSuchElementException is thrown.
	 * @return Preferred name for adr
	 */
	public static Item checkADRName(String name, boolean createNew) {
		synchronized (adrMapLock) {
			if (adrFormattedMap.containsKey(name.toLowerCase().replaceAll("\\W", ""))) {
				return adrFormattedMap.get(name.toLowerCase().replaceAll("\\W", ""));
			}
		}
		List<Result> resultList;
		synchronized (metaMapLock) {
			metaMapCallCount++;
			//System.out.println("MetaMap call " + metaMapCallCount);
			resultList = metaMapApi.processCitationsFromString(name);
		}
    	for (Result r : resultList) {
    		try {
				for (Utterance u : r.getUtteranceList()) {
					for (PCM pcm : u.getPCMList()) {
						for (Mapping mapping : pcm.getMappingList()) {
							String preferredNames = "";
							for (int i=0; i < mapping.getEvList().size(); i++) {
								preferredNames += mapping.getEvList().get(i).getPreferredName().toLowerCase().replaceAll("\\W", "");
								if (i < mapping.getEvList().size() - 1) {
									preferredNames += "_";
								}
							}
							if (preferredNames.length() > 0) {
								//check if already created
								try {
									Item item = fromInt(itemID(preferredNames, false, true));
									synchronized (adrMapLock) {
										adrFormattedMap.put(name.toLowerCase().replaceAll("\\W", ""), item);
									}
									return item;
								}
								catch(NoSuchElementException e) {
									//otherwise, create new object if flag is true
									if (createNew) {
										Item newItem;
								    	synchronized (itemMapLock) {
								    		newItem = new Item(preferredNames, name, nextID++, false);
								    		itemMap.put(newItem.getiD(), newItem);
								    		numADRs++;
								    	}
										synchronized (adrMapLock) {
											adrFormattedMap.put(name.toLowerCase().replaceAll("\\W", ""), newItem);
											return newItem;
										}
									}else {
										throw new NoSuchElementException();
									}
								}
							}
						}
					}
				}
			} catch (NoSuchElementException ne) {
				throw new NoSuchElementException();
			} catch (Exception e) {
				App.handleException(e);
			}
    	}
    	String formattedName = name.toLowerCase().replaceAll("\\W", "");
    	//check if already created
    	try{
    		Item item = fromInt(itemID(formattedName, false, true));
    		synchronized (adrMapLock) {
				adrFormattedMap.put(formattedName, item);
			}
    		return item;
    	}
    	catch(NoSuchElementException e) {
    		if(createNew) {
    	    	Item newItem;
    	    	synchronized (itemMapLock) {
    	    		newItem = new Item(formattedName, name, nextID++, false);
    	    		itemMap.put(newItem.getiD(), newItem);
    	    		numADRs++;
    	    	}
    	    	synchronized (adrMapLock) {
    	    		adrFormattedMap.put(name, newItem);
    	    	}
    			return newItem;
    		}
    		else {
    			throw new NoSuchElementException();
    		}
    	}
	}
	

	public void addReportID(String reportID) {
		this.reportIDs.add(reportID);
	}
	
	public List<String> getReportIDs(){
		return this.reportIDs;
	}
	

	/**
	 * Gets the number of distinct items that are drugs.
	 * 
	 * @return The number of distinct items that are drugs.
	 */

	public static int getNumDrugs() {
		return itemMap.size() - numADRs;
	}
	
	/**
	 * Gets the number of distinct items that are adr's.
	 * 
	 * @return The number of distinct items that are adr's.
	 */
	public static int getNumReactions() {
		return numADRs;
	}
	
//	/**
//	 * Reads in a mapping for standardizing various terms that refer to the same adr into one standardized term 
//	 * to help in mining and matching known rules.
//	 * 
//	 * @param fileName
//	 * 				The path of the file containing the mapping.
//	 */
//	public static void parseADRHierarchy(String fileName) {
//		Map<String, ADR> adrMap = new HashMap<String, ADR>();
//		InputStream fis = null;
//		BufferedReader br = null;
//		PrintWriter pw = null;
//		String line;
//		try {
//			fis = new FileInputStream(fileName);
//			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
//			pw = new PrintWriter("D:/Documents/MQP/adrHierarchy.txt", "UTF-8");
//			
//			while ((line = br.readLine()) != null) {
//				String[] row = line.split("\t");
////				String[] row = line.split(",");
//				ADR adr;
//				String termType = row[4];
//				termType = termType.toLowerCase();
//				
//				boolean adrInMap = adrMap.containsKey(row[1]);
//				if(adrInMap) {
//					adr = adrMap.get(row[1]);
//				}
//				else {
//					adr = new ADR();
//				}
//				
//				if(termType.equals("llt")){
//					adr.addLLT(row[6]);
//				}
//				else if(termType.equals("pt")) {
//					adr.addPT(row[6]);
//				}
//				
//				if(!adrInMap) {
//					adrMap.put(row[1], adr);
//				}
//			}
//			
//			System.out.println("Number of ADRs in hierarchy: " + adrMap.size());
//			
//			for(ADR adr : adrMap.values()) {
//				pw.println(adr.csvString());
//				for(String llt : adr.getLLT()) {
//					Item.adrFormattedMap.put(llt, adr.toString());
//				}
//				for(String pt : adr.getPT()) {
//					Item.adrFormattedMap.put(pt, adr.toString());
//				}
//			}
//		} catch (Exception e) {
//			System.err.println("Error reading known rules from " + fileName);
//			e.printStackTrace();
//		} finally {
//			try {
//				br.close();
//				fis.close();
//				pw.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * Gets the standardized version of the item's name. I.e. all spaces are removed and all letters are lower case.
	 * 
	 * return Standardized version of the item's name.
	 */
	@Override
	public String toString() {
		return this.getShortName();
	}

	private final int iD;

	protected final String shortName;
	protected final String longName;

	/**
	 * The private constructor for Item. This is pretty straightforward. It contains
	 * the name of the item and its unique ID.
	 * 
	 * @param name
	 *            The name of the drug or reaction.
	 * @param ID
	 *            The unique ID referring to the Item.
	 */
	protected Item(String name, String longName, int ID, boolean isDrug) {
		this.shortName = name;
		this.iD = ID;
		this.isDrug = isDrug;
		this.longName = longName;
	}

	/**
	 * A getter object to determine if this item is a drug or a reaction.
	 * 
	 * @return true if drug, false if reaction
	 */
	public boolean isDrug() {
		return isDrug;
	}

	/**
	 * A getter object to receive the unique ID of the Item.
	 * 
	 * @return the Item's unique ID.
	 */
	public int getiD() {
		return iD;
	}
	
	/**
	 * A getter object to receive the short name of the Item
	 * 
	 * @return the Item's name.
	 */
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * A getter object to receive the long name of the Item
	 * 
	 * @return the Item's name.
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * Used to determine if two items are the same by comparing their IDs.
	 * 
	 * @param arg0
	 * 				The item being compared with this item.
	 * @return Integer indicating that this item's ID comes before (-1), after (1), or is the same as (0) the other item's ID.
	 */
	@Override
	public int compareTo(Item arg0) {
		return arg0.iD < iD ? 1 : arg0.iD == iD ? 0 : -1;
	}
}
