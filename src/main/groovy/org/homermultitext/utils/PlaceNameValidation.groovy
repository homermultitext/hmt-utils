package org.homermultitext.utils

import edu.harvard.chs.cite.CiteUrn
import edu.holycross.shot.safecsv.SafeCsvReader
import edu.holycross.shot.greekutils.GreekMsString

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


  /** Constructor drawing required data from File sources.
   * @param tokensFile CSV file associating passages in first column
   * with analyses in second.
   * @param authListFile CSV file with URN value in first column.
   * @param debugLevel Degree of spewing to System.err.
   */
  PlaceNameValidation(File tokensFile, File authListFile, Integer debugLevel) {
    debug = debugLevel
    tokensMap = populateTokensMap(tokensFile)
    if (debug > 1) { System.err.println  "constructor set toeknsMap to " + tokensMap}
    authorityList = populateAuthorityList(authListFile)
    validationMap = computeScores()
  }

  /** Constructor drawing required data from File sources.
   * @param tokensFile CSV file associating passages in first column
   * with analyses in second.
   * @param authListFile CSV file with URN value in first column.
   */
  PlaceNameValidation(File tokensFile, File authListFile) {
    tokensMap = populateTokensMap(tokensFile)
    authorityList = populateAuthorityList(authListFile)
    validationMap =    computeScores()
  }


  /* ********************************************************** */
  /// methods required to implement HmtValidation interface:

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
  //
  /* ********************************************************** */  






  /* ********************************************************** */  
  /// methods that actually do the validation work:
  
  LinkedHashMap computeScores() {
    LinkedHashMap scoreMap = [:]

    
    // check for existence of tokensMap ...
    this.total = tokensMap.size()
    
    Integer good = 0
    Integer bad = 0



    if (debug > 0) {System.err.println "analyze against list " + tokensMap.keySet()}
    tokensMap.keySet().each { k ->
      if (debug > 0) {
	System.err.println "PlaceNameValidation: ${k} in list? ${authorityList.contains(k)}"
      }
	
      if (authorityList.contains(k)) {
	this.successes++;
	scoreMap[k] = true
      } else {
	this.failures++;
	scoreMap[k] = false
      }
    }
    return scoreMap
  }



  
  // add error checking: file must exist, be nonempty,
  // keys must be valid urns
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    Integer count = 0
    
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { tokenLine ->
      // skip headerline:
      if (count > 0)  {
	CiteUrn urn
	String authValue = tokenLine[0]
	try {
	  if (debug > 1) { System.err.println "Loading URN string " + authValue}
	  urn = new CiteUrn(authValue)
	  validList.add(tokenLine[0])
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
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { cols ->
      if (cols[1] ==~ /urn:cite:hmt:place.+/) {
	if (debug > 0) {   System.err.println "Place name column : " +  cols[1] }
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
     }
     return occurrences
  }
  /* ********************************************************** */  
  
}
