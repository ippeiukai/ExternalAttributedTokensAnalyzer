/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;

public interface Subtokenizer {
  
  void init(TokenStream attributeSource);  
  boolean incrementSubtoken();
  void resetSubtokenization();
  
}