/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis.subtokenizers;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;

import com.github.ippeiukai.lucene.external_token.analysis.Subtokenizer;


public class IndexFlaggingFilterSubtokenizer extends
    FilterSubtokenizer {
  
  public IndexFlaggingFilterSubtokenizer(
      Subtokenizer subtokenizer) {
    super(subtokenizer);
  }
  
  private FlagsAttribute flagsAttr;
  
  @Override
  public void init(TokenStream attributeSource) {
    super.init(attributeSource);
    flagsAttr = attributeSource.addAttribute(FlagsAttribute.class);
  }
  
  private int index = 0;
  
  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
    index = 0;
  }
  
  @Override
  public boolean incrementSubtoken() {
    if (!inner.incrementSubtoken()) {
      return false;
    }
    flagsAttr.setFlags(index++);
    return true;
  }
  
}
