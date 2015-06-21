package org.homermultitext.utils




/**
 * Interface for validating the output of a HMT project tokenization.
 */
public interface HmtValidation {


  /** 
   * Gets a human-readable label for the validation class. 
   * @returns A string.
   */
  String label()

  /** 
   * Determines if validation was successful.
   * @returns True if all tokens are valid. 
   */
  boolean validates()
  
  /** 
   * Counts valid tokens.
   * @returns Number of valid tokens.
   */
  Integer successCount()

  /** 
   * Counts invalid tokens.
   * @returns Number of invalid tokens.
   */
  Integer failureCount()


  /** 
   * Counts all tokens.
   * @returns Total number of tokens analyzed.
   */
  Integer tokensCount()

  /** Gets a mapping of all tokens to a type-specific validation result.
   * @returns A map keyed by token URNs.
   */
  LinkedHashMap getValidationResults()

  /** Gets a mapping of all tokens to a CTS URN 
   * identifying the occurrence of this token.
   * @returns A map keyed by token URNs, mapping to text passages.
   */
  LinkedHashMap getOccurrences()
  
}
