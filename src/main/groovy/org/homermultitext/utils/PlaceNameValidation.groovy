package org.homermultitext.utils


import edu.harvard.chs.cite.CiteUrn


/**
 * Implementation of the Validation interface to
 * validate the referential integrity of tokens referring
 * to place names.
 */
class PlaceNameValidation implements HmtValidation {

  Integer debug = 0


  /** Map of tokens (Cite URNs) to passges where they occur (CTS URNs with subreference). */
  LinkedHashMap tokensMap = [:]

  /** Map of the same token URNs to a boolean value, true if the URN value is valid. */
  LinkedHashMap validationMap = [:]

  /** Number of validly identified tokens. */
  Integer successes = 0
  /** Number of tokens not identified by valid tokens. */
  Integer failures = 0
  /** Total number of tokens analyzed. */
  Integer total = 0

  /** List of valid values. */
  ArrayList authorityList = []

  PlaceNameValidation(File tokensFile, File authListFile, Integer debugLevel) {
    debug = debugLevel
    tokensMap = populateTokensMap(tokensFile)
    System.err.println  "constructor set toeknsMap to " + tokensMap
    authorityList = populateAuthorityList(authListFile)
    computeScores()
  }
  
  PlaceNameValidation(File tokensFile, File authListFile) {
    tokensMap = populateTokensMap(tokensFile)
    authorityList = populateAuthorityList(authListFile)
    computeScores()
  }


  /// methods required to implement interface

  /** 
   * @returns A label for the validation class. 
   */
  String label() {
    return "Validation of place name identifiers"
  }

  /** 
   * @returns True is all tokens have valid identifiers. 
   */
  boolean validates() {
    return (total == successes)
  }


  /** 
   * @returns Number of tokens that have valid identifiers.
   */
  Integer successCount() {
    return successes
  }


  /** 
   * @returns Number of tokens that do not have valid identifiers.
   */
  Integer failureCount() {
    return failures
  }


  /** 
   * @returns Total number of tokens analyzed.
   */
    Integer tokensCount() {
    return total
  }


  /** Gets a mapping of all tokens to a boolean value,
   * true if the token has a valid identifier.
   * @returns A map keyed by token URNs, mapping to boolean
   * values.
   */
    LinkedHashMap getValidationResults() {
    return validationMap
  }

  /** Gets a mapping of all tokens to a CTS URN 
   * identifying the occurrence of this token.
   * @returns A map keyed by token URNs, mapping to text passages.
   */
  LinkedHashMap getOccurrences() {
    return tokensMap
  }
  
  /// methods doing the validation work:
  
  void computeScores() {
    // check for existence of tokensMap ...
    this.total = tokensMap.size()
    
    Integer good = 0
    Integer bad = 0
    def scoreMap = [:]


    if (debug > 0) {System.err.println "analyze against list " + tokensMap.keySet()}
    tokensMap.keySet().each { k ->

      if (debug > 0) {
	System.err.println "PlaceNameValidation: ${k} in list? ${authorityList.contains(k)}"
      }
	
      if (authorityList.contains(k)) {
	this.successes++;
	validationMap[k] = true
      } else {
	this.failures++;
	validationMap[k] = false
      }
    }
  }


  // add error checking: file must exist, be nonempty,
  // keys must be valid urns
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    Integer count = 0
    srcFile.eachLine { l ->
      if (count > 0)  {
	def cols = l.split(/,/)
	CiteUrn urn
	String authValue = cols[0]
	try {
	  if (debug > 1) { System.err.println "Loading URN string " + authValue}
	  urn = new CiteUrn(authValue)
	  validList.add(cols[0])
	} catch (Exception e) {
	  System.err.println "PlaceNameValidation: bad value in authority list. ${e}"
	  throw e
	}
      }
      count++;
    }
    return validList
  }
  
  
  // read file, return contents as a map
  LinkedHashMap populateTokensMap(File srcFile) {
    LinkedHashMap occurrences = [:]
    def placeNames = srcFile.readLines().findAll { l -> l ==~ /.+,urn:cite:hmt:place.+/}
     placeNames.each { p ->
      def cols = p.split(/,/)
      if (debug > 0) {   System.err.println "Place name column : " + cols }
      def pname = cols[1]
      def psg = cols[0]
      if (occurrences[pname]) {
	def psgs = occurrences[pname]
	psgs.add(psg)
	occurrences[pname] =  psgs
      } else {
	occurrences[pname] = [psg]
      }
    }
     return occurrences
  }

  
}
