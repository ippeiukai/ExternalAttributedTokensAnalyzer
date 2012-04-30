/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.lucene.analysis.TokenStream;

import com.github.ippeiukai.externaltoken.lucene.analysis.TaggedTokenSubtokenizeFilter;

public class TaggedTokenSubtokenizeFilterFactoryCore extends TokenFilterFactoryCoreBase {
  
  private static enum Mode{
    ORDERED, LABELLED
  }
  
  private Mode mode;
  private Pattern subtokenDelimiter;
  private String[] labels;
  private int numMaxTags;
  private Pattern labelPattern;
  private int labelPatternGroup;
  
  public TaggedTokenSubtokenizeFilterFactoryCore(ParameterProvider setting) {
    super(setting);
    String modeStr = setting.get("mode");
    String subtokenDelimiterStr = setting.get("subtokenDelimiter");
    String labelsStr = setting.get("labels");
    numMaxTags = setting.getInt("numMaxTags", -1);
    String labelDelimiter = setting.get("labelDelimiter");
    
    if(modeStr == null){
      throw new IllegalArgumentException("no mode");
    }
    if (modeStr.compareToIgnoreCase("ordered") == 0 ){
      mode = Mode.ORDERED;
    }else if(modeStr.compareToIgnoreCase("labelled") == 0){
      mode = Mode.LABELLED;
    }else{
      throw new IllegalArgumentException("unknown mode");
    }
    
    if(subtokenDelimiterStr == null){
      throw new IllegalArgumentException("no tokenDelimiter");
    }
    try {
      subtokenDelimiter = Pattern.compile(subtokenDelimiterStr);
    } catch (PatternSyntaxException e) {
      throw new IllegalArgumentException(e);
    }
    
    if(labelsStr == null){
      throw new IllegalArgumentException("no labels");
    }
    labels = labelsStr.split(",\\s*");
    
    switch(mode){
      case ORDERED:
        if(numMaxTags < 1){
          throw new IllegalArgumentException("no numMaxTags");
        }
        break;
      case LABELLED:
        if(labelDelimiter == null){
          throw new IllegalArgumentException("no labelDelimiter");
        }
        try{
          labelPattern = Pattern.compile(String.format("(.*)%s", labelDelimiter));
        }catch(PatternSyntaxException e){
          throw new IllegalArgumentException(e);
        }
        labelPatternGroup = 1;
        break;
    }
  }
  
  @Override
  public TokenStream create(TokenStream input) {
    switch(mode){
      case ORDERED:
        return new TaggedTokenSubtokenizeFilter(input, subtokenDelimiter, numMaxTags, labels);
      case LABELLED:
        return new TaggedTokenSubtokenizeFilter(input, subtokenDelimiter,
            labelPattern, labelPatternGroup, labels);
    }
    throw new Error("We have a bug!");
  }
  
}
