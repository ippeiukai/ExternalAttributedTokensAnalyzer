/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;

import com.github.ippeiukai.externaltoken.lucene.analysis.Subtokenizer;


public class LabelIndexFlaggingFilterSubtokenizer extends
    FilterSubtokenizer {
  
  private final Matcher matcher;
  private final int group;
  private final Map<String, Integer> labelsIndex;
  private final int defaultLabelIndex;
  private static final int IGNORED_INDEX = -1;
  
  private FlagsAttribute flagsAttr;
  private CharTermAttribute termAttr;

  public LabelIndexFlaggingFilterSubtokenizer(Subtokenizer subtokenizer,
      Pattern pattern, String... labels) {
    this(subtokenizer, pattern, 0, labels, IGNORED_INDEX);
  }
  
  public LabelIndexFlaggingFilterSubtokenizer(Subtokenizer subtokenizer,
      Pattern pattern, int group, String... labels) {
    this(subtokenizer, pattern, group, labels, IGNORED_INDEX);
  }
  
  public LabelIndexFlaggingFilterSubtokenizer(Subtokenizer subtokenizer,
      Pattern pattern, int group, String[] labels, int defaultLabelIndex) {
    super(subtokenizer);
    this.matcher = pattern.matcher("");
    this.group = group;
    labelsIndex = new HashMap<String,Integer>(labels.length * 2);
    for (int index = labels.length - 1; index >= 0; --index) {
      if (labels[index] == null) continue;
      labelsIndex.put(labels[index], index);
    }
    this.defaultLabelIndex = defaultLabelIndex;
  }
  
  @Override
  public void init(TokenStream attributeSource) {
    super.init(attributeSource);
    flagsAttr = attributeSource.addAttribute(FlagsAttribute.class);
    termAttr = attributeSource.addAttribute(CharTermAttribute.class);
  }
  
  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
  }
  
  @Override
  public boolean incrementSubtoken() {
    while (inner.incrementSubtoken()) {
      final int index = determineIndexFromLabel();
      if (index == IGNORED_INDEX) {
        continue;
      }
      flagsAttr.setFlags(index);
      return true;
    }
    return false;
  }
  
  private int determineIndexFromLabel(){
    matcher.reset(termAttr);
    if (!matcher.find()){
      return defaultLabelIndex;
    }
    if(group > matcher.groupCount()){
      return defaultLabelIndex;
    }
    final String label = matcher.group(group);
    if (!labelsIndex.containsKey(label)){
      return defaultLabelIndex;
    }
    return labelsIndex.get(label);
  }
  
}
