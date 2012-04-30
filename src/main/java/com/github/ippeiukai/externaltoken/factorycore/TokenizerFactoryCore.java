/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;

public interface TokenizerFactoryCore {
  
  public Tokenizer create(Reader input);
  
}