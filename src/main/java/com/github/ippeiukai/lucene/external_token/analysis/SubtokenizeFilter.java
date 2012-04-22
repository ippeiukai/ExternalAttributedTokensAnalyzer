/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public abstract class SubtokenizeFilter extends TokenFilter {
  
  private enum NextAction {
    INCREMENT_INPUT, SUBTOKENS, EXHAUSTED
  }
  
  private NextAction next;
  private PositionIncrementAttribute posIncrAttr;
  
  /**
   * @param input
   */
  public SubtokenizeFilter(TokenStream input) {
    super(input);
    next = NextAction.INCREMENT_INPUT;
    if (hasAttribute(PositionIncrementAttribute.class)) {
      posIncrAttr = getAttribute(PositionIncrementAttribute.class);
    } else {
      posIncrAttr = addAttribute(PositionIncrementAttribute.class);
    }
  }
  
  @Override
  public boolean incrementToken() throws IOException {
    switch (next) {
      case SUBTOKENS:
        if (incrementSubtoken()) {
          posIncrAttr.setPositionIncrement(0);
          return true; // subsequent subtokens
        }
      case INCREMENT_INPUT:
        int skippedPosIncr = 0;
        while (input.incrementToken()) {
          if (skippedPosIncr != 0) {
            posIncrAttr.setPositionIncrement(posIncrAttr.getPositionIncrement()
                + skippedPosIncr);
          }
          resetSubtokenization();
          if (incrementSubtoken()) {
            next = NextAction.SUBTOKENS;
            return true; // first subtoken
          }
          skippedPosIncr = posIncrAttr.getPositionIncrement();
        }
        next = NextAction.EXHAUSTED;
      default: // EXHAUSTED
        return false;
    }
  }
  
  @Override
  public void reset() throws IOException {
    super.reset();
    next = NextAction.INCREMENT_INPUT;
  }
  
  protected abstract boolean incrementSubtoken();
  
  protected abstract void resetSubtokenization();
}
