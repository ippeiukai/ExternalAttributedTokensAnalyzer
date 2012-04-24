/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers;

import org.apache.lucene.analysis.TokenStream;

import com.github.ippeiukai.externaltoken.lucene.analysis.Subtokenizer;

public class FilterSubtokenizer implements Subtokenizer {
  protected Subtokenizer inner;
  
  public FilterSubtokenizer(Subtokenizer inner) {
    this.inner = inner;
  }

  @Override
  public void init(TokenStream attributeSource) {
    inner.init(attributeSource);
  }

  @Override
  public boolean incrementSubtoken() {
    return inner.incrementSubtoken();
  }

  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
  }
}