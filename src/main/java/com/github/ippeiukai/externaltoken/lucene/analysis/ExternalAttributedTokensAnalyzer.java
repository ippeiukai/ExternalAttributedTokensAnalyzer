/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.ReusableAnalyzerBase;

/**
 * Default:<br>
 * tokenDelimiter: ASCII record separator<br>
 * attributeDelimiter: ASCII unit separator<br>
 * labelDelimiter: ":"<br>
 * No regex.
 */
public class ExternalAttributedTokensAnalyzer extends ReusableAnalyzerBase {
  
  public static final char RECORD_SEPARATOR = '\u001E';
  public static final char UNIT_SEPARATOR = '\u001F';
  
  private String labelDelimiter = ":";
  private String tokenDelimiter = String.valueOf(RECORD_SEPARATOR);
  private String attributeDelimiter = String.valueOf(UNIT_SEPARATOR);
  
  private final String[] labels;
  
  public ExternalAttributedTokensAnalyzer(String... labels) {
    this.labels = labels;
  }

  public void setTokenDelimiter(String tokenDelimiter) {
    this.tokenDelimiter = tokenDelimiter;
  }

  public void setAttributeDelimiter(String attributeDelimiter) {
    this.attributeDelimiter = attributeDelimiter;
  }
  
  public void setLabelDelimiter(String labelDelimiter) {
    this.labelDelimiter = labelDelimiter;
  }

  @Override
  protected TokenStreamComponents createComponents(String fieldName,
      Reader reader) {
    try {
      final PatternTokenizer pt = new PatternTokenizer(reader,
          Pattern.compile(Pattern.quote(tokenDelimiter)), -1);
      pt.setOutputsEmptyToken(true);
      final TaggedTokenSubtokenizeFilter ts = new TaggedTokenSubtokenizeFilter(
          pt, attributeDelimiter, labelDelimiter, labels);
      return new TokenStreamComponents(pt, ts);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
