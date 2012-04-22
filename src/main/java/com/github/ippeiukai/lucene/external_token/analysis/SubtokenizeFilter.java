/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public class SubtokenizeFilter extends TokenFilter {
  
  private enum NextAction {
    INCREMENT_INPUT, SUBTOKENS, EXHAUSTED
  }
  private NextAction next;
  
  protected CharTermAttribute termAttr;
  private PositionIncrementAttribute posIncrAttr;
  private final Subtokenizer subtokenizer;

  private AttributeSource.State preSubtokenizeState;

  /**
   * @param input
   */
  public SubtokenizeFilter(TokenStream input, Subtokenizer subtokenizer) {
    super(input);
    next = NextAction.INCREMENT_INPUT;
    termAttr = getAttribute(CharTermAttribute.class);
    if (hasAttribute(PositionIncrementAttribute.class)) {
      posIncrAttr = getAttribute(PositionIncrementAttribute.class);
    } else {
      posIncrAttr = addAttribute(PositionIncrementAttribute.class);
    }
    subtokenizer.init(this);
    this.subtokenizer = subtokenizer;
  }

  @Override
  public boolean incrementToken() throws IOException {
    switch (next) {
      case SUBTOKENS:
        if (nextSubtoken()) {
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
          preSubtokenizeState = captureState();
          subtokenizer.resetSubtokenization();
          if (nextSubtoken()) {
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
  
  private boolean nextSubtoken() {
    clearAttributes();
    restoreState(preSubtokenizeState);
    return subtokenizer.incrementSubtoken();
  }
  
  @Override
  public void reset() throws IOException {
    super.reset();
    next = NextAction.INCREMENT_INPUT;
  }
  
  //-------------
  
  public static interface Subtokenizer {
    void init(TokenStream attributeSource);  
    boolean incrementSubtoken();
    void resetSubtokenization();
  }
  
  public static class FilterSubtokenizer implements Subtokenizer {
    protected Subtokenizer inner;
    
    public FilterSubtokenizer(Subtokenizer inner) {
      this.inner = inner;
    }

    @Override
    public void init(TokenStream attributeSource) {
      inner.init(attributeSource);
    }

    @Override
    public boolean incrementSubtoken() {
      return inner.incrementSubtoken();
    }

    @Override
    public void resetSubtokenization() {
      inner.resetSubtokenization();
    }
  }
  
}
