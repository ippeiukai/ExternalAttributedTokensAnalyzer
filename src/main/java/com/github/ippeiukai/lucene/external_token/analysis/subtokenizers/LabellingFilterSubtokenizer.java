/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis.subtokenizers;

import java.util.Formatter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.github.ippeiukai.lucene.external_token.analysis.Subtokenizer;

/**
 * format will be used with the subtoken count or the label and term.
 */
public class LabellingFilterSubtokenizer extends FilterSubtokenizer {
  
  private final String[] labels;
  private final String format;
  private final StringBuilder buffer = new StringBuilder();
  private Formatter bufferFormatter = new Formatter(buffer);
  
  /**
   * @param format
   *          example: "%d: %s"
   */
  public LabellingFilterSubtokenizer(Subtokenizer inner, String format) {
    super(inner);
    this.format = format;
    this.labels = null;
  }

  /**
   * @param labels
   *          each label must be unique. example: ["first","second","third"]
   * @param format
   *          example: "%s: %s"
   */
  public LabellingFilterSubtokenizer(Subtokenizer inner, String format, String... labels) {
    super(inner);
    this.format = format;
    this.labels = labels;
  }
  
  private CharTermAttribute termAttr = null;
  private int count = 0;
  
  @Override
  public void init(TokenStream attributeSource) {
    inner.init(attributeSource);
    termAttr = attributeSource.addAttribute(CharTermAttribute.class);
    count = 0;
  }
  
  @Override
  public boolean incrementSubtoken() {
    if(!inner.incrementSubtoken()){
      return false;
    }
    ++count;
    buffer.delete(0, buffer.length());
    if(labels != null && count - 1 < labels.length) {
      bufferFormatter.format(format, labels[count - 1], termAttr).flush();
    } else {
      bufferFormatter.format(format, count, termAttr).flush();
    }
    termAttr.setEmpty().append(buffer);
    return true;
  }
  
  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
    count = 0;
  }
  
}
