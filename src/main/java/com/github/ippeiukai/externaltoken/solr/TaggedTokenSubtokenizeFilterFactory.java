/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.solr;

import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;

import com.github.ippeiukai.externaltoken.factorycore.TaggedTokenSubtokenizeFilterFactoryCore;
import com.github.ippeiukai.externaltoken.factorycore.TokenFilterFactoryCore;

public class TaggedTokenSubtokenizeFilterFactory extends
    BaseTokenFilterFactory {
  
  private TokenFilterFactoryCore factoryCore;
  
  @Override
  public void init(Map<String,String> args) {
    super.init(args);
    factoryCore = new TaggedTokenSubtokenizeFilterFactoryCore(
        new TokenFilterFactoryParameterAdapter(this));
  }
  
  @Override
  public TokenStream create(TokenStream input) {
    return factoryCore.create(input);
  }
}
