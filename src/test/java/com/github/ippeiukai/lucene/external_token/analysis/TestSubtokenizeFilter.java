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

public class TestSubtokenizeFilter extends
    BaseTokenStreamTestCase {
  
  @Test
  public void testWithPatternSplittingSubtokenizer() throws IOException {
    String input = "aabf--ooaabf-ooabfoob ab caaaa-aaaaab- xx";
    TokenStream ts = new MockTokenizer(new StringReader(input),
        MockTokenizer.WHITESPACE, false);
    ts = new SubtokenizeFilter(ts, new PatternSplittingSubtokenizer(Pattern.compile("-")));
    String[] terms = {"aabf", "", "ooaabf", "ooabfoob", "ab", "caaaa",
        "aaaaab", "", "xx"};
    int[] positionIncrements = {1, 0, 0, 0, 1, 1, 0, 0, 1};
    assertTokenStreamContents(ts, terms, positionIncrements);
  }
  
  @Test
  public void testWithPatternSplittingAndLimitAndLabel() throws IOException {
    String input = "aabf--ooaabf-ooabfoob ab caaaa-aaaaab- xx";
    TokenStream ts = new MockTokenizer(new StringReader(input),
        MockTokenizer.WHITESPACE, false);
    SubtokenizeFilter.Subtokenizer subtokenChain = new PatternSplittingSubtokenizer(Pattern.compile("-"));
    subtokenChain = new LimitNumberSubtokenFilter(subtokenChain, 3);
    subtokenChain = new LabellingFilterSubtokenizer(subtokenChain, "%s:%s",new String[]{"main"});
    ts = new SubtokenizeFilter(ts, subtokenChain);
    String[] terms = {"main:aabf", "2:", "3:ooaabf",
        "main:ab", "main:caaaa", "2:aaaaab", "3:", "main:xx"};
    int[] positionIncrements = {1, 0, 0, 1, 1, 0, 0, 1};
    assertTokenStreamContents(ts, terms, positionIncrements);
  }
  
}
