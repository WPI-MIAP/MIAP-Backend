/**
 * 
 */
package org.maras.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.maras.framework.edit.DamerauLevenshtein;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * Class representing a Drug 
 * This class maintains a static list of all drugs used in the system
 * @author Andrew Schade
 *
 */
public class Drug extends Item {

	private static ArrayList<Drug> drugs = new ArrayList<>();
	private static Object mapLock = new Object();
	private static Object cacheLock = new Object();
	public static HashMap<String, Drug> cache = new HashMap<String, Drug>();

	/**
	 * Private Constructor, just initializes drug list.
	 */
	protected Drug(int ID, String shortName, String fullName) {
		super(shortName, fullName, ID, true);
	}

	/**
	 * Read drugs from the drug map file to initialize drugs with their IDs
	 * @param path The path to the drug file
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public static void readDrugs(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Type listType = new TypeToken<ArrayList<Drug>>() {
		}.getType();
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Drug.class, new JsonDeserializer<Drug>() {

			@Override
			public Drug deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				final JsonObject jsonObject = json.getAsJsonObject();
				final String shortName = jsonObject.get("short_name").getAsString();
				final String fullName = jsonObject.get("full_name").getAsString();
				final int iD = jsonObject.get("ID").getAsInt();
				return new Drug(iD, shortName, fullName);
			}
		});
		Gson g = builder.create();
		ArrayList<Drug> drugs = g.fromJson(new FileReader(new File(path)), listType);
		Drug.drugs = drugs;
		System.out.printf("%d drugs read in", drugs.size());
	}

	private static String[] wordsToRemove = { "pill", "oral", "tablet", "injection", "intravenus", "capsule",
			"intravenous", "once-daily", "once", "daily", "twice", " mg", ".", "%", " ml", "/mg", "/ml", "lotion",
			"ointment", "topical", "vaginal", "solution", "soap", "liquid", "injectable", "per", "menstrual", "pain",
			"as ", "chewable", "license", "holder", "unspecified", " in ", "coated", "with", "cream", "intramuscular",
			"powder", "for ", "ophthalmic", "extended", "release", " gm", "/gm", "gram", "milligram", "#", " g ", "/g ",
			"film", "gelatine", "gelatin", "gelat", "subcutaneous", "cutaneous", "drug", "implant", "childrens", " kit",
			"gel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	/**
	 * Process a drug name to remove any key phrases or words contained in the wordsToRemove list
	 * @param name The drug name
	 * @return The processed drug name
	 */
	private static String process(String name) {
		for (String pattern : wordsToRemove) {
			while (name.indexOf(pattern) != -1) {
				name = name.substring(0, name.indexOf(pattern))
						+ name.substring(name.indexOf(pattern) + pattern.length(), name.length());
			}
		}
		return name;
	}
	private static int matchesParsed = 0;
	/**
	 * Matches a drug name to a pre-existing drug object
	 * @param name The name of the drug
	 * @return The drug object
	 */
	public static Drug match(String name) {
		double minDistance = Double.MAX_VALUE;
		Drug minDrug = drugs.get(0);
		name = process(name.toLowerCase());
		DamerauLevenshtein dL = new DamerauLevenshtein(10, 6, 20, 9);
		synchronized(cacheLock) {
			if (!cache.containsKey(name)) {
				for (Drug d : drugs) {
					double currDistance = dL.execute(name, d.getShortName());
					if (currDistance < minDistance) {
						minDistance = currDistance;
						minDrug = d;
					}
				}
				cache.put(name, minDrug);
				synchronized (mapLock) {
					map.append("\"" + name + "\", \"" + minDrug.shortName + "\", " + minDistance + ";\n");	
				}
			}
			else {
				minDrug = cache.get(name);
				minDistance = dL.execute(name, minDrug.getShortName());
				//TODO Save Cache!!!!!!!!!!!!!
			}
		}
		matchesParsed++;
//		if (matchesParsed % 10000 == 0)
//		{
//			System.out.println(matchesParsed + " Drug Matches Parsed");
//		}
		
		return minDrug;
	}

	public static StringBuilder map = new StringBuilder();

}
