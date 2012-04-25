/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public class SubtokenizeFilter extends TokenFilter {
  
  private enum NextAction {
    INCREMENT_INPUT, SUBTOKENS, EXHAUSTED
  }
  private NextAction next;
  
  protected final CharTermAttribute termAttr;
  private final PositionIncrementAttribute posIncrAttr;
  private final Subtokenizer subtokenizer;

  private State preSubtokenizeState;

  public SubtokenizeFilter(TokenStream input, SubtokenizerFactory subtokenizerFactory) {
    super(input);
    next = NextAction.INCREMENT_INPUT;
    termAttr = addAttribute(CharTermAttribute.class);
    posIncrAttr = addAttribute(PositionIncrementAttribute.class);    
    subtokenizer = subtokenizerFactory.subtokenizer();
    subtokenizer.init(this);
  }
  
  public SubtokenizeFilter(TokenStream input, final Subtokenizer subtokenizer) {
    this(input, new SubtokenizerFactory() {
      @Override
      public Subtokenizer subtokenizer() {
        return subtokenizer;
      }
    });
  }

  @Override
  public boolean incrementToken() throws IOException {
    switch (next) {
      case SUBTOKENS:
        if (nextSubtoken(0)) {
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

    System.out.println(termAttr);
    return subtokenizer.incrementSubtoken();
  }
  
  private boolean nextSubtoken(int positionIncrement) {
    clearAttributes();
    restoreState(preSubtokenizeState);

    System.out.println(termAttr);
    posIncrAttr.setPositionIncrement(positionIncrement);
    return subtokenizer.incrementSubtoken();
  }
  
  @Override
  public void reset() throws IOException {
    super.reset();
    subtokenizer.init(this);
    next = NextAction.INCREMENT_INPUT;
  }
  
}
