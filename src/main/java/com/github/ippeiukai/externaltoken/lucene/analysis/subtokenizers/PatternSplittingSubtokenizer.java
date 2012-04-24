/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.github.ippeiukai.externaltoken.lucene.analysis.Subtokenizer;


public class PatternSplittingSubtokenizer implements Subtokenizer {
  
  private Matcher matcher;
  
  public PatternSplittingSubtokenizer(Pattern pattern) {
    matcher = pattern.matcher("");
  }
  
  private static final int EXHAUSTED = -1;
  private int index = EXHAUSTED;
  private CharTermAttribute termAttr;
  
  @Override
  public void init(TokenStream attributeSource) {
    termAttr = attributeSource.addAttribute(CharTermAttribute.class);
  }
  
  @Override
  public void resetSubtokenization() {
    buffer.delete(0, buffer.length()).append(termAttr);
    matcher.reset(buffer);
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
      end = buffer.length();
      nextStart = EXHAUSTED;
    }
    System.out.println(buffer);
    System.out.println(index);
    System.out.println(end);
    System.out.println(nextStart);
    termAttr.setEmpty().append(buffer, index, end);
    index = nextStart;
    return true;
  }
  
}
