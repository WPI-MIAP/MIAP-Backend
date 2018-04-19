package org.maras.program;

import java.io.FileNotFoundException;

import org.maras.framework.Drug;
import org.maras.framework.Rule;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * This class is used to generate the Standardized Rules file.
 * @author Brian
 *
 */
public class StandardizeRules {

	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		String drugPath = "MARAS/data/test2.json";
		Drug.readDrugs(drugPath);
		System.out.println("Standardizing known rules...");
		Rule.standardizeAllKnownRulesThreading("D:/Documents/MQP/knownRules.txt");
		System.out.println("Complete!!!");
	}
}
