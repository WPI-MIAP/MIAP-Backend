---
title: Review for Approximate Drug Name Matching
author: Andrew Schade
date: 08 November 2017
references:
- id: du2005approximate
  type: article-journal
  author:
  - family: Du
    given: Mengmeng
  issued:
  - year: '2005'
  title: Approximate name matching
  container-title: 'NADA, Numerisk Analys och Datalogi, KTH, Kungliga Tekniska H&ouml;gskolan.
    Stockholm: un'
  page: '3-15'
- id: allison99edit
  type: webpage
  author:
  - family: Allison
    given: Lloyd
  issued:
  - year: 1999
  title: 'Dynamic Programming Algorithm (DPA) for Edit-Distance'
  URL: 'http://users.monash.edu/~lloyd/tildeAlgDS/Dynamic/Edit/'


---

When parsing the input data in order begin preprocessing it for
mining in our MARAS tool, we found that we had many similar drug
names in our input fields, where a human would often be able to
recognize as belonging to the same drug. However, in these situations
the association-rule mining algorithms would end up treating them
as different drugs, as their names wouldn't pass na&iuml;ve equality
checks. In order to accomplish this, our algorithms must consider
a 'fuzzy' notion of equality.

This notion is often used in name lookups for people---as it is easy
to misspell someone's name, especially in international settings. In
their master's thesis, @du2005approximate, explored the effectiveness
of various algorithms for matching similar personal names. They found
that for language independent name matching, algorithms based on
**edit distance**^[See [Terms](#terms)] gave the most effective results.

However, intuitively, this method might not be as effective for our
dataset. We want to combine drug names that are often 

@du2005approximate found that there was a tradeoff between speed
and maximizing the number of relevant matches. They found that using
a Bloom Filter and reverse edit distance would minimize the number of
irrelevant matches while running faster, while using edit distance 
with a trie data structure and constant first letter would maximize the
number of relevant matches at the expense of speed.

This sounds like it would work pretty well for our usage. While we are
unlikely to encounter misspellings, the data we get from FAERS can often
contain extra information, like the active ingredient in addition to the
name, the dosage information, or even just a repeat of the drug names
themselves.

However, in order to have anything to match, we need to have a database
of normalized drug names---a set of drugs that we match everything back
to. The main difficulty here is developing a vocabulary of drugs that
is sufficiently large and will almost always contain the drug that was
meant in the report. For this purpose, we are using [Insert Dataset citation].

The normailization process would start when creating the dataset for use.
We strip the whitespace from the drug name strings (only for the purposes
of matching), and ensure that all characters in the string are lowercase,
before proceeding with the matching algorithm. For an example of how this
works, consider the following (much abridged) vocabulary:

* `vicodin`
* `zamicet`
* `ibuprofen`
* `theophylline`

If we are attempting to match `IBUPROFEN 500mg`, then the process would be to
first perform the same normalization step as we did with the strings in the
dataset: removing whitespace and converting to lowercase. Thus,
`IBUPROFEN 500mg` becomes `ibuprofen500mg`. Then we calculate the edit distance
for each of the strings in our vocabulary. 

At first, we considered pure Levenschtein Distance, which is an edit distance
involving only the substitution, insertion or deletion of single letters.
We modified the cost of substitution to be the same as an insertion followed by
a deletion, otherwise all words of the same length and no overlapping letters
would be just as far away as a word that contains every letter in the source
string, but is twice as long.

The algorithm we used to test this distance was implemented in JAVA by
@allison99edit, for example if we run `ibuprofen500mg`, we get edit distances
of:

* `ibuprofen500mg -> vicodin`: 15
* `ibuprofen500mg -> zamicet`: 17
* `ibuprofen500mg -> ibuprofen`: 5
* `ibuprofen500mg -> theophylline`: 22

We also considered a second distance metric: Damerau-Levenschtein distance,
which adds the ability to swap adjacent characters. This operation needs to
have a cost of at least half the sum of the costs of addition and deletion for
the algorithm we found, implemented by Kevin Stern in 2014. We chose
a cost of 1.2 as an initial test. For this metric,
we get edit distances of:

* `ibuprofen500mg -> vicodin`: 15
* `ibuprofen500mg -> zamicet`: 17
* `ibuprofen500mg -> ibuprofen`: 5
* `ibuprofen500mg -> theophylline`: 19.4

In our example vocabulary, both algorithms come up with `ibuprofen` as the best
match by far, which is good.

# Terms

This section is for creating a formalism for any terms defined more loosely
above.

Edit Distance
:   Given two strings $a$ and $b$ on an alphabet $\Sigma$ (for
    example, the set of ASCII characters), the edit distance
    $\mathsf{d}(a,b)$ is the minimum-weight series of edit operations
    that transforms $a$ into $b$.
:   Less formally, edit distance is the number of simple edits required
    to transform one string into another. The different operation 
    classified as simple edits are the substitution of a letter, deletion
    of a letter, insertion of a letter, and the transposition of two 
    adjacent letters (@du2005approximate pp. 5).

    The less the edit distance between two strings is, the more similar
    each string is to the other.

# Dataset

The dataset portion of this is almost more important than the name-matching
side. In order for accurate sorting of the imperfect names in the ADR reports
we need to have a dataset that includes almost all named drugs without having
too many overlapping names. Unfortunately, with [Insert dataset citiation here]
there were many overlapping names in even the smallest dataset provided, as the
dataset provides some mapping functionality between different formattings for
each drug.

Thus, we had to further clean the dataset in order to make full use of it for
drug name matching. For the first iteration of dataset cleaning, we used
a script to create a JSON datafile that contained only drug names that didn't
have another dataset entry with a name that was a substring of it's name.

For example, if there was `injectablediazepam` as well as `diazepam`, we'd only
want to include `diazepam`. To accomplish this the following python loop
was executed for every new drug name read into the script.

```python
flag = True
for drug_id in output_dict.keys():
    drug = output_dict[drug_id]
    if name in drug:
        flag = False
        break;
    if drug in name:
        output_dict.pop(drug_id)
if flag:
    output_dict[nId] = name

```

This is just a naive check to see if our dictionary of drugs (and id's) either
contains another drug whose name is fully contained within the current drug's
name---which means we ignore this new drug---or if drugs we already added to
the dictionary fully contain the current drug name, whereupon we remove those
drugs from the dictionary and keep checking more drugs.

Of course, this makes parsing the dataset a very computationally expensive
process, being $O(n^2)$ and having to process more than 200,000 lines. In
initial testing, this takes almost an hour. Fortunately, once the dataset is satisfactorily cleaned, the drug list will not have to be recalculated for
quite a while.

Unfortunately, this didn't do quite enough to remove redundancies in the
dataset. This algorithm left overly-specific entries such as the following:

```
1003676: 'zerit15mgoralcapsule',
1003680: 'zerit20mgoralcapsule',
1003684: 'zerit30mgoralcapsule',
1003688: 'zerit40mgoralcapsule'
```

A human user would reduce such drugs to `'zerit'`, leaving off the dosage
information, but since there wasn't necessarily an entry in the dataset
merely saying `'zerit'`, the entries were never shortened.

Thus, we worked on creating a dictionary of strings that could be (mostly)
safely ignored when parsing the drug names. This included words like
`oral`, and `injectable`, but also numbers, units, or body part descriptors
such as `vaginal`, `subcutaneous`. Because we aggressively removed matching
character sequences from our potential drug names, we had to be sure that
the patterns we were using wouldn't match unintended words. For example, without special treatment, the pattern `in` would match the final two
characters in the word `protein`, which was undesirable. Thus, we made the
pattern `' in '`, which would only match with whole words.

Additionally, drugs would often be of the form:

```
3858048: 'd4t30mgoralcapsule[zerit]',
3858049: 'dht30mgoralcapsule[zerit]',
3858259: 'd4t20mgoralcapsule[zerit]',
3858260: 'dht20mgoralcapsule[zerit]'
```

This is an easy special case, since we can just find the string inside
the square brackets and reduce our result to `zerit`, which is our goal.

