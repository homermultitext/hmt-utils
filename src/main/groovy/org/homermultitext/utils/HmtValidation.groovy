package org.homermultitext.utils





public interface HmtValidation {

  LinkedHashMap getValidationResults()
  LinkedHashMap getOccurrences()
  String label()
  boolean validates()
  Integer successCount()
  Integer failureCount()
  Integer tokensCount()
  
}
