/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.lucene.analysis.Tokenizer;

import com.github.ippeiukai.externaltoken.lucene.analysis.PatternTokenizer;

public class PatternTokenizerFactoryCore extends TokenizerFactoryCoreBase {
  
  private Pattern pattern;
  private int group;
  private boolean outputEmptyToken = false;
  
  public PatternTokenizerFactoryCore(ParameterProvider setting) {
    super(setting);
    String patternStr = setting.get("pattern");
    if(patternStr == null || patternStr.isEmpty()){
      throw new IllegalArgumentException("no pattern");
    }
    try {
      pattern = Pattern.compile(patternStr);
    } catch(PatternSyntaxException e){
      throw new IllegalArgumentException(e);
    }
    group = setting.getInt("group", -1);
    if(setting.get("outputEmptyToken") != null){
      outputEmptyToken = Boolean.parseBoolean(setting.get("outputEmptyToken"));
    }
  }
  
  @Override
  public Tokenizer create(Reader input) {
    PatternTokenizer tokenizer;
    try{
      tokenizer = new PatternTokenizer(input, pattern, group);
    } catch(IOException e){
      throw new RuntimeException(e);
    }
    tokenizer.setOutputsEmptyToken(outputEmptyToken);
    return tokenizer;
  }
  
}
