/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import org.apache.lucene.analysis.TokenStream;

public interface TokenFilterFactoryCore {
  
  public TokenStream create(TokenStream input);
  
}