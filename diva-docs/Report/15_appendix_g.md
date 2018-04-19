MARAS (Java) Documentation
=======================================

# `public class App`

Main Application class for the MARAS system. This class handles arguments, input data parsing, and output formatting.

 * **Author:** Brian

     <p>

## `public static void main(String[] args)`

Main function to handle parameters

 * **Parameters:** `args` — The arguments passed

## `public void run()`

Begin MARAS execution

## `public static void handleException(Exception e)`

Static function to handle an exception and gracefully exit the execution. This function updates the status file to properly include the error message. It also prints out the error details to Standard Output to ensure it is properly written to the log.txt file. This function should be called any time an Exception is caught that should kill the program.

 * **Parameters:** `e` — The Exception thrown

## `private File parseFaersFiles(List<File> files, File outputDir) throws IOException`

Parse a list of FAERS data files, returning an output file organizing each report

 * **Parameters:** `files` — The FAERS data files (DRUG, REAC, and DEMO)
 * **Returns:** The output file
 * **Exceptions:** `IOException` — 

 # `public class Dataset`

The class defining the Dataset structure. A Dataset is a collection of {@link Report}s where each report is generated from a single Adverse Reaction FARAS report.

 * **Author:** Andrew Schade

     <p>

## `public Dataset(List<File> reportFiles, int maxTransactionLength, double minSupport, double minConfidence, File reportOutput)    throws FileNotFoundException, UnsupportedEncodingException`

Reads in a dataset from a file.

 * **Parameters:**
   * `drugFiles` — 
   * `path` — The location of the file containing the dataset data.
 * **Exceptions:**
   * `FileNotFoundException` — 
   * `UnsupportedEncodingException` — 

## `public InteractionSets getInteractions()`

Generates itemsets after checking if the itemsets are already generated, if they are it will return the generated itemests.

 * **Returns:** The {@link InteractionSets} mined with {@link FPGrowth}.

## `public RuleSets getAssociationRules()`

Generates rulesets after checking to see if the rulesets are already generated. If they are, it will return the generated rulesets. Also will generate itemsets if those are not already generated.

 * **Returns:** The {@link RuleSets} mined with {@link Agrawal}.

## `public int getDatasetSize()`

Gets the number of reports in the database.

 * **Returns:** The size of the database defined by the number of reports.


# `public class Drug extends Item`

Class representing a Drug This class maintains a static list of all drugs used in the system

 * **Author:** Andrew Schade

     <p>

## `protected Drug(int ID, String shortName, String fullName)`

Private Constructor, just initializes drug list.

## `public static void readDrugs(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException`

Read drugs from the drug map file to initialize drugs with their IDs

 * **Parameters:** `path` — The path to the drug file
 * **Exceptions:**
   * `JsonIOException` — 
   * `JsonSyntaxException` — 
   * `FileNotFoundException` — 

## `private static String process(String name)`

Process a drug name to remove any key phrases or words contained in the wordsToRemove list

 * **Parameters:** `name` — The drug name
 * **Returns:** The processed drug name

## `public static Drug match(String name)`

Matches a drug name to a pre-existing drug object

 * **Parameters:** `name` — The name of the drug
 * **Returns:** The drug object


# `public class Group implements Comparable<Group>`

A Group contains a complete ADR rule and its singleton component associations.

## `public Group(Rule comb)`

Creates a group from a combined rule. Populates the bins for singletons, but does not find them. Does not calculate the group score.

 * **Parameters:** `comb` — The rule to use as a basis for the group.

## `public void setKLDs(double[] klds)`

Set the kl divergences to a new array.

 * **Parameters:** `klds` — A list of KL divergence values.

## `public double[] getKLDs()`

 * **Returns:** the array of kl divergences.

## `public void calcScore()`

The algorithm for calculating the score. Updates the score in the group but not the composite rule.

<code>score = ((overall_confidence - average_confidence) * (1 - (conf_std_dev / avg))) / number_of_comb</code>

