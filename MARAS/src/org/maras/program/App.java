package org.maras.program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.SSLEngineResult.Status;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import org.maras.framework.Dataset;
import org.maras.framework.Drug;
import org.maras.framework.Group;
import org.maras.framework.Groupset;
import org.maras.framework.InteractionSets;
import org.maras.framework.Item;
//import org.maras.testing.GroupTester;
//import org.maras.testing.ItemsetFile;
//import org.maras.testing.ItemsetTester;
//import org.maras.testing.RuleTester;
//import org.maras.testing.RulesFile;
//import org.maras.testing.Tester;
import org.maras.framework.MarasStatus;
import org.maras.framework.Rule;
import org.maras.framework.RuleSets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;	

/**
 * Main Application class for the MARAS system.
 * This class handles arguments, input data parsing, and output formatting. 
 * @author Brian
 *
 */
public class App
{

    private static final String defaultDrugPath = "data/test2.json";
    private static final String defaultOutPath = "results/";
    private static final String defaultStandardizedKnownRulesPath = "data/knownRules_standardized.csv";
    private static final String defaultPath = "data/ZipFiles/faers_ascii_2013q1.zip";
    private static final String defaultReportPath = "storage/reports.csv";
    private static final String defaultStatusPath = "storage/status.csv";
    private final List<String> path;
    private final String outPath;
    private final String drugPath;
    private final String standardizedKnownRulesPath;
    private static String metaMapPath = "";
    private final String reportPath;
    private static String statusPath = "";
    private static final int defaultSupport = 15;
    private static final double defaultConfidence = 0.0;
    private final int minSupport;
    private final double minConfidence;
    private Boolean metaMapInitialized;
    private static List<Process> metaMapProcesses = null;

