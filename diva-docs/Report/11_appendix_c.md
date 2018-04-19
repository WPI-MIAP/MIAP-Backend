# User Interface (JavaScript) Documentation

## Constants


loggerMiddleware
:	Entry point of the React App

currentDrugs
:	Fetch the drugs from the server and add to state object

	| Param | Type |
	| --- | --- |
	| state |  | 
	| action | <code>\*</code> | 

selectDrug
:	Select a drug or remove the selected drug

	| Param | Type |
	| --- | --- |
	| state | <code>string</code> | 
	| action |  |

selectMaxScore
:	Select the max score or set default to 2

	| Param | Type |
	| --- | --- |
	| state | <code>number</code> | 
	| action | <code>\*</code> | 

selectMinScore
:	Select the min score or set default to 2

	| Param | Type |
	| --- | --- |
	| state | <code>number</code> | 
	| action | <code>\*</code> | 

selectRule
:	Select a rule or remove the currently selected rule

	| Param | Type |
	| --- | --- |
	| state | <code>string</code> | 
	| action | <code>\*</code> | 
	
treeViewSorting
:	Select the sorting method in the galaxy view
	
dmeColors
:	Colors and labels associated with different ranges of severe ADR counts
	
scoreColors
:	Colors and labels associated with different score ranges
	
scoreBorderColors
:	Colors associated with node borders for different score ranges (used in Interaction Profile)

baseNodeColor
:	Color of base node in Interaction Profile (same as color of nodes in Overview).
	
baseNodeBorderColor
:	Border color of base node in Interaction Profile.
	
severeADRColor
:	Color of severe ADRs in Interaction Profile.

regularADRColor
:	Color of non-severe ADRs in Interaction Profile.
	
barColor
:	Color of bars in barchart of Report View.
	
barSelectedColor
:	Color of selected bar in barchart of Report View.
	
selectedColor
:	Color used for bar indicating the selected tab.
	
primaryColor
:	Primary color of the application (used as background of toolbar among others).
	
secondaryColor
:	Secondary color of the application (used by score distribution).
	
complementaryColor
:	Color used for tabs, footer, etc.
	
wpiLogo
:	WPI logo shown on left of toolbar.
	
teamPhoto
:	Team picture found on About Us page.
	
medicines
:	Drug count icon for toolbar.
	
connection
:	Interaction count icon for toolbar.
	
overviewName
:	Display name for the view on the left (previously Overview).

galaxyViewName
:	Display name for the middle view (previously Galaxy View).
	
interactionProfileName
:	Display name for the view on the right (previously Interaction Profile).
	
generateColor
:   Generate color based on score

	| Param | Type |
	| --- | --- |
	| score | <code>number</code> | 
	| scoreRange | <code>array</code> |
	
generateScoreBorderColor
:   Border colors of interaction profile nodes based on score

	| Param | Type |
	| --- | --- |
	| score | <code>number</code> | 
	| scoreRange | <code>array</code> | 
	
getStyleByDMECount
:   Get the style of the background of galaxy panels based on number of DMEs

	| Param | Type |
	| --- | --- |
	| numDMEs | <code>number</code> | 
	| dmeRange | <code>array</code> | 
	
countDrugInteraction
:   Count number of drugs and interactions after applying filter

	| Param | Type |
	| --- | --- |
	| rules | <code>array</code> | 
	| filter | <code>string</code> | 
	| minScore | <code>number</code> | 
	| maxScore | <code>number</code> |

## Functions

visibilityFilter(state, action)
:   Select the filtering method in overview

	| Param | Type | Default |
	| --- | --- | --- |
	| state | <code>string</code> | <code>&quot;all&quot;</code> | 
	| action | <code>\*</code> |  | -->