## `public double getScore()`

 * **Returns:** The group score.

## `public void setScore(double score)`

Set the score of a group. Used for testing to ensure that the groups generated match those from another file.

## `public Rule getCompositeRule()`

 * **Returns:** The composite rule for the group.

## `public ArrayList<HashSet<Rule>> getSingletonRules()`

 * **Returns:** An arraylist of the Hashset for singleton rules.

## `@Override  public int compareTo(Group g)`

Comparator for descending sorting of groups. This comparator behaves opposite to convention.

## `public void addSingle(Rule rule)`

Adds a singleton to the appropriate bin.

 * **Parameters:** `rule` — The singleton rule to add.


# `public class Groupset extends ArrayList<Group>`

This class contains a set of groups developed from a closed ruleset. It provides an interface for creating output files in formats useful for DIVA visualization and human consumption.

 * **Author:** Andrew Schade

     <p>

## `public String getResultString()`

Get a string representing the results of analysis

 * **Returns:** The string

## `public String getResultString(int top)`

Get a string representing the results of analysis

 * **Parameters:** `top` — Max number of results to consider
 * **Returns:** The String

## `public String getVizString()`

 * **Returns:** The visualization string

## `public String getVizString(int top)`

Get the string used by the visualization to represent the data

 * **Parameters:** `top` — The max number of results
 * **Returns:** The visualization string

## `public String getVizLabelString()`

Visualization data file generation

 * **Returns:** The string

## `public String getVizLabelString(int top)`

Visualization Data file Generation.

 * **Parameters:** `top` — The max number of results to consider
 * **Returns:** The string

## `List<String> matchingReportIDs = new ArrayList<String>()`

To find a list of supporting reports, we check to find the list of Report IDs that are similar across all of the Items

## `public void outputDIVARules(BufferedWriter outfile) throws IOException`

Write the DIVA rules

 * **Parameters:** `outfile` — The output file to write to
 * **Exceptions:** `IOException` — 

## `private String getDIVARuleString()`

Get the DIVA rule string

 * **Returns:** The rule string

## `private String getDIVARuleString(int top)`

Get the DIVA rule string

 * **Parameters:** `top` — The max number of results to consider
 * **Returns:** The rule string

## `private String getHeaderRow()`

Get the header string for the output

 * **Returns:** The header string

## `public void outputGroups(BufferedWriter outfile) throws IOException`

Write the results to file

 * **Parameters:** `outfile` — The file to write to
 * **Exceptions:** `IOException` — 

## `public void outputViz(BufferedWriter labels, BufferedWriter info) throws IOException`

Write the visualization strings to file

 * **Parameters:**
   * `labels` — The label file to write to
   * `info` — The info file to write to
 * **Exceptions:** `IOException` — 

## `public void outputViz(BufferedWriter outfile) throws IOException`

Write the visualization string to file

 * **Parameters:** `outfile` — The file to write to
 * **Exceptions:** `IOException` — 


# `public class Interaction extends Itemset`

This class represents a set of items and a support count. This implementation is based on the SPMF Itemset data type by Philippe Fournier-Viger.

 * **Author:** Brian McCarthy

     <p>

## `public void addItem(Item item)`

Add an item to the list

 * **Parameters:** `item` — The item to add

## `public Interaction cloneMinusOne(Item item)`

Get an interaction with the list of items excluding the given one

 * **Parameters:** `item` — The item to exclude
 * **Returns:** The cloned interaction

## `public Interaction cloneMinusInteraction(Interaction interaction)`

Get an Interaction with all of the items NOT contained in the given interaction

 * **Parameters:** `interaction` — The interaction to exclude items from
 * **Returns:** The cloned interaction


# `public class InteractionSets extends Itemsets`

This class represents a set of Interactions, where an Interaction is a set of items with an associated support count. Interactions are ordered by size. For example, interactionSet 1 is a set of interactions of size 1 (contains 1 item).

Based on SPMF Itemsets data type by Philippe Fournier-Viger

 * **Author:**
   * Brian McCarthy
   * Andrew Schade

     <p>

