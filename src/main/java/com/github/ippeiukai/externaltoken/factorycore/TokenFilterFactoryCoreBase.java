/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import org.apache.lucene.analysis.TokenStream;

/**
 * Provides TokenFilterFactory functionality common to Solr and ElasticSearch.
 */
public abstract class TokenFilterFactoryCoreBase implements TokenFilterFactoryCore {
  
  public TokenFilterFactoryCoreBase(ParameterProvider setting) {
    // nothing to do
  }

  @Override
  public abstract TokenStream create(TokenStream input);
  
}
