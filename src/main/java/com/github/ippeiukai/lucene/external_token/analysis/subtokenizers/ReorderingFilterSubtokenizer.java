/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.lucene.external_token.analysis.subtokenizers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.util.AttributeSource;

import com.github.ippeiukai.lucene.external_token.analysis.Subtokenizer;

/**
 * reorder according to the index in FlagsAttribute
 */
public class ReorderingFilterSubtokenizer extends FilterSubtokenizer {

  private TokenStream attributeSource;
  private FlagsAttribute flagsAttr;
  
  private final SubtokenState.Pool subtokenStatePool = new SubtokenState.Pool();
  private final PriorityQueue<SubtokenState> statesQueue = new PriorityQueue<SubtokenState>();
  
  public ReorderingFilterSubtokenizer(Subtokenizer inner) {
    super(inner);
  }
  
  @Override
  public void init(TokenStream attributeSource) {
    super.init(attributeSource);
    this.attributeSource = attributeSource;
    this.flagsAttr = attributeSource.addAttribute(FlagsAttribute.class);
  }
  
  @Override
  public void resetSubtokenization() {
    inner.resetSubtokenization();
    statesQueue.clear(); // usually empty, no need to recyle.
  }
  
  @Override
  public boolean incrementSubtoken() {
    while(inner.incrementSubtoken()){
      final SubtokenState subtokenState = subtokenStatePool.getInstance(flagsAttr.getFlags(), attributeSource.captureState());
      statesQueue.add(subtokenState);
    }
    if(statesQueue.isEmpty()){
      return false;
    }
    SubtokenState subtokenState = statesQueue.poll();
    attributeSource.restoreState(subtokenState.getState());
    subtokenStatePool.recycle(subtokenState);
    return true;
  }
  
  // -----
  
  private static class SubtokenState implements Comparable<SubtokenState> {
    
    private int index;
    private AttributeSource.State state;
    
    private SubtokenState(){
      clear();
    }
    
    private void clear(){
      index = Integer.MAX_VALUE;
      state = null;
    }
    
    private void initialize(int index, AttributeSource.State state){
      this.index = index;
      this.state = state;
    }

    public final AttributeSource.State getState() {
      return state;
    }

    @Override
    public int compareTo(SubtokenState o) {
      return Double.compare(this.index, o.index);
    }
    
    public static class Pool {
      
      private final Deque<SubtokenState> pool = new ArrayDeque<ReorderingFilterSubtokenizer.SubtokenState>();
      
      public SubtokenState getInstance(int index, AttributeSource.State state) {
        SubtokenState instance = pool.poll();
        if(instance == null){
          instance = new SubtokenState();
        }
        instance.initialize(index, state);
        return instance;
      }
      
      public void recycle(SubtokenState instance){
        instance.clear();
        pool.offer(instance);
      }
    }
    
  }
   
}
