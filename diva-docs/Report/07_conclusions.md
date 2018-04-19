# Conclusions

In this section, we summarize the accomplishments of this 
project and conclude by proposing areas where this project can be expanded in the future.

## Summary and Findings

In this project we accomplished a complete refactor of the existing MARAS and DIVA systems into one streamlined application, MIAP. Furthermore, we enhanced both the rule-mining and data visualization capabilities of the resultant system. By making organized code repositories and extensively documenting the refactored code, we ensured that the system would be maintainable and extensible in the future. To improve the rule-mining system, we added drug matching and ADR standardization capabilities, that in turn improved the automatic labeling of known rules. When refactoring the data visualization system, we improved usability by adding features such as the score distribution coupled with the range selector for filtering scores and the generation of report "chips" that allow users to easily access reports for a specific drug or interaction. After a user evaluation, participants' feedback was incorporated by further enhancing the system to increase the system's usability. As a result of this project, users are now able to go through the entire multiple drug interaction analysis process through the user interface, from uploading FAERS files for analysis to identifying novel drug-drug interactions.

## Future Work


Through the usability evaluation process, our own experience using the application, and discussions with domain specialists at the FDA, we have developed a list of recommendations for improvements that can be made to MIAP in the future. Principal among these are the integration of a database, user interface improvements for the Report View, the ability to create custom datasets, and evaluation by domain specialists with the FDA.

### Database

A database is necessary to make this application viable because the current method of loading reports and rules takes an excessive amount of time. Currently, the reports file is parsed each time a request for reports is made, and likewise the rules file is parsed each time a request for rules is made. As more data is added, the times for each request will only increase. By adding a database for reports and rules, waiting times could be drastically reduced when querying by report ID or drug names.

### Report View Improvements

As is, the Report View provides users with a wealth of information in a large table that is hard to analyze. To improve the usability of this view, we suggest adding the ability to search through reports, filter reports, and change the format of the information so that it is easier to read and understand. Additionally, annotation functionality should be added to satisfy [Requirement 7](#lst:req7), meaning that domain specialists should be able to leave comments on specific reports or label rules as known, unknown, a co-occurrence, etc.

### Visualization of Custom Datasets

In the MIAP system, users can see the datasets that have already been added and upload FAERS data through the user interface. When a new dataset is uploaded, the data analysis is ran on that dataset as well as all previously uploaded datasets to generate an aggregated visualization file. However, some users may wish to look at some subset of the datasets. For this reason we suggest adding functionality for the creation of custom datasets and investigation into methods of optimizing collective data analysis to reduce the time taken to produce a new visualization when new data is added or a custom dataset is created.

### FDA Evaluation

Lastly, we recommend having domain specialists with the FDA evaluate MIAP to ensure that the application meets their needs and to receive any suggestions for making it easier to use. A proposed methodology for this evaluation can be found in @Sec:usability-evaluation. Specifically, we would also like to determine whether metaphors currently employed in the visualization are intuitive, such as the use of dashed lines for known rules and solid lines for unknown rules or the use of the term "edge" rather than "link". In the case of dashed and solid lines, many users that evaluated MIAP found it counterintuitive for dashed lines to be known and solid lines to be unknown. However, we believe that given the domain, this metaphor makes sense because unknown rules are more important to FDA safety analysts and therefore are represented by solid lines. 

Meanwhile, for the terminology used to describe the lines between two nodes, it is possible that FDA personnel may not be familiar with the term edge, as this term is commonly related to graph theory, a topic commonly studied in computer science but not in other fields. Therefore, these evaluations will help establish how well the system meets the FDA's needs, as well as reveal what changes in metaphors and terminology would improve the system's usability.

