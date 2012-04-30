/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public class PositionMultiplyingTokenFilter extends TokenFilter {
  
  private final int factor;
  private PositionIncrementAttribute posIncrAttr;
  
  public PositionMultiplyingTokenFilter(TokenStream input, int factor) {
    super(input);
    assert (0 <= factor);
    this.factor = factor;
    posIncrAttr = addAttribute(PositionIncrementAttribute.class);
  }
  
  @Override
  public final boolean incrementToken() throws IOException {
    if(!input.incrementToken()){
      return false;
    }
    posIncrAttr.setPositionIncrement(posIncrAttr.getPositionIncrement() * factor);
    return true;
  }
  
  
}
