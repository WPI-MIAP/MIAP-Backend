# Evaluation and Testing

After completing our integration and improvement steps in creating MIAP, we
needed to ensure not only that it would run without bugs, but that it still was
capable of providing users with the tools to accomplish all of the tasks that 
they were able to in the previous iterations of DIVA. This chapter, divided
into our technical testing and user evaluation sections, will outline
our evaluation processes.

## Technical Testing

Though we started the project with existing algorithms for ranking and 
visualizing the drug interaction data, we redeveloped the systems from the
ground up, so it was essential that we conduct testing to show that we were able
to reproduce the functionality of the discreet tools we started with. To that
end, we conducted several tests during our development process, ensuring that,
at each stage, we were able to mine and rank the data correctly.
[Technical Requirement 1](#lst:req-t1) formalizes our expectations for
the technical performance of our system.

<div class="listing" id="lst:req-t1">
MIAP Technical Requirement 1
```
Correctly intake, mine, and rank new FAERS data provided by a user with no 
further human intervention
```
</div>

The first part of this testing was ensuring that the mining and ranking 
systems, once implemented in early iterations of MIAP, matched the 
rule-generation results from the tools developed by our graduate student 
mentors. We developed a testing function that matched each section of the
rule generation algorithm, itemsets generation, and rule generation, to ensure
that, for the same data, our code generated the same output as the old system.
In @Sec:java-refactoring, we discuss the specifics of this methodology.

In @Sec:improving-the-platform, we added several new features, including 
adverse reaction mapping, known-rule recognition, and drug name matching. When 
adding these features, we included tests to ensure that we correctly recognized 
known rules and that our drug name matching was sensible overall. The specific 
methodology of these testing procedures are discussed in detail in 
@Sec:adverse-reaction-mapping and @Sec:drug-name-matching.

Since we were comparing two different algorithms for use as our known-rule
recognition system, we were able to use the comparison metrics as our test for
correctness. We compared the number of rules recognized, trying to bring that
number as high as possible without any obvious inaccuracies.

With edit distance for drug name matching, we first tried to find the vocabulary
that had the most unique drug names without including a drug name that would be
difficult to match because of dosage information, included alternate names,
or had otherwise extraneous information in the drug name. The largest sensible
vocabulary was the one we chose to move forward with. After finding the most
suitable vocabulary, we moved on to testing the parameters for calculating the
edit distance on each input drug name. Here the goal was to have the smallest 
number of insensible matches. We examined both the raw percentage of unique
reports matched sensibly, as well as a metric that de-emphasized the most
common matches, so that they would not overshadow inaccuracies with more 
obscure drugs.

## Usability Evaluation

<!-- Here, introduce our Usability study by summarizing the function of the study,
why we chose to do this particular type of user case study, and discussing
alternatives. -->

Following the development of MIAP, it was essential to confirm that the application is still capable of meeting the design requirements outlined in cooperation with the FDA. To this end, we decided to conduct a quantitative and qualitative user evaluation that assesses how well MIAP meets the requirements and identifies areas for improvement within the application. When constructing the user evaluation, we considered the traditional user study that involves comparing the new application with the state of the art. However, safety analysts at the FDA do not currently use any visualization tool for identifying MDARs, so we could not take this approach. While we considered conducting a comparison study between the previous DIVA interface and the new MIAP application, we decided to focus instead on having users complete a number of tasks within the MIAP application alone to focus more on its usability and less on the differences in visual appeal between the two interfaces. 

### Experimental Design Methodology

Our usability evaluation was focused on determining how well the MIAP 
application met the requirements from @wpimaras, discussed in 
@Sec:diva-methodology. During the development process for MIAP, we decided to 
skip over [Requirement 7](#lst:req7) and leave it for future work to focus on more pressing features.

Requirements [8](#lst:req8) and [9](#lst:req9) focused on usability and were
general enough for us to consider part of the overall requirement for the
project, in that creating an application that is usable is part of following
good software engineering practices. Therefore, we omitted those requirements
from our final list of requirements for MIAP. Thus, the six following 
requirements, adapted from @wpimaras, informed the development of our usability
study.

<div class="listing" id="lst:req-1">
MIAP Usability Requirement 1
```
Provide a view of all ranked hypothetical interactions at-a-glance.
```
</div>

As in the original DIVA platform, viewing all the hypothetical interactions
at-a-glance on the main screen is an essential requirement. Having designed the
"Overview" view to be the root of all user interactions with the 
interface---it is where drugs and interactions are selected, filtered and
searched for---we need to test how well the Overview provides this feature.

<div class="listing" id="lst:req-2">
MIAP Usability Requirement 2
```
Facilitate differentiation between unknown hypothetical interactions
and already researched known interactions.
```
</div>

The mined data is going to include many reports for known drug interactions,
so it is important to be able to tell them apart from the hypothetical 
interactions that need further research to confirm. We need to test if the 
method we used to differentiate them---solid lines for unknown, hypothetical,
interactions, and dashed lines to represent known reactions---is sufficient for
users to easily tell the two states apart.

<div class="listing" id="lst:req-3">
MIAP Usability Requirement 3
```
Allow focus on select interactions for further study.
```
</div>

The Galaxy View and Interaction Profile are parts of the visualization that
are designed to provide focus on a select set of interactions beyond what is
available in the Overview. We need to be able to see if they can provide the
needed functionality, in that they get rid of uninteresting or unimportant
drugs and interactions and allow for users to be able to keep track of them.

<div class="listing" id="lst:req-4">
MIAP Usability Requirement 4
```
Facilitate the prioritization of focused interactions by various criteria.
```
</div>

Prioritization is the key to being able to analyze a set of drug interactions.
Once a user chooses or is assigned a group of drugs to examine, they need to be
able to tell which is the most interesting, and that can depend on a number
of criteria: the contrast scores of the interactions, the severity of the 
reactions, the number of reports for each interaction, or others. Therefore,
we need to be able to test how well MIAP is able to prioritize interactions
by different criteria.

<div class="listing" id="lst:req-5">
MIAP Usability Requirement 5
```
Facilitate detection of severe ADRs.
```
</div>

As discussed in [@Sec:diva-methodology], we need to be able to see when an
interaction involves a serious reaction. Adverse reactions that threaten
imminent death are going to be prioritized higher than those which are less
dangerous, so we want to be able to test how easy it is to notice those
severe reactions.

<div class="listing" id="lst:req-6">
MIAP Usability Requirement 6
```
Provide access to the underlying FAERS reports.
```
</div>

The underlying FAERS reports are the backbone of the evaluation process for
confirming MDARs, and we need to test how easily available we have made those
reports, as well as how accessible they are for the user to consume.

From these requirements, we developed a list of tasks that, if easily completed,
would demonstrate that the MIAP platform had met those requirements. However,
in its first iteration, this list included tasks that were easy for us, the
developers, to complete in multiple views. We wanted our test to evaluate
each view separately and show that the DIVA views added value to the system,
so we refined our tasks to focus on one each view.

### Experiment Overview

There were two planned phases for our qualitative evaluation experiment. First, we selected WPI students to interact with the interface in order to gain general
insights of the usability of the system. Additionally, this would allow us to determine whether MIAP meets the requirements laid out in @Sec:usability-evaluation, which are based on those 
from @wpimaras. 
Second, we intended to meet with domain specialists from the FDA to evaluate 
how well the tool meets their needs beyond the requirements they communicated 
with Kakar. However, we did not have time to meet with specialists from the FDA, so this part of the evaluation has been added to our recommendations for future work @Sec:fda-evaluation.

WPI Students
:   To recruit WPI students for the study we sought volunteers vial email,
    social media, and word of mouth, offering participants a chance to win
    one of a small number of incentives.

    After we had recruited participants, a member of our team met in person with
    each participant separately to conduct the evaluation. To begin with, 
    the participant was given a brief outline of the methodology used to conduct
    the evaluation in order to ensure that they were still okay with 
    participating. If they agreed to continue participating, then we asked 
    for their email address to use in the case that they won an incentive. They
    were then given a short overview (@Sec:usability-evaluation-materials) of the purpose of the interface that we 
    had developed along with some background knowledge necessary for a 
    rudimentary understanding of how to interact with the interface.

    Afterwards, the participant was presented with a series of tasks to complete
    using a set interactive version of the interface. While the participant was 
    performing each task, they were asked to verbally indicate what they were 
    trying to do and whether they were confused by anything. At the same time, 
    the member of our team was taking notes regarding the participant’s level of
    frustration/satisfaction, time taken to complete the task, and any verbal 
    comments that the participant made.

    Following the completion of the tasks, the participant was asked a series of
    questions regarding their interaction with the interface and anything that 
    they thought could be improved. The member of our team made notes as 
    necessary. Then, the participant had the opportunity to ask any questions 
    they might have had regarding our project. They were then notified that they 
    would receive an email if they were later found to have won one of the 
    incentives.

    After all the WPI students had participated in our evaluation, a drawing was
    held to identify the winners of the incentives. Each student was assigned
    a unique number from one to the number of WPI students who participated. 
    For each incentive, a random number generator was used to randomly select a 
    WPI student to win that incentive. If the same student was selected multiple
    times, we repeated the aforementioned process until all incentives were 
    assigned to different students. Then, each winner of an incentive was 
    notified via email, and a meeting was be arranged for them to pick up their 
    incentive.

FDA Domain Specialists
:   Please note, we did not have time to meet with specialists from the FDA, so this part of the evaluation has been added to our recommendations for future work @Sec:fda-evaluation. However, in cooperation with PhD students Kakar and Qin we designed the following approach. To recruit domain specialists from the FDA, we would contact members of the
    CDER that have assisted the research of Kakar and Qin.

    After finding domain specialists willing to participate in our study, a 
    member of our team will meet in person with each specialist separately to conduct 
    the evaluation. To begin with, the specialist will be given a brief outline of 
    the methodology used to conduct the evaluation in order to ensure that 
    they are still okay with participating. Then, they will be given a short 
    overview of the interface that we have developed. Before seeing the 
    interface itself, we will ask the specialist to try and imagine how they
    might use this kind of tool.

    Afterwards, the specialist will be invited to explore the interface directly. 
    We will guide them through the tasks that we set out for the WPI students, and
    ask them to try and use the interface to accomplish the use cases they 
    had mentioned previously, as well as any other way they might intend
    to use it. While the specialist is working they will be asked to verbally 
    indicate what they are trying to do and whether they are confused by 
    anything. At the same time, the member of our team will take notes regarding the 
    specialist’s level of frustration/satisfaction and any verbal comments 
    that the specialist makes.

    Following this, the specialist will be asked a series of questions regarding 
    their interaction with the interface, including whether they think the 
    interface would help them in identifying adverse drug-drug interactions 
    and anything that they think could be improved to help them better do 
    their job. The member of our team will make notes as necessary. Then, the 
    specialist will have the opportunity to ask any questions they might have
    regarding our project.

Both phases center around the requirements that we adapted from
-@wpidiva, in the design process. The tasks we developed from those requirements
each focus on their own views in the application. We presented the tasks with
relationships to the views we wished the subject to focus on when accomplishing
the tasks, so that we could understand how well each specific view fulfilled the
requirements set out for them. Our tasks, organized by view, were as follows:

Overview (Req. [-@lst:req-1])
:   \ 

    *   Task 1: Find 3 unknown drug-drug interactions (Req. [-@lst:req-3;-@lst:req-4]).
    *   Task 2: Find 3 drug-drug interactions with a score greater than 
        **0.5** (Req. [-@lst:req-3]).

Galaxy View
:   \ 

    *   Task 3: Find and select drugs **Trazodone**, **Metaxalone**, and **Tylenol**. Of
        these drugs, which drug has the highest number of ADRs 
        (Req. [-@lst:req-2;-@lst:req-5])?
    *   Task 4: For the drug **Metaxolone**, how many high scored, (score > **0.2**),
        and unknown interactions are there 
        (Req. [-@lst:req-3;-@lst:req-4;-@lst:req-5])?

Interaction Profile
:   \ 
    
    *   Task 5: Find which ADRs that occur between **Diazepam** and **Trazodone** 
        (Req. [-@lst:req-2]).
    *   Task 6: How many reports support the interaction between **Diazepam** and 
        **Trazodone** (Req. [-@lst:req-6])?
    *   Task 7: Find all severe ADRs associated with **Trazodone**'s 
        interactions  (Req. [-@lst:req-2;-@lst:req-5]).

Report View (Req. [-@lst:req-6])
:   \ 

    *   Task 8: Please open the reports for the interaction between drugs **Zoloft** and 
        **Tylenol**.
    *   Task 9: List the three most frequent drugs reported with the interacting drugs 
        **Zoloft** and **Tylenol**.
