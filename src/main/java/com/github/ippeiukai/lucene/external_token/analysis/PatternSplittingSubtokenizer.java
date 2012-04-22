/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class PatternSplittingSubtokenizer implements SubtokenizeFilter.Subtokenizer {
  
  private Matcher matcher;
  
  public PatternSplittingSubtokenizer(Pattern pattern) {
    matcher = pattern.matcher("");
  }
  
  private static final int EXHAUSTED = -1;
  private int index = EXHAUSTED;
  private CharTermAttribute termAttr;
  
  @Override
  public void init(TokenStream attributeSource) {
    termAttr = attributeSource.getAttribute(CharTermAttribute.class);
  }
  
  @Override
  public void resetSubtokenization() {
    matcher.reset(termAttr);
    buffer.delete(0, buffer.length());
    index = 0;
  }
  
  private final StringBuilder buffer = new StringBuilder();
  
  @Override
  public boolean incrementSubtoken() {
    if (index == EXHAUSTED) {
      return false;
    }
    final int nextStart, end;
    if (matcher.find()) {
      end = matcher.start();
      nextStart = matcher.end();
    } else {
      end = termAttr.length();
      nextStart = EXHAUSTED;
    }
    buffer.delete(0, buffer.length()).append(termAttr, index, end);
    termAttr.setEmpty().append(buffer);
    index = nextStart;
    return true;
  }
  
}
