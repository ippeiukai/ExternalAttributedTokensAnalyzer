/**
 * ExternalAttributedTokensAnalyzer
 * Copyright 2012 Ippei Ukai
 */
package com.github.ippeiukai.externaltoken.factorycore;

public interface ParameterProvider {

  String get(String parameterKey);
  int getInt(String parameterKey, int defaultValue);
  boolean getBoolean(String parameterKey, boolean defaultValue);
  
}
