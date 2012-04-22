/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;

public class PatternSplittingSubtokenizeFilter extends SubtokenizeFilter {
  
  private AttributeSource.State state;
  private Matcher matcher;
  private static final int EXHAUSTED = -1;
  private int index = EXHAUSTED;
  
  /**
   * @param input
   * @param pattern
   */
  public PatternSplittingSubtokenizeFilter(TokenStream input, Pattern pattern) {
    super(input);
    matcher = pattern.matcher("");
  }
  
  @Override
  protected void resetSubtokenization() {
    state = captureState();
    matcher.reset(termAttr);
    index = 0;
  }
  
  private final StringBuilder buffer = new StringBuilder();
  
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
    buffer.delete(0, buffer.length()).append(termAttr, index, end);
    termAttr.setEmpty().append(buffer);
    index = nextStart;
    return true;
  }
  
}
