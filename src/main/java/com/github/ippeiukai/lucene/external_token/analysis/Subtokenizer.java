/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import org.apache.lucene.analysis.TokenStream;

public interface Subtokenizer {
  
  void init(TokenStream attributeSource);  
  boolean incrementSubtoken();
  void resetSubtokenization();
  
}