## `public InteractionSets getClosures()`

This method finds all the interactions within this collection where for all k greater than the level of a given interaction, the interaction is a member of the closure if and only if the interaction is not a subset of any interaction with a level of k.

 * **Returns:** The K-interactionsets with only closed interaction contained inside.



# `public class Item implements Comparable<Item>`

A class defining the Item structure. It also contains a map for associating Items with their unique IDs.

 * **Author:** Andrew Schade

     <p>

## `public static void restartMetaMap()`

Method to relaunch the MetaMap API

## `public static Item fromInt(int i)`

Finds the Item defined by the specified unique ID.

 * **Parameters:** `i` — The unique ID query to find an Item.
 * **Returns:** The item matching the unique ID query. If the ID is not matched a

     NoSuchElementException will be thrown.

## `public static Item fromIntKnown(int i)`

Finds the Item defined by the specified unique ID. Doesn't have a lock because no new data is being added, only look-ups are happening.

 * **Parameters:** `i` — The unique ID query to find an Item.
 * **Returns:** The item matching the unique ID query. If the ID is not matched a

     NoSuchElementException will be thrown.

## `public static int itemID(String name, boolean isDrug, boolean standardized)`

Gets the item ID for a specified string.

 * **Parameters:** `name` — the string name of the item. Will be matched ignoring case.
 * **Returns:** The integer ID matched for the string. If multiple items have the

     same name, the lowest ID match will be returned. A

     NoSuchElementException will be thrown if there is no matched string.

## `public static int itemIDKnown(String name, boolean isDrug, boolean standardized)`

Gets the item ID for a specified string. Doesn't have a lock because no new data is being added, only look-ups are happening.

 * **Parameters:** `name` — the string name of the item. Will be matched ignoring case.
 * **Returns:** The integer ID matched for the string. If multiple items have the

     same name, the lowest ID match will be returned. A

     NoSuchElementException will be thrown if there is no matched string.

## `public static Item makeItem(String name, boolean isDrug, boolean standardized)`

This is the public constructor for creating an Item. It loads an item in from a name. This can be updated in the future to ensure ID consistency. If a name already maps to an item, then it doesn't create a new one.

 * **Parameters:** `name` — The name of the drug or reaction, etc.
 * **Returns:** A new item created, which is added to the data structure tracking all

     items.

## `public static Item checkADRName(String name, boolean createNew)`

Checks to see if an adr corresponds to an alternate name for a known adr. If so, returns the preferred name for the adr.

 * **Parameters:**
   * `name` — The unformatted name of an ADR
   * `createNew` — Boolean flag indicating whether a new ADR item should be created if it does not already exist. If false,

     a NoSuchElementException is thrown.
 * **Returns:** Preferred name for adr

## `public static int getNumDrugs()`

Gets the number of distinct items that are drugs.

 * **Returns:** The number of distinct items that are drugs.

## `public static int getNumReactions()`

Gets the number of distinct items that are adr's.

 * **Returns:** The number of distinct items that are adr's.

## `@Override  public String toString()`

Gets the standardized version of the item's name. I.e. all spaces are removed and all letters are lower case.

return Standardized version of the item's name.

## `protected Item(String name, String longName, int ID, boolean isDrug)`

The private constructor for Item. This is pretty straightforward. It contains the name of the item and its unique ID.

 * **Parameters:**
   * `name` — The name of the drug or reaction.
   * `ID` — The unique ID referring to the Item.

## `public boolean isDrug()`

A getter object to determine if this item is a drug or a reaction.

 * **Returns:** true if drug, false if reaction

## `public int getiD()`

A getter object to receive the unique ID of the Item.

 * **Returns:** the Item's unique ID.

## `public String getShortName()`

A getter object to receive the short name of the Item

 * **Returns:** the Item's name.

## `public String getLongName()`

A getter object to receive the long name of the Item

 * **Returns:** the Item's name.

