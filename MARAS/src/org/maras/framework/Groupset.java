package org.maras.framework;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains a set of groups developed from a closed ruleset. It
 * provides an interface for creating output files in formats useful for DIVA
 * visualization and human consumption.
 * 
 * @author Andrew Schade
 *
 */
public class Groupset extends ArrayList<Group> {
	/**
	 * Get a string representing the results of analysis
	 * @return The string
	 */
	public String getResultString() {
		return getResultString(Integer.MAX_VALUE);
	}

	/**
	 * Get a string representing the results of analysis
	 * @param top Max number of results to consider
	 * @return The String
	 */
	public String getResultString(int top) {
		Collections.sort(this);
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (Group group : this) {
			counter++;
			if (counter > top) {
				break;
			}
			List<Item> drugs = group.getCompositeRule().getAnte();
			List<Item> reactions = group.getCompositeRule().getCons();
			sb.append("===Group " + counter + " === # of drug : ");
			sb.append(drugs.size() + " score : " + group.getScore() + "\n");
			for (Item drug : drugs) {
				sb.append("[" + drug.getLongName() + "] ");
			}
			sb.append("-> ");
			for (Item reaction : reactions) {

				sb.append("[" + reaction.getLongName() + "] ");
			}
			sb.append("| Supp = " + group.getCompositeRule().getAbsoluteSupport());
			sb.append(" , Conf = " + (group.getCompositeRule().getConfidence() * 100) + "%\n");
			ArrayList<HashSet<Rule>> singles = group.getSingletonRules();
			int size = drugs.size() + reactions.size();
			for (int i = singles.size() - 1; i > -1; i--) {
				for (Rule r : singles.get(i)) {
					if (r.getInteraction().size() != size) {
						size--;
						sb.append("*\n");
					}
					List<Item> singleDrugs = r.getAnte();
					List<Item> singleReactions = r.getCons();
					for (Item drug : singleDrugs) {
						sb.append("[" + drug.getLongName() + "] ");
					}
					sb.append("-> ");
					for (Item reaction : singleReactions) {

						sb.append("[" + reaction.getLongName() + "] ");
					}
					sb.append("| Supp = " + r.getAbsoluteSupport());
					sb.append(" , Conf = " + (r.getConfidence() * 100) + "%\n");
				}
			}
			sb.append("\n\n");
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return The visualization string
	 */
	public String getVizString() {
		return getVizString(Integer.MAX_VALUE);
	}

	/**
	 * Get the string used by the visualization to represent the data
	 * @param top The max number of results
	 * @return The visualization string
	 */
	public String getVizString(int top) {
		Collections.sort(this);
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (Group group : this) {
			counter++;
			if (counter > top) {
				break;
			}
			sb.append(group.getCompositeRule().getConfidence() * 100 + ","
					+ group.getCompositeRule().getAbsoluteSupport() + "," + group.getScore());

			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Visualization data file generation
	 * @return The string
	 */
	public String getVizLabelString() {
		return getVizLabelString(Integer.MAX_VALUE);
	}

	/**
	 * Visualization Data file Generation.
	 * 
	 * 
	 * @param top The max number of results to consider
	 * @return The string
	 */
	public String getVizLabelString(int top) {
		Collections.sort(this);
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		sb.append("[");
		for (Group group : this) {
			if (group.getCompositeRule().getAnte().size() > 2 || group.getCompositeRule().getCons().size() > 1) {
				continue;
			}
			counter++;
			if (counter > top) {
				break;
			}
			List<Item> drugList = group.getCompositeRule().getAnte();
			List<Item> reactionList = group.getCompositeRule().getCons();
			int size = drugList.size() + reactionList.size();
			StringBuilder drugs = new StringBuilder();
			for (Item drug : drugList) {
				drugs.append("[" + drug.getLongName() + "] ");
			}
			sb.append("{\"Rank\":\"" + counter + "\",\"Score\":\"" + group.getScore() +
					"\",\"ADR\":\"" + reactionList.get(0).getLongName() + "\",\"r_Drugname\":\"" + drugs +
					"\",\"Support\":\"" + group.getCompositeRule().getAbsoluteSupport() + "\",\"Confidence\":\"" +
					group.getCompositeRule().getConfidence() * 100 + "\",");

			ArrayList<HashSet<Rule>> singles = group.getSingletonRules();

			for (Rule r : singles.get(0)) {
				if (r.getInteraction().size() != size) {
					size--;
				}
				List<Item> singleDrugList = group.getCompositeRule().getAnte();
				
				for(int j = 0; j < 2; j++) {
					sb.append("\"Drug" + (j + 1) + "\":{\"id\":" + singleDrugList.get(j).getiD() + ",\"name\":\""
							+ singleDrugList.get(j).getLongName() + "\"},\"Support" + (j + 1) + "\":\"" + r.getAbsoluteSupport() + 
							"\",\"Confidence" + (j + 1) + "\":\"" + r.getConfidence() * 100 + "\",");
				}

			}
			sb.append("\"status\":\"");
			
			if(group.getCompositeRule().isKnown()) {
				sb.append("known");
			} else {
				sb.append("unknown");
			}
			sb.append("\",");
		
			sb.append("\"id\":");
			StringBuilder reportIDs = new StringBuilder();
			reportIDs.append("\"");
			
			/**
			 * To find a list of supporting reports, we check to find the list of Report IDs that are similar
			 * across all of the Items 
			 */
			List<String> matchingReportIDs = new ArrayList<String>();
			for(String reportID : group.getCompositeRule().getAnte().get(0).getReportIDs()) {
				boolean match = true;
				for(Item item : group.getCompositeRule().getAnte()) {
					if(!item.getReportIDs().contains(reportID)) {
						match = false;
						break;
					}
				}
				for(Item item : group.getCompositeRule().getCons()) {
					if(!item.getReportIDs().contains(reportID)) {
						match = false;
						break;
					}
				}
				if(match) {
					matchingReportIDs.add(reportID);
				}
			}

			for(String reportID : matchingReportIDs) {
				reportIDs.append(reportID + ",");
			}
			if(!matchingReportIDs.isEmpty()) {
				reportIDs.deleteCharAt(reportIDs.length() - 1); // Delete the extra comma at the end
			}
			reportIDs.append("\"");
			sb.append(reportIDs);
			sb.append("},");
		}
		sb.deleteCharAt(sb.length() - 1); // Delete the extra comma at the end
		sb.append("]");
		return sb.toString();
	}
	


	/**
	 * Write the DIVA rules
	 * @param outfile The output file to write to
	 * @throws IOException
	 */
	public void outputDIVARules(BufferedWriter outfile) throws IOException {
		outfile.write(this.getDIVARuleString());
	}

	/**
	 * Get the DIVA rule string
	 * @return The rule string
	 */
	private String getDIVARuleString() {
		return getDIVARuleString(Integer.MAX_VALUE);
	}

	/**
	 * Get the DIVA rule string
	 * @param top The max number of results to consider
	 * @return The rule string
	 */
	private String getDIVARuleString(int top) {
		Collections.sort(this);
		StringBuilder outputBuilder = new StringBuilder();

		outputBuilder.append(getHeaderRow() + '\n');

		int rank = 1;
		for (Group group : this) {
			if (group.getCompositeRule().getAnte().size() > 2 || group.getCompositeRule().getCons().size() > 1) {
				continue;
			}
			if (rank > top)
				break;
			List<Item> drugList = group.getCompositeRule().getAnte();
			List<Item> reactionList = group.getCompositeRule().getCons();
			int size = drugList.size() + reactionList.size();
			StringBuilder drugs = new StringBuilder();
			for (Item drug : drugList) {
				drugs.append("[" + drug.getLongName() + "] ");
			}
			String reaction = reactionList.get(0).getLongName();
			List<String> row = new ArrayList<>();
			row.add(new Integer(rank).toString());
			row.add(new Double(group.getScore()).toString());
			row.add(reaction);
			row.add(drugs.toString());
			row.add(new Integer(group.getCompositeRule().getAbsoluteSupport()).toString());
			row.add(new Double(group.getCompositeRule().getConfidence()).toString());
			for(Rule rule : group.getSingletonRules().get(0)) {
				row.add(rule.getAnte().get(0).getLongName());
				row.add(new Integer(rule.getAbsoluteSupport()).toString());
				row.add(new Double(rule.getConfidence()).toString());
			}
			if (group.getCompositeRule().isKnown()) {
				row.add("known");
			}
			else {
				row.add("unknown");
			}
			row.add("\"\"");
			outputBuilder.append(row.stream().collect(Collectors.joining(",")));
			outputBuilder.append("\n");
			rank++;
		}

		return outputBuilder.toString();
	}

	/**
	 * Get the header string for the output
	 * @return The header string
	 */
	private String getHeaderRow() {
		List<String> headerRow = Arrays.asList("Rank", "Score", "ADR", "r_drugName", "Support", "Confidence", "Drug1",
				"Support1", "Confidence1", "Drug2", "Support2", "Confidence2", "status", "id");
		return headerRow.stream().collect(Collectors.joining(","));
	}
	
	/**
	 * Write the results to file
	 * @param outfile The file to write to
	 * @throws IOException
	 */
	public void outputGroups(BufferedWriter outfile) throws IOException {
		outfile.write(this.getResultString());
	}

	/**
	 * Write the visualization strings to file
	 * @param labels The label file to write to
	 * @param info The info file to write to
	 * @throws IOException
	 */
	public void outputViz(BufferedWriter labels, BufferedWriter info) throws IOException {
		labels.write(this.getVizLabelString());
		info.write(this.getVizString());
	}
	
	/**
	 * Write the visualization string to file
	 * @param outfile The file to write to
	 * @throws IOException
	 */
	public void outputViz(BufferedWriter outfile) throws IOException {
		outfile.write(this.getVizLabelString());
	}
}
