package org.maras.framework.mining;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import org.maras.framework.RuleSets;
import org.maras.framework.InteractionSets;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class extends the AlgoAgrawalFaster94 class from the SPMF library put
 * together by Philippe Fournier-Viger.
 * The extension of this association rule mining algorithm is used to run the
 * algorithm using InteractionSets and
 * producing RuleSets.
 *
 * @see AlgoAgrawalFaster94
 * @see InteractionSets
 * @see RuleSets
 * @author Brian Zylich
 */
public class Agrawal extends AlgoAgrawalFaster94 {

  /**
       * Constructor
   */
  public Agrawal() { super(); }

  /**
       * Run the algorithm
       * @param interactions  a set of frequent itemsets
       * @param databaseSize  the number of transactions in the database
       * @param minconf  the minconf threshold
       * @return  the set of association rules
       */
  public RuleSets runAlgorithm(InteractionSets interactions, int databaseSize,
                               double minconf) {
    AssocRules rules;
    try {
      rules = super.runAlgorithm(interactions, null, databaseSize, minconf);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return new RuleSets("Association Rules", rules);
  }
}
