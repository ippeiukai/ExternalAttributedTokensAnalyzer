package com.github.ippeiukai.externaltoken.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.ReusableAnalyzerBase;

/**
 * 
 * subtokenDelimiter:"/"
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
