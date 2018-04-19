Introduction
============

More than 100,000 deaths and 2 million hospitalizations, annually,
are caused by the side effects of prescribed drugs or the unintended
interactions from taking several drugs at once [@fda16, placeholder]. These side effects,
called Adverse Drug Reactions (ADRs), create a public health problem
that makes it one of the leading causes of death worldwide. While
the ADRs directly caused by a single drug are well-researched, often
documented well before drugs reach the public market, there is much
less documentation about how drugs will act in combination with each other.
Polypharmacy, or having multiple drugs prescribed at once, affects more than 
40% of elderly people living at home and some of these patients regularly take 
up to 18 different drugs. The number of combinations of drugs
that are taken together is far larger than can be researched in depth by medical
professionals, so it is necessary to have tools that can use patient data, collected
after the drugs have gone to market, to 
assist researchers in identifying probable candidates for multi-drug 
interactions.

Originally developed by Xiao Qin and Tabassum Kakar [@wpimaras, @wpidiva], the 
Multi-Drug Adverse Reaction Analytics Strategy (MARAS) and Drug-Drug
Interactions via Visual Analysis (DIVA) systems are designed to aid
analysts in discovering these multi-drug interactions. MARAS focuses
on mining FAERS, a dataset provided by the FDA that contains ADR reports
submitted around the globe, to find groups of drugs likely to be contributing
to Multi-drug Adverse Reactions (MDARs), sorted by a severity score. DIVA,
on the other hand, focuses on visualizing the 2-drug interactions found by MARAS.
It allows users to see the network of interactions at a glance and find the
raw FAERs reports contributing to the groupings found by MARAS.

For these tools to be used by professional analysts, they need to be tightly
integrated, efficient, and stable. Our project evaluated the MARAS and DIVA systems
as they were previously, implemented improvements in code quality and performance,
and integrated the systems
into a fully-functional web application that can be used by analysts to
discover new MDARs from FAERS data. We focused on the usability and
maintainability of the system, while also developing additional features
and designs, to ensure that FDA employees and other analysts can utilize
the application as effectively as possible. The DIVA software can
improve the speed, efficiency, and accuracy of MDAR discovery and allow
the FDA to more efficiently identify and publicize these dangerous
interactions.

## Motivation

When patients experience unwanted symptoms while taking a drug, they submit reports,
called Adverse Drug Events (ADEs), to an FDA-run database, the "FDA's Adverse 
Event Reporting System" (FAERS) [@fda17]. As more and more people are being prescribed
multiple drugs, the number of ADEs sent to FAERS is increasing rapidly. This
database receives millions of new records every year, which makes it impossible
for individual analysts to take full advantage of the amount of data being collected.
There is need for a system that was able to use the wealth of data in the
FAERS database to allow drug evaluators make informed decisions about where
to focus more in-depth investigations.

This niche requires an application that can provide evaluators an at-a-glance
overview of the possible drug interactions indicated by the data, as well as
a system for understanding which associations are the most common or most 
severe in order to inform investigator priorities.

## Previous Research

Analyzing the multitude of drug interactions requires both a back-end mining
algorithm that can report on the most common or likely combinations of drugs and
an interface that can allow evaluators to quickly understand those interactions
and then dive deeper into individual reports to study further. Our graduate
student partners [@wpimaras, @wpidiva], researched two techniques that address
the back-end space and interface respectively. Kakar and Qin created the
Multi-drug Adverse Reactions Analytic System (MARAS), which ranks mined associations
by their 'contrast score' a metric that quantifies how closely an ADR is
associated with a pair of drugs rather than each drug individually.

They also developed the Drug-Drug Interactions via Visual Analytics (DIVA) paradigm, which is a visualization, with several different views to allow the 
interactions---found and ranked in MARAS---to be analyzed in different contexts
within the same application. Unfortunately, these two projects were conducted 
largely independently. Thus, it was a labor-intensive, manual process to take the 
associations ranked in MARAS and integrate them into the DIVA visualization.

## Contributions

Our project focused on developing a **minimum viable product** to demonstrate
how Kakar and Qin's work could be used to assist evaluators discovering and testing 
multiple-drug adverse reactions. We unified MARAS and DIVA into a single 
application, the Multiple-drug Interaction Analytics Platform (MIAP), which 
focused on creating a smoothly interactive user experience. Our results, 
supported by an evaluation, demonstrate the utility of the technology for 
supporting the drug evaluation workflow.

As a minimum viable product, our project demonstrated the capability to:

*   Correctly intake, mine, and rank new FAERS data provided by a user with no 
    further human intervention,
*   View all ranked hypothetical drug-drug interactions at-a-glance,
*   Differentiate between unknown hypothetical drug-drug interactions and known interactions that have already been researched,
*   Focus on and select hypothetical drug-drug interactions for further study,
*   Prioritize focused hypothetical drug-drug interactions by various criteria,
*   Facilitate detection of severe Adverse Drug Reactions, and
*   Link to the underlying FAERS reports.

Future work can conduct experiments on the customer value of MIAP,
determining how valuable a tool like it would be to evaluators. If the system
is found to be worth developing further, additional improvements should me made to expand MIAP into an
application that fulfills
all features required of a software suite that can facilitate the complete
evaluation process. This requires research in determining exactly what other
workflows in the MDAR evaluation process should be integrated into a single
application.
