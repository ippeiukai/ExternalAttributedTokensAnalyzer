/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.io.IOException;
import java.util.Formatter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public abstract class SubtokenizeFilter extends TokenFilter {
  
  private enum NextAction {
    INCREMENT_INPUT, SUBTOKENS, EXHAUSTED
  }
  
  private NextAction next;
  
  protected CharTermAttribute termAttr;
  private PositionIncrementAttribute posIncrAttr;
  
  private int subtokenCount;
  private int maxNumSubtoken = Integer.MAX_VALUE;
  private String labelFormat = null;

  /**
   * @param input
   */
  public SubtokenizeFilter(TokenStream input) {
    super(input);
    next = NextAction.INCREMENT_INPUT;
    termAttr = getAttribute(CharTermAttribute.class);
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
          resetSubtokenization();
          subtokenCount = 0;
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

  private final StringBuilder buffer = new StringBuilder();
  private final Formatter bufferFormatter = new Formatter(buffer);
  
  private boolean nextSubtoken() {
    if (!incrementSubtoken()) return false;
    if (maxNumSubtoken < ++subtokenCount) return false;
    if (labelFormat != null) {
      buffer.delete(0, buffer.length());
      bufferFormatter.format(labelFormat, subtokenCount - 1, termAttr).flush();
      termAttr.setEmpty().append(buffer);
    }
    return true;
  }
  
  @Override
  public void reset() throws IOException {
    super.reset();
    next = NextAction.INCREMENT_INPUT;
  }
  
  /**
   * default is Integer.MAX_VALUE
   */
  public SubtokenizeFilter setMaxNumSubtoken(int maxNumSubtoken) {
    this.maxNumSubtoken = maxNumSubtoken;
    return this;
  }

  /**
   * @param format use %1$d for index, %2$s for term. 
   */
  public SubtokenizeFilter setEnableIndexMarking(String format) {
    labelFormat = format;
    return this;
  }

  protected abstract boolean incrementSubtoken();
  
  protected abstract void resetSubtokenization();
}
