/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;

/**
 * Provides TokenFilterFactory functionality common to Solr and ElasticSearch.
 */
public abstract class TokenizerFactoryCoreBase implements TokenizerFactoryCore {
  
  public TokenizerFactoryCoreBase(ParameterProvider setting) {
    // nothing to do
  }

  @Override
  public abstract Tokenizer create(Reader input);
  
}
