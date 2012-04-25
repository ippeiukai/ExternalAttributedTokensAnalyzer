/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.lucene.analysis;

import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;

import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.IncrementPositionFilterSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.IndexFlaggingFilterSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.LabelIndexFlaggingFilterSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.LabellingFilterSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.LimitNumberFilterSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.PatternSplittingSubtokenizer;
import com.github.ippeiukai.externaltoken.lucene.analysis.subtokenizers.ReorderingFilterSubtokenizer;

/**
 * Examples:
 * 
 * orderedTagMode: <br>
 * input:"cars","cars/car/noun/plural"<br>
 *  => subtokenDelimiter:"/", numMaxTags:4, labels:{"text","lemma","pos","form-case-tense-aspect"}
 * 
 * labelledTagMode: <br>
 * input: "text:cars|pos:noun", "text:cars|lemma:car|pos:noun|form-case-tense-aspect:plural"<br>
 *  => subtokenDelimiter:"|", labels:{"text","lemma","pos","form-case-tense-aspect"}
 *  
 * 
 */
public class TaggedTokenSubtokenizeFilter extends SubtokenizeFilter {
  
  /**
   * orderedTagMode
   */
  public TaggedTokenSubtokenizeFilter(TokenStream input,
      String subtokenDelimiter, int numMaxTags) {
    super(prefilter(input, numMaxTags), orderedTagMode(subtokenDelimiter,
        numMaxTags));
  }
  
  /**
   * orderedTagMode with labels
   */
  public TaggedTokenSubtokenizeFilter(TokenStream input,
      String subtokenDelimiter, int numMaxTags, String[] labels) {
    super(prefilter(input, numMaxTags), orderedTagMode(subtokenDelimiter,
        numMaxTags, labels));
  }
  
  /**
   * labelledTagMode
   */
  public TaggedTokenSubtokenizeFilter(TokenStream input,
      String subtokenDelimiter, String labelDelimiter, String[] labels) {
    super(prefilter(input, labels.length), labelledTagMode(subtokenDelimiter,
        labelDelimiter, labels));
  }
  
  private static TokenStream prefilter(TokenStream input, int numMaxTags) {
    return new PositionMultiplyingTokenFilter(input, numMaxTags);
  }
  
  private static SubtokenizerFactory orderedTagMode(String subtokenDelimiter,
      int numMaxTags) {
    return orderedTagMode(subtokenDelimiter, numMaxTags, null);
  }
  
  private static SubtokenizerFactory orderedTagMode(String subtokenDelimiter,
      int numMaxTags, String[] labels) {
    
    final Pattern _delimiterPattern = Pattern.compile(Pattern
        .quote(subtokenDelimiter));
    final int _numMaxTags = numMaxTags;
    final String _labelFormat;
    final String[] _labels;
    if (labels == null) {
      _labelFormat = "%1$d:%2$s";
      _labels = null;
    } else {
      _labelFormat = "%1$s:%2$s";
      _labels = labels;
    }
    
    return new SubtokenizerFactory() {
      @Override
      public Subtokenizer subtokenizer() {
        Subtokenizer subtokenizer = new PatternSplittingSubtokenizer(
            _delimiterPattern);
        subtokenizer = new LimitNumberFilterSubtokenizer(subtokenizer,
            _numMaxTags);
        if (_labels == null) {
          subtokenizer = new LabellingFilterSubtokenizer(subtokenizer,
              _labelFormat);
        } else {
          subtokenizer = new LabellingFilterSubtokenizer(subtokenizer,
              _labelFormat, _labels);
        }
        subtokenizer = new IndexFlaggingFilterSubtokenizer(subtokenizer);
        subtokenizer = new IncrementPositionFilterSubtokenizer(subtokenizer);
        return subtokenizer;
      }
    };
  }
  
  private static SubtokenizerFactory labelledTagMode(String subtokenDelimiter,
      String labelDelimiter, String[] labels) {
    
    final Pattern _delimiterPattern = Pattern.compile(Pattern
        .quote(subtokenDelimiter));
    final Pattern _labelPattern = Pattern.compile(String.format("(.*)%s",
        Pattern.quote(labelDelimiter)));
    final int _labelPatternGroup = 1;
    final String[] _labels = labels.clone();
    
    return new SubtokenizerFactory() {
      @Override
      public Subtokenizer subtokenizer() {
        Subtokenizer subtokenizer = new PatternSplittingSubtokenizer(
            _delimiterPattern);
        subtokenizer = new LabelIndexFlaggingFilterSubtokenizer(subtokenizer,
            _labelPattern, _labelPatternGroup, _labels);
        subtokenizer = new ReorderingFilterSubtokenizer(subtokenizer);
        subtokenizer = new IncrementPositionFilterSubtokenizer(subtokenizer);
        return subtokenizer;
      }
    };
  }
  
}