    /**
     * Main function to handle parameters
     * @param args The arguments passed
     */
    public static void main(String[] args)
    {
        List<String> faersFiles = new ArrayList<String>();
        String statusPath = "";
        String outPath = "";
        String drugPath = "";
        String standardizedKnownRulesPath = "";
        String reportPath = "";
        Integer minSupport = null;
        Double minConfidence = null;
        Boolean metaMap = true;
        

        for(int i = 0; i < args.length; i++) {
        	switch(args[i]) {
        	case("-f"):
        		faersFiles.add(args[i+1]);
        		break;
        	case("-d"):
        		if(drugPath == "") {
        			drugPath = args[i+1];
        		}
        		break;
        	case("-r"):
        		if(standardizedKnownRulesPath == "") {
        			standardizedKnownRulesPath = args[i+1];
        		}
        		break;
        	case("-m"):
        		if(metaMapPath == "") {
        			metaMapPath = args[i + 1];
        		}
        		break;
        	case("-s"):
        		if(minSupport == null) {
        			minSupport = Integer.parseInt(args[i + 1]);
        		}
        		break;
        	case("-c"):
        		if(minConfidence == null) {
        			minConfidence = Double.parseDouble(args[i + 1]);
        		}
        		break;
        	case("-o"):
        		if(outPath == "") {
        			outPath = args[i + 1];
        		}
        	break;
        	case("-p"):
        		if(reportPath == "") {
        			reportPath = args[i + 1];
        		}
        	case("-t"):
        		if(statusPath == ""){
        			statusPath = args[i + 1];
        		}
        	}
        }
        if(statusPath == "") {
        	statusPath = defaultStatusPath;
        }
        if(reportPath == "") {
        	reportPath = defaultReportPath;
        }
        if(outPath == "") {
        	outPath = defaultOutPath;
        }
        if(faersFiles.isEmpty()) {
        	faersFiles.add(defaultPath); // TODO: Consider adding all of the .zip files in the data/ZipFiles directory
        }
        if(drugPath == "") {
        	drugPath = defaultDrugPath;
        }
        if(standardizedKnownRulesPath == "") {
        	standardizedKnownRulesPath = defaultStandardizedKnownRulesPath;
        }
        if(minSupport == null) {
        	minSupport = defaultSupport;
        }
        if(minConfidence == null) {
        	minConfidence = defaultConfidence;
        }
        if(metaMapPath == "") {
        	metaMap = false;
        }
        App app = new App(faersFiles, drugPath, standardizedKnownRulesPath, outPath, minSupport, minConfidence, metaMap, reportPath, statusPath);
        app.run();
        System.out.println("Closing metamap processes");
        
    }
    
   
    public App(List<String> path, String drugPath, String standardizedKnownRulesPath, String outPath, int minSupport, double minConfidence, Boolean metaMapInitialized, String reportPath, String statusPath)
    {
        this.path = path;
        this.outPath = outPath;
        this.minSupport = minSupport;
        this.minConfidence = minConfidence;
        this.drugPath = drugPath;
        this.standardizedKnownRulesPath = standardizedKnownRulesPath;
        this.metaMapInitialized = metaMapInitialized;
        this.reportPath = reportPath;
        this.statusPath = statusPath;
    }
    /**
     * Begin MARAS execution
     */
    public void run()
    {
        Dataset dataset;
        List<String> quarters = new ArrayList<String>();
//        List<Tester> tests = new ArrayList<Tester>();
//        int numSuccessfulTests = 0;
        try
        {  
        	updateStatus("status", "inprogress");
        	updateStatus("message", "MARAS analysis is currently running");
        	updateStatus("updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	// Unzip the files
			File dataDir = new File("data");
			System.out.println("Data directory:  " + dataDir.getAbsolutePath());
			Drug.readDrugs(drugPath);
			FileInputStream fis;
			byte[] buffer = new byte[1024];
			ArrayList<File> unzippedFiles = new ArrayList<File>();
			long startTime, totalTime;
			startTime = System.currentTimeMillis();
			// Extract zip files to data directory
			for(String path : this.path) {
				fis = new FileInputStream(path);
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry entry = zis.getNextEntry();
				while (entry != null) {
					String fileName = entry.getName();
					if (entry.isDirectory()) {
						entry = zis.getNextEntry();
						continue;
					}
					if ((fileName.contains("DRUG") || fileName.contains("REAC") || fileName.contains("DEMO"))
							&& fileName.contains(".txt")) {
						fileName = new File(fileName).getName();
						String shortenedName = fileName.replace("DRUG", "").replace("REAC", "").replaceAll("DEMO", "").replace(".txt", "");
						if(!quarters.contains(shortenedName)) {
							quarters.add(shortenedName);
						}
						File newFile = new File(dataDir + File.separator + "raw" + File.separator + fileName);
						if (newFile.exists()) {
							// This file already exists in the data directory
							System.out.println("File " + fileName + " already exists in the directory " + dataDir);
							entry = zis.getNextEntry();
							continue;
						}
						new File(newFile.getParent()).mkdirs(); // What's the effect of this line?
						FileOutputStream fos = new FileOutputStream(newFile);
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						unzippedFiles.add(newFile);
						fos.close();

					}
					entry = zis.getNextEntry();

				}
				zis.close();
			}

			if (unzippedFiles.isEmpty()) {
				System.out.println("No new files found. Exiting now.");
				updateStatus("status", "completed");
				updateStatus("updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				System.exit(0);
			}
			// We have the new files extracted. Now let's iterate through them and create
			// the output file
			this.parseFaersFiles(unzippedFiles, dataDir);
			// Iterate through the files in the data directory, getting a list of drug,
			// reaction, and demo files
			File[] allDataFiles = dataDir.listFiles();
			List<File> reportFiles = new ArrayList<File>();
			for (File file : allDataFiles) {
				System.out.println("Data file found with name : " + file.getAbsolutePath());
				if (file.getName().contains("Reports.txt")) {
					reportFiles.add(file);
				}
			}
			totalTime = System.currentTimeMillis() - startTime;
			System.out.println("Found " + reportFiles.size() + " report files");
			System.out.println("Time to extract and parse raw FAERS data: " + totalTime + " milliseconds");
			
			Item.setMetaMapInitialized(metaMapInitialized);
        	System.out.println("Reading in reports...");
        	File reportOutput = new File(this.reportPath);
            dataset = new Dataset(reportFiles, 15, minSupport, minConfidence, reportOutput);
            
            BufferedWriter drugMapFile = new BufferedWriter(new FileWriter(outPath + "drug_map.txt"));
            drugMapFile.write(Drug.map.toString());
            drugMapFile.close();
            
            System.out.println("The # of drugs is " + Item.getNumDrugs());
            System.out.println("The # of ade is " + Item.getNumReactions());
            System.out.println("Parsing known rules...");
            startTime = System.currentTimeMillis();
            Rule.parseStandardizedKnownRulesThreading(standardizedKnownRulesPath);
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to parse known rules: " + totalTime);
            System.out.println("Mining frequent itemsets...");
            startTime = System.currentTimeMillis();
            InteractionSets itemsets = dataset.getInteractions();
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Number of itemsets: " + itemsets.getItemsetsCount());
            System.out.println("Time to mine frequent itemsets: " + totalTime + " milliseconds");
            System.out.println("Mining association rules...");
            startTime = System.currentTimeMillis();
            RuleSets rules = dataset.getAssociationRules();
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Number of rules: " + rules.getRulesCount());
            System.out.println("Time to mine association rules: " + totalTime + " milliseconds");
            System.out.println("Finding itemset closures...");
            startTime = System.currentTimeMillis();
            InteractionSets closedItemsets = itemsets.getClosures();
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to find itemset closures: " + totalTime + " milliseconds");
            
            System.out.println("Finding filtered and closed rules...");
            startTime = System.currentTimeMillis();
            RuleSets filteredRules = rules.filterRules();
            RuleSets closedRules = filteredRules.filterNoSingletonRules().findClosures(closedItemsets);
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to find filtered and closed rules: " + totalTime + " milliseconds");
            
            System.out.println("Generating groups...");
            Groupset groups = new Groupset();
            startTime = System.currentTimeMillis();
            for (AssocRule rule : closedRules.getRules())
            {
            	((Rule) rule).checkKnown();
                Group group = ((Rule) rule).getGroup(filteredRules);
                group.calcScore();
                groups.add(group);
            }
            totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Time to generate groups: " + totalTime + " milliseconds");         
            System.out.println("Writing results to file...");
            File outPathFile = new File(outPath);
            outPathFile.mkdirs();
            File closedResults = new File(outPath, "closed_results.txt");
            File rulesCSV = new File(outPath, "rules.csv");
            File rulesLongNames = new File(outPath, "_rules_drug_adr_matching_long_names.txt");
            closedResults.createNewFile();
            rulesCSV.createNewFile();
            rulesLongNames.createNewFile();
            BufferedWriter resultsFile = new BufferedWriter(new FileWriter(closedResults));

            BufferedWriter vizFile = new BufferedWriter(new FileWriter(rulesCSV));
            BufferedWriter rulesFile = new BufferedWriter(new FileWriter(rulesLongNames));

			groups.outputGroups(resultsFile);
			groups.outputDIVARules(rulesFile);
			groups.outputViz(vizFile);
			resultsFile.close();
			rulesFile.close();
			vizFile.close();
			System.out.println("MARAS analysis completed successfully!");
			updateStatus("status", "completed");
			updateStatus("updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			updateStatus("message", "MARAS analysis completed successfully!");
			for(String quarterName : quarters) {
				updateStatus("sources", quarterName);
			}

			// test that groups generated are correct
			// String correctGroupsPath =
			// "D:/Documents/MQP/diva-cpp/diva-mqp/data/results/result_closed.txt";
			// String testGroupsPath =
			// "D:/Documents/MQP/diva-maras/outputclosed_results.txt";
			// GroupTester groupTester = new GroupTester(testGroupsPath, correctGroupsPath);
			// tests.add(groupTester);

			// run all tests
			// for(Tester t: tests) {
			// t.runTest();
			// if(t.getSuccess()) {
			// numSuccessfulTests++;
			// }
			// t.printAnalysis();
			// }
			// System.out.println("" + tests.size() + " test(s) completed. " +
			// numSuccessfulTests + " test(s) were successful.");
			// System.out.println("Test success rate: " + ((double) numSuccessfulTests /
			// tests.size()));
		} catch (Exception e) {
			App.handleException(e);
		}
	}
    
    /**
     * Static function to handle an exception and gracefully exit the execution.
     * This function updates the status file to properly include the error message.
     * It also prints out the error details to Standard Output to ensure it is properly written
     * to the log.txt file.
     * This function should be called any time an Exception is caught that should kill the 
     * program.
     * @param e The Exception thrown
     */
    public static void handleException(Exception e) {
    	StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		e.printStackTrace();
		System.out.println(stringWriter.toString());
		updateStatus("status", "error");
		updateStatus("message", e.getMessage());
		updateStatus("updated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.exit(1);
    }

	/**
	 * Parse a list of FAERS data files, returning an output file organizing each
	 * report
	 * 
	 * @param files
	 *            The FAERS data files (DRUG, REAC, and DEMO)
	 * @return The output file
	 * @throws IOException
	 */
	private File parseFaersFiles(List<File> files, File outputDir) throws IOException {

		// of the drugs and Reactions?
		TreeMap<String, Set<String>> reportDrugs = new TreeMap<String, Set<String>>(); // Maps the ID of a report to its
																						// drugs
		TreeMap<String, Set<String>> reportReactions = new TreeMap<String, Set<String>>(); // Maps the ID of a report to
																							// its
																							// reactions
		TreeMap<String, String> reportDemos = new TreeMap<String, String>(); // Maps the ID of a report to its Demo
		TreeMap<String, JsonObject> reportJson = new TreeMap<String, JsonObject>(); // JSON objects corresponding to report objects
		String line;
		String reportQuarter = "";
		for (File file : files) {
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			if (file.getName().contains("DRUG")) {
				System.out.println("Parsing drug file " + file.getAbsolutePath());
				// Drug file
				reportQuarter = file.getName().replace("DRUG", "").replace(".txt", "");
				br.readLine();
				while ((line = br.readLine()) != null) {
					String[] tokenizedLine = line.split("\\$");
					// System.out.println("Line: " + line);
					// System.out.println("Number of tokens in drug txt line: " +
					// tokenizedLine.length);
					String id = tokenizedLine[0];
					String drugName = tokenizedLine[4];
					if (reportDrugs.containsKey(id)) {
						reportDrugs.get(id).add(drugName);
					} else {
						Set<String> drugs = new HashSet<String>();
						// ArrayList<Item>[] items = new ArrayList<Item>[2];
						drugs.add(drugName);
						reportDrugs.put(id, drugs);
					}
				}
			} else if (file.getName().contains("REAC")) {
				// Reaction file
				System.out.println("Parsing reaction file " + file.getAbsolutePath());
				while ((line = br.readLine()) != null) {
					String[] tokenizedLine = line.split("\\$");
					// System.out.println("Line: " + line);
					// System.out.println("Number of tokens in drug txt line: " +
					// tokenizedLine.length);
					String id = tokenizedLine[0];
					String reactionName = tokenizedLine[2];
					if (reportReactions.containsKey(id)) {
						reportReactions.get(id).add(reactionName);
					} else {
						Set<String> reactions = new HashSet<String>();
						// ArrayList<Item>[] items = new ArrayList<Item>[2];
						reactions.add(reactionName);
						reportReactions.put(id, reactions);
					}
				}
			} else if (file.getName().contains("DEMO")) {
				System.out.println("Parsing demo file " + file.getAbsolutePath());
				while ((line = br.readLine()) != null) {
					String[] tokenizedLine = line.split("\\$");
					String id = tokenizedLine[0];
					String reptCode = tokenizedLine[8];
					reportDemos.put(id, reptCode);
					if(!reportJson.containsKey(id)) {
						JsonObject json = new JsonObject();
						json.addProperty("primaryId", id);
						json.addProperty("i_f_code", tokenizedLine[3]);
						json.addProperty("event_dt", tokenizedLine[4]);
						json.addProperty("mfr_dt", tokenizedLine[5]);
						json.addProperty("init_fda_dt", tokenizedLine[6]);
						json.addProperty("fda_dt", tokenizedLine[7]);
						json.addProperty("rept_cod", reptCode);
						json.addProperty("auth_num", "NULL");
						json.addProperty("mfr_num", tokenizedLine[9]);
						json.addProperty("mfr_sndr", tokenizedLine[10]);
						json.addProperty("lit_ref", "NULL");
						json.addProperty("age", tokenizedLine[11]);
						json.addProperty("age_cod", tokenizedLine[12]);
						json.addProperty("age_grp", "NULL");
						json.addProperty("sex", tokenizedLine[13]);
						json.addProperty("e_sub", tokenizedLine[14]);
						json.addProperty("wt", tokenizedLine[15]);
						json.addProperty("wt_cod", tokenizedLine[16]);
						json.addProperty("rept_dt", tokenizedLine[17]);
						json.addProperty("to_mfr", tokenizedLine[18]);
						json.addProperty("occp_cod", tokenizedLine[19]);
						json.addProperty("reporter_country", tokenizedLine[20]);
						json.addProperty("occr_country", "NULL");
						reportJson.put(id, json);
					}
					
				}
			}
			
			br.close();
		}
		String outputPath = outputDir.getAbsolutePath() + File.separator + reportQuarter + "Reports.txt";
		File outputFile = new File(outputPath);
		FileOutputStream fos = new FileOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		System.out.println("Writing output to " + outputPath);
		for (String id : reportDrugs.keySet()) {
			if (reportDemos.get(id).equals("EXP")) {
				String reportLine = "";
				JsonObject json = reportJson.get(id);
				String drugCollection = "";
				String reactionCollection = "";
				String updatedDrugCollection = "";
				String updatedReactionCollection = "";
				String matchedDrugCollection = "";
				reportLine += id + "->";
				for (String drug : reportDrugs.get(id)) {
					reportLine += drug + ",";
					drugCollection += drug + ", ";
					updatedDrugCollection += drug.replace(" ", "").replace("-", "") + ", ";
					matchedDrugCollection += Drug.match(drug).getLongName() + ", ";
				}
				reportLine += "->";
				for (String reaction : reportReactions.get(id)) {
					reportLine += reaction + ",";
					reactionCollection += reaction + ", ";
					updatedReactionCollection += reaction.replace(" ", "").replace("-", "") + ", ";
				}
				// Remove the final extra space and comma
				drugCollection = drugCollection.substring(0, drugCollection.length() - 2);
				reactionCollection = reactionCollection.substring(0, reactionCollection.length() - 2);
				updatedDrugCollection = updatedDrugCollection.substring(0, updatedDrugCollection.length() - 2);
				updatedReactionCollection = updatedReactionCollection.substring(0, updatedReactionCollection.length() - 2);
				matchedDrugCollection = matchedDrugCollection.substring(0, matchedDrugCollection.length() - 2);
				json.addProperty("drugname", drugCollection);
				json.addProperty("SideEffect", reactionCollection);
				json.addProperty("outc_cod", "NULL");
				json.addProperty("drugname_updated", updatedDrugCollection);
				json.addProperty("sideEffect_updated", updatedReactionCollection);
				json.addProperty("drugname_matched", matchedDrugCollection);
				
				
			
				bw.write(reportLine);
				bw.newLine();
//				System.out.println(reportLine);
			}
			

		}
		File reportFile = new File(this.reportPath);
		File tempReportFile = new File(this.reportPath + "_temp");
		BufferedWriter tempReportWriter = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(tempReportFile)));
		int lineNumber = 0;
		if(reportFile.exists()) {
			Scanner reportScanner = new Scanner(reportFile);
			while(reportScanner.hasNextLine()) {
				String reportLine = reportScanner.nextLine();
				// Don't write the last closing bracket
				if(reportScanner.hasNextLine()) {
					tempReportWriter.write(reportLine);
					lineNumber++;
					tempReportWriter.newLine();
				}
			}
			reportScanner.close();
		} else {
			tempReportWriter.write("[");
			tempReportWriter.newLine();
			lineNumber++;
		}
		for(String id : reportJson.keySet()) {
			if(reportJson.get(id).get("rept_cod").toString().contains("EXP")) {
				if(lineNumber > 1) {
					tempReportWriter.write(",");
				}
				tempReportWriter.write(reportJson.get(id).toString());
				tempReportWriter.newLine();
				lineNumber++;
			}
		}
		//tempReportWriter.write("]");
		
		tempReportWriter.close();
		reportFile.delete();
		reportFile.createNewFile();
		FileChannel temp = new FileInputStream(tempReportFile).getChannel();
		FileChannel original = new FileOutputStream(reportFile).getChannel();
		original.transferFrom(temp, 0, temp.size());
		FileWriter fw = new FileWriter(reportFile, true);
		fw.write("]");
		fw.close();
		temp.close();
		original.close();
		//Files.copy(tempReportFile.toPath(), reportFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
//		FileChannel temp = null;
//		FileChannel original = null;
//		try {
//			temp = new FileInputStream(tempReportFile).getChannel();
//			original = new FileInputStream(reportFile).getChannel();
//			original.transferFrom(temp, 0, temp.size());
//		} finally {
//			if(temp != null) {
//				temp.close();
//			}
//			if(original != null) {
//				original.close();
//			}
//		}
		
		
		bw.close();
		return outputFile;
	}
	
	private static void updateStatus(String field, String newValue) {
		Gson gson = new Gson();
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader(statusPath));
		} catch (IOException e) {
			App.handleException(e);
			return;
		}
    	MarasStatus statusJson = gson.fromJson(reader, MarasStatus.class);
    	
    	switch(field) {
		case "status":
			statusJson.setStatus(newValue);
			break;
		case "updated":
			statusJson.setUpdated(newValue);
			break;
		case "sources":
			statusJson.addSource(newValue);
			break;
		case "message":
			statusJson.setMessage(newValue);
		}
    	try(Writer writer = new FileWriter(statusPath)){
    		gson.toJson(statusJson, writer);
    	} catch(IOException e) {
    		App.handleException(e);
    		return;
    	}
		
	}
}