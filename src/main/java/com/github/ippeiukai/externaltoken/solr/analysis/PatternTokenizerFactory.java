/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.solr.analysis;

import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;

import com.github.ippeiukai.externaltoken.factorycore.PatternTokenizerFactoryCore;
import com.github.ippeiukai.externaltoken.factorycore.TokenizerFactoryCore;

public class PatternTokenizerFactory extends BaseTokenizerFactory {
  
  private TokenizerFactoryCore factoryCore;
  
  @Override
  public void init(Map<String,String> args) {
    super.init(args);
    factoryCore = new PatternTokenizerFactoryCore(new TokenizerFactoryParameterAdapter(this));
  }
  
  @Override
  public Tokenizer create(Reader input) {
    return factoryCore.create(input);
  }
  
}
