/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

public class TestPatternSplittingSubtokenizeFilter extends
    BaseTokenStreamTestCase {
  
  @Test
  public void testSplit() throws IOException {
    String input = "aabf--ooaabf-ooabfoob ab caaaa-aaaaab- xx";
    TokenStream ts = new MockTokenizer(new StringReader(input),
        MockTokenizer.WHITESPACE, false);
    ts = new PatternSplittingSubtokenizeFilter(ts, Pattern.compile(Matcher
        .quoteReplacement("-")));
    assertTokenStreamContents(ts, new String[] {"aabf", "", "ooaabf",
        "ooabfoob", "ab", "caaaa", "aaaaab", "", "xx"}, new int[] {1, 0, 0, 0,
        1, 1, 0, 0, 1});
  }
  
}
