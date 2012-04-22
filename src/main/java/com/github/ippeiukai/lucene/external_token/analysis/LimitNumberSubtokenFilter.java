package com.github.ippeiukai.lucene.external_token.analysis;

import com.github.ippeiukai.lucene.external_token.analysis.SubtokenizeFilter.FilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.SubtokenizeFilter.Subtokenizer;

public class LimitNumberSubtokenFilter extends FilterSubtokenizer {
  
  private final int maxCount;
  private int count = 0;
  
  public LimitNumberSubtokenFilter(Subtokenizer inner, int maxSubtokenCount) {
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
