/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers;

import com.github.ippeiukai.externaltoken.lucene.analysis.Subtokenizer;

public class LimitNumberFilterSubtokenizer extends FilterSubtokenizer {
  
  private final int maxCount;
  private int count = 0;
  
  public LimitNumberFilterSubtokenizer(Subtokenizer inner, int maxSubtokenCount) {
    super(inner);
    maxCount = maxSubtokenCount;
  }
  
  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
    count = 0;
  }
  
  @Override
  public boolean incrementSubtoken() {
    if (!inner.incrementSubtoken()) {
      return false;
    }
    if (maxCount <= count) {
      return false;
    }
    count++;
    return true;
  }
  
}