## `@Override  public int compareTo(Item arg0)`

Used to determine if two items are the same by comparing their IDs.

 * **Parameters:** `arg0` — The item being compared with this item.
 * **Returns:** Integer indicating that this item's ID comes before (-1), after (1), or is the same as (0) the other item's ID.


# `public class MarasStatus`

Function representing the status of the system This class is used to simplify the JSON management

 * **Author:** Brian

     <p>



# `public class Report implements Comparable<Report>, Iterable<Item>`

A report maps to a single entry in the FARAS data recovered for use.

 * **Author:** Andrew Schade

     <p>

## `@Override  public int compareTo(Report other)`

This function compares two reports to each other, allowing sorting by ascending transaction length. In cases where the transaction length is the same, the ordering will use the ordering determined by the drugs and reactions in the reports, starting with the first.


# `public class Rule extends AssocRule implements Comparable<Rule>`

This class represent a MARAS association rule. A rule can be used to find a group (a rule and all singleton association rules that can be derived from that rule).

This class is an extension of the AssocRule class from the SPMF library put together by Philippe Fournier-Viger.

 * **See also:**
   * AssocRule
   * Item
   * Group
   * RuleSets
 * **Author:** Brian Zylich

## `private static Map<Rule, Boolean> knownRules = new HashMap<Rule, Boolean>()`

Used to hold all known drug-drug interactions

## `public static void parseStandardizedKnownRulesThreading(String fileName)`

Reads in a set of known rules that have already been converted to standardized forms. This function uses threading to accomplish this goal.

 * **Parameters:** `fileName` — The path of the file containing the standardized known rules.

## `public static void standardizeAllKnownRulesThreading(String fileName)`

Reads in a set of known rules that have not already been converted to standardized forms. Then, the standardized known rules are written to a file. This function uses threading to accomplish this goal. This function should only be run once or whenever the known rules file changes and a new standardized known rules file must be generated.

 * **Parameters:** `fileName` — The path of the file containing the known rules.

## `private static void addKnownRule(List<Item> antecedent, List<Item> consequent)`

Adds a new rule, created from the given antecedent and consequent, to the map of known rules. This function is protected by a lock to allow for multithreading.

 * **Parameters:**
   * `antecedent` — A list of items corresponding to the drugs involved in the rule.
   * `consequent` — A list of items corresponding to the reactions involved in the rule.

## `private List<Integer> reportIds`

Used to track which FAERS reports support this rule

## `private final List<Item> antecedent`

List of Item objects forming the antecedent

## `private final List<Item> consequent`

List of Item objects forming the consequent

## `private boolean known = false`

Boolean indicating if the rule corresponds to a known drug-drug interaction

## `public Rule(List<Item> itemset1, List<Item> itemset2, int supportAntecedent, int transactionCount, double confidence, double lift)`

Constructor

 * **Parameters:**
   * `itemset1` — the antecedent of the rule (an itemset)
   * `itemset2` — the consequent of the rule (an itemset)
   * `supportAntecedent` — the coverage of the rule (support of the antecedent)
   * `transactionCount` — the absolute support of the rule (integer)
   * `confidence` — the confidence of the rule
   * `lift` — the lift of the rule

## `private Rule(List<Item> itemset1, List<Item> itemset2)`

