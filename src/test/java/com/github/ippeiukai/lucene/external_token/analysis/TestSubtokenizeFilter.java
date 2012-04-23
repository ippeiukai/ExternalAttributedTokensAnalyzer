/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.IncrementPositionFilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.IndexFlaggingFilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.LabelIndexFlaggingFilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.LabellingFilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.LimitNumberFilterSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.PatternSplittingSubtokenizer;
import com.github.ippeiukai.lucene.external_token.analysis.subtokenizers.ReorderingFilterSubtokenizer;

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
  public void testWithPatternSplittingAndLimitAndLabellingAndIndexAndIncrement() throws IOException {
    String input = "aabf--ooaabf-ooabfoob ab caaaa-aaaaab- xx";
    TokenStream ts = new MockTokenizer(new StringReader(input),
        MockTokenizer.WHITESPACE, false);
    ts = new PositionMultiplyingTokenFilter(ts, 3);
    Subtokenizer subtokenChain = new PatternSplittingSubtokenizer(Pattern.compile("-"));
    subtokenChain = new LimitNumberFilterSubtokenizer(subtokenChain, 3);
    subtokenChain = new LabellingFilterSubtokenizer(subtokenChain, "%s:%s",new String[]{"main"});
    subtokenChain = new IndexFlaggingFilterSubtokenizer(subtokenChain);
    subtokenChain = new IncrementPositionFilterSubtokenizer(subtokenChain);
    ts = new SubtokenizeFilter(ts, subtokenChain);
    String[] terms = {"main:aabf", "2:", "3:ooaabf",
        "main:ab", "main:caaaa", "2:aaaaab", "3:", "main:xx"};
    int[] positionIncrements = {3, 1, 1, 1, 3, 1, 1, 1};
    assertTokenStreamContents(ts, terms, positionIncrements);
  }
  
  @Test
  public void testWithPatternSplittingAndLabelIndexAndReorderingAndIncrement() throws IOException {
    String input = "third:aabf-first:-nolabel-second:ooabfoob second:ab first:caaaa-second:aaaaab- first:xx";
    TokenStream ts = new MockTokenizer(new StringReader(input),
        MockTokenizer.WHITESPACE, false);
    ts = new PositionMultiplyingTokenFilter(ts, 3);
    Subtokenizer subtokenChain = new PatternSplittingSubtokenizer(Pattern.compile("-"));
    subtokenChain = new LabelIndexFlaggingFilterSubtokenizer(subtokenChain,
        Pattern.compile("^(.*):"), 1, "first", "second", "third");
    subtokenChain = new ReorderingFilterSubtokenizer(subtokenChain);
    subtokenChain = new IncrementPositionFilterSubtokenizer(subtokenChain);
    ts = new SubtokenizeFilter(ts, subtokenChain);
    String[] terms = {"first:", "second:ooabfoob", "third:aabf","second:ab",
        "first:caaaa", "second:aaaaab", "first:xx"};
    int[] positionIncrements = {3, 1, 1, 2, 2, 1, 2};
    assertTokenStreamContents(ts, terms, positionIncrements);
  }
  
}
