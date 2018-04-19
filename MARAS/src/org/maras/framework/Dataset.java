package org.maras.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Collections;

import org.maras.framework.mining.Agrawal;
import org.maras.framework.mining.FPGrowth;
import org.maras.program.App;

/**
 * The class defining the Dataset structure. A Dataset is a collection of
 * {@link Report}s where each report is generated from a single Adverse Reaction
 * FARAS report.
 *
 * @author Andrew Schade
 *
 */
public class Dataset {
	private final List<Report> reportSet;
	private final int maxTransactionLength;
	private final double minSupport;
	private final double minConfidence;
	private InteractionSets interactions;
	private RuleSets associations;

	/**
	 * Reads in a dataset from a file.
	 *
	 * @param drugFiles
	 * @param path
	 *            The location of the file containing the dataset data.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public Dataset(List<File> reportFiles, int maxTransactionLength, double minSupport, double minConfidence, File reportOutput)
			throws FileNotFoundException, UnsupportedEncodingException {
		this.maxTransactionLength = maxTransactionLength;
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.reportSet = new ArrayList<>();
		String line;
		try {
			System.out.println("Parsing files");
			long drugTime = 0;
			long adrTime = 0;
			int adrCount = 0;
			for (File reportFile : reportFiles) {
				FileInputStream fis = new FileInputStream(reportFile.getAbsolutePath());
				BufferedReader reportbr = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
				int lineCount = 0;
				System.out.println("Parsing reports...");
				reportbr.readLine();
				while ((line = reportbr.readLine()) != null) {
					String[] tokenizedLine = line.split("->");
					String id = tokenizedLine[0];
					String[] drugList = tokenizedLine[1].split(",");
					String[] reactionList = tokenizedLine[2].split(",");
					if (drugList.length + reactionList.length <= this.maxTransactionLength) {
						// Parse each drug
						lineCount++;
						Set<Drug> reportDrugs = new HashSet<>();
						Set<Item> reportReactions = new HashSet<>();
						long startTime = System.currentTimeMillis();
						for (String drugName : drugList) {
							Drug drug = (Drug)Item.makeItem(drugName, true, false);
							drug.addReportID(id);
							reportDrugs.add(drug);
						}
						long endTime = System.currentTimeMillis() - startTime;
						drugTime += endTime;
						startTime = System.currentTimeMillis();
						for (String reactionName : reactionList) {
							adrCount++;
							//System.out.println("ADR Count: " + adrCount);
//							if(adrCount > 2000) {
//								System.out.println("Resetting MetaMap");
//								adrCount = 0;
//								App.relaunchMetaMap();
//							}
							Item reaction = Item.makeItem(reactionName, false, false);
							reaction.addReportID(id);
							reportReactions.add(reaction);
						}
						endTime = System.currentTimeMillis() - startTime;
						adrTime += endTime;
						Report r = new Report(reportDrugs, reportReactions, id);
						reportSet.add(r);
					}
				}
				reportbr.close();
				fis.close();
			}

			System.out.println("Found " + reportSet.size() + " reports");
			System.out.println("Successfully finished parsing the data");
			System.out.println("Time taken to match drug names: " + drugTime + " milliseconds.");
			System.out.println("Time taken to standardize ADR names: " + adrTime + " milliseconds.");
		} catch (IOException e) {
			App.handleException(e);
		}
	}

	/**
	 * Generates itemsets after checking if the itemsets are already generated, if
	 * they are it will return the generated itemests.
	 * 
	 * @return The {@link InteractionSets} mined with {@link FPGrowth}.
	 */
	public InteractionSets getInteractions() {
		if (this.interactions == null) {
			FPGrowth fpgrowth = new FPGrowth();
			InteractionSets interactions = fpgrowth.runAlgorithm(reportSet, minSupport);
			this.interactions = interactions;
		}
		return this.interactions;
	}

	/**
	 * Generates rulesets after checking to see if the rulesets are already
	 * generated. If they are, it will return the generated rulesets. Also will
	 * generate itemsets if those are not already generated.
	 * 
	 * @return The {@link RuleSets} mined with {@link Agrawal}.
	 */
	public RuleSets getAssociationRules() {
		if (this.associations == null) {
			Agrawal agrawal = new Agrawal();
			RuleSets rules = agrawal.runAlgorithm(this.getInteractions(), getDatasetSize(), this.minConfidence);
			this.associations = rules;
		}
		return this.associations;
	}

	/**
	 * Gets the number of reports in the database.
	 * 
	 * @return The size of the database defined by the number of reports.
	 */
	public int getDatasetSize() {
		return reportSet.size();
	}
}
