/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis.subtokenizers;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import com.github.ippeiukai.lucene.external_token.analysis.Subtokenizer;


public class IncrementPositionFilterSubtokenizer extends FilterSubtokenizer {
  
  public IncrementPositionFilterSubtokenizer(Subtokenizer inner){
    super(inner);
  }
  
  private int previousIndex;
  private FlagsAttribute flagsAttr;
  private PositionIncrementAttribute posIncrAttr;

  private int basePositioinIncr;
  
  @Override
  public void init(TokenStream attributeSource) {
    super.init(attributeSource);
    flagsAttr = attributeSource.addAttribute(FlagsAttribute.class);
    posIncrAttr = attributeSource.addAttribute(PositionIncrementAttribute.class);
    previousIndex = 0;
  }
  
  @Override
  public void resetSubtokenization() {
    super.resetSubtokenization();
    basePositioinIncr = posIncrAttr.getPositionIncrement();
    if (basePositioinIncr == 0) {
      throw new RuntimeException(
          "Synonyms (tokens with 0 position increment) are not allowed with this implementation.");
    }
  }
  
  @Override
  public boolean incrementSubtoken() {
    if (!super.incrementSubtoken()) {
      return false;
    }
    final int index = flagsAttr.getFlags();
    int newPosition = basePositioinIncr + index - previousIndex;
    if (newPosition < 0) {
      throw new RuntimeException(
          "Something went wrong. Perhaps the previous subtoken's index was too big: "
              + previousIndex
              + " Or this subtoken's index was smaller than the previous:"
              + index);
    }
    posIncrAttr.setPositionIncrement(basePositioinIncr + index - previousIndex);
    basePositioinIncr = 0;
    previousIndex = index;
    return true;
  }
  
}