Constructor - only to be used for known DDRs (don't care about their support, confidence, etc)

 * **Parameters:**
   * `itemset1` — the antecedent of the rule (an itemset)
   * `itemset2` — the consequent of the rule (an itemset)

## `private static int[] getItemIds(List<Item> itemList)`

Helper function used to turn a list of Items into an array of integers representing those items.

 * **Returns:** the array of integer IDs.

## `public List<Item> getAnte()`

Returns the list of item objects representing the antecedent of the rule.

 * **Returns:** the list of item objects.

## `public List<Item> getCons()`

Returns the list of item objects representing the consequent of the rule.

 * **Returns:** the list of item objects.

## `public Group getGroup(RuleSets rules)`

Creates a Group from this Rule that includes this Rule and all subrules that can be derived from this Rule.

 * **Returns:** the Group created from this Rule.

## `public Interaction getInteraction()`

Used to find the interaction corresponding to this rule.

 * **Returns:** InteractionSet containing all items from the antecedent and the consequent of the rule.

## `public List<Integer> getReports()`

Used to find which FAERS reports support this association rule.

 * **Returns:** list of report ids.

## `public void checkKnown()`

Used to check whether a rule is known or unknown, modifying the 'known' variable within the Rule object. This function should be run after loading all known rules.

## `public boolean isKnown()`

Used to determine if this rule corresponds to a known drug-drug interaction

 * **Returns:** boolean indicating if rule is known

## `private Rule getSimpleRule()`

Used to compare a newly generated rule against known rules by simplifying the rule for hashing.

 * **Returns:** Simplified version of the rule containing only the antecedent and consequent

## `public boolean sameRule(Rule other)`

Check whether two rules have the same items in their antecedent and consequent

 * **Parameters:** `other` — The other rule to compare to
 * **Returns:** boolean indicating whether the antecedents and consequents match

## `@Override  public boolean equals(Object other)`

Check whether two rules have the same items in their antecedent and consequent, as well as the same support and confidence values.

 * **Parameters:** `other` — The other rule to compare to
 * **Returns:** boolean indicating whether the rules are the same

## `@Override public String toString()`

Get a string representation of the rule, including the rule's antecedent, consequent, support, and confidence.

 * **Returns:** string representation of the rule

## `@Override public int compareTo(Rule r)`

Comparator used to sort rules in groups for hashing and to facilitate testing.


# `public class RuleSets extends AssocRules`

This class represent a set of MARAS association rules. RuleSets can be used to find closures (filters rules to ensure that the itemset crated by the rules is a closure).

This class is an extension of the AssocRules class from the SPMF library put together by Philippe Fournier-Viger.

 * **See also:**
   * AssocRules
   * InteractionSets
   * Rule
 * **Author:** Brian Zylich

## `public RuleSets(String name)`

Constructor

 * **Parameters:** `name` — a name for this list of association rules (string)

## `public RuleSets(String name, AssocRules rules)`

Constructor

 * **Parameters:**
   * `name` — a name for this list of association rules (string)
   * `rules` — rules generated by the association rule mining algorithm

## `public RuleSets findClosures(InteractionSets closures)`

Filters the rules to ensure that the itemset created by the rules is a closure.

 * **Parameters:** `closures` — The InteractionSets object containing all closed InteractionSet's.
 * **Returns:** RuleSets object containing all of the closed rules.

## `public RuleSets filterRules()`

Filters the rules to ensure that the antecedent only contains drugs and the consequent only contains reactions.

 * **Returns:** RuleSets object containing all of the filtered rules.

## `public RuleSets filterNoSingletonRules()`

Filters rules to remove rules with <2 drugs

 * **Returns:** RuleSets object without any rules that have less than 2 drugs

## `public RuleSets filterNoComplexRules()`

Filters rules to remove rules with >2 drugs or >1 ADR

 * **Returns:** RuleSets object without any rules that have more than 2 drugs or more than 1 adr

# `public class Agrawal extends AlgoAgrawalFaster94`

This class extends the AlgoAgrawalFaster94 class from the SPMF library put together by Philippe Fournier-Viger. The extension of this association rule mining algorithm is used to run the algorithm using InteractionSets and producing RuleSets.

 * **See also:**
   * AlgoAgrawalFaster94
   * InteractionSets
   * RuleSets
 * **Author:** Brian Zylich

## `public Agrawal()`

Constructor

## `public RuleSets runAlgorithm(InteractionSets interactions, int databaseSize, double minconf)`

Run the algorithm

 * **Parameters:**
   * `interactions` — a set of frequent itemsets
   * `databaseSize` — the number of transactions in the database
   * `minconf` — the minconf threshold
 * **Returns:** the set of association rules

 