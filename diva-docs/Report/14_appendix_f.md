# MARAS Documentation

This repository (https://bitbucket.org/divamqp/diva-maras) contains the code for the Multi-Drug Adverse Reaction Analytics Strategy (MARAS) system. 

The MARAS directory contains the java code for the MARAS system, including pre-processing, drug and reaction parsing, data mining, and output for visualization (see https://bitbucket.org/divamqp/diva-node-web). 

The diva-docs repository contains the markdown files for our report.

## How to run

The MARAS system is used with the [DIVA platform](diva.wpi.edu:3000). The analysis runs when a FAERS data file is uploaded, and the visualization is updated accordingly.

The MARAS system can be manually run using the java code, running App.java. Command line arguments are as follows:

-f: FAERS .zip file to analyze

-d: Path to the Drug map file to be used (should usually point to the test2.json file contained in the data directory)

-r: Path to the Standardized Known Rules file to be used (should usually point to the knownRules_standardized.csv file contained in the data directory)

-m: Path to the MetaMap installation directory

-s: Minimum support for rules to be considered

-c: Minimum confidence for rules to be considered

-o: Output Path

-p: Path to the reports file to add on to (for the Visualization)

-t: Path to the status file (for the Visualization)

MetaMap processes should be running separately when the system is started.

To best emulate the process used by the DIVA system, use the maras.sh wrapper script contained in the [DIVA repository](https://bitbucket.org/divamqp/diva-node-web), which handles launching MetaMap automatically.

## Output

The MARAS system outputs multiple files.

rules.csv: This is the rule data that is used by DIVA to visualize the results. In the DIVA-MARAS workflow it is output to the storage directory, which is where the DIVA system looks for this data. 

closed_results.txt: This file gives a detailed view of all of the rules mined, sorted from highest score to the lowest. Each rule displays the drugs and the associated ADR, along with the contrast score and the rule's support and confidence. In addition, it shows the support and confidence of each contained drug mapped to the ADR, to represent the Context Associated Cluster (CAC).

_rules_drug_adr_matching.txt: This shows the same rule data in rules.csv in a different format. _rules_drug_adr_matching_long_names.txt shows the same data, but with the original drug names rather than the matched ones.

## Data

The data directory contains much of the information used by the system. Reports.txt is a file containing all report information, used by the DIVA platform in order to maintain relevent report data and have it available for FDA evaluators. The ZipFiles directory contains two sample FAERS data sets: the 2013Q1 and 2017Q2 data. The raw directory is where the underlying text files from the FAERS data sets are extracted; if there are duplicates here, the system will end the process, recognizing that the data has already been analyzed. 

For each data set, the report information is converted to a more brief and compact format, and written to a text file with a name corresponding to the data set; for example the 2013 Q1 report data will be exported to 2013Q1Reports.txt. This is so when additional data is added, there is no need to repeat FAERS data extraction and analysis.

If you would like to run analysis on a set of data by itself, simply remove the associated Reports files from the raw directory, and remove the truncated report file (e.g. 2013Q1Reports.txt) for any data you do not want included. Otherwise, the included data set will be used in combination with the previously analyzed data. 