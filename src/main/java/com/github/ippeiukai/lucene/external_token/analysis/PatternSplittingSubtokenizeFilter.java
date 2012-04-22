/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.AttributeSource;

public class PatternSplittingSubtokenizeFilter extends SubtokenizeFilter {
  
  private AttributeSource.State state;
  private Matcher matcher;
  private CharTermAttribute termAttr;
  private static final int EXHAUSTED = -1;
  private int index = EXHAUSTED;
  
  /**
   * @param input
   * @param pattern
   */
  public PatternSplittingSubtokenizeFilter(TokenStream input, Pattern pattern) {
    super(input);
    matcher = pattern.matcher("");
    termAttr = getAttribute(CharTermAttribute.class);
  }
  
  @Override
  protected void resetSubtokenization() {
    state = captureState();
    matcher.reset(termAttr);
    index = 0;
  }
  
  private final CharTermAttributeImpl buffer = new CharTermAttributeImpl();
  
  @Override
  protected boolean incrementSubtoken() {
    if (index == EXHAUSTED) {
      return false;
    }
    clearAttributes();
    restoreState(state);
    final int nextStart, end;
    if (matcher.find()) {
      end = matcher.start();
      nextStart = matcher.end();
    } else {
      end = termAttr.length();
      nextStart = EXHAUSTED;
    }
    buffer.setEmpty().append(termAttr, index, end);
    termAttr.setEmpty().append(buffer, 0, end - index);
    index = nextStart;
    return true;
  }
  
}
