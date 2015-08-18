package org.homermultitext.utils

import edu.harvard.chs.cite.CiteUrn
import edu.holycross.shot.safecsv.SafeCsvReader
import edu.holycross.shot.greekutils.GreekMsString


class EthnicNameValidation implements HmtValidation {

  Integer debug = 0


  // map of token URNs to CTS URNs w subref (occurrences)
  LinkedHashMap tokensMap = [:]
  // map of the same token URNs to boolean (t = valid)
  LinkedHashMap validationMap = [:]
  Integer successes = 0
  Integer failures = 0
  Integer total = 0


  ArrayList authList = []


  /** Constructor with single parameter for authority list.
   * Useful for interactively validating one or more tokens.
   * @param authListFile Authority list for ethnic names.
   */
  EthnicNameValidation(File authListFile) {
    authList = populateAuthorityList(authListFile)
  }



  /** Constructor with parameters for tokens to validate and 
   * authority list to use in validation.
   * Useful for batch validation of tokens in a file.
   * @param tokensFile Tokens to validate.
   * @param authListFile Authority list for ethnic names.
   */
  EthnicNameValidation(File tokensFile, File authListFile) {
    tokensMap = populateTokensMap(tokensFile)
    authList = populateAuthorityList(authListFile)
    computeScores()
  }

  // ////////////////////////////////////////////////////////
  // ********* methods required to implement interface *** //
  //

  
  /** Validates a single token.
   * @param token CITE URN of token to validate, as a String.
   * @returns Either the string "true" or the string "false".
   */
  String validateToken(String token) {
    String decision = "false"
    String place = token.replace("hmt:peoples","hmt:place")
    if (authList.contains(place)) {
      decision = "true"
    }
    return decision
  }

  boolean isValid(String token) {
    if (validateToken(token) == "true") {
      return true
    } else {
      return false
    }
  }

  
  String label() {
   return "Validation of identifiers for ethnic names"
  }
  
  boolean validates() {
    return (total == successes)
  }

  Integer successCount() {
    return successes
  }

  Integer failureCount() {
    return failures
  }

  Integer tokensCount() {
    return total
  }

  LinkedHashMap getValidationResults() {
    return validationMap
  }


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
    tokensMap.keySet().each { k ->
      String place = k.replace("hmt:peoples","hmt:place")
      if (authList.contains(place)) {
	this.successes++;
	validationMap[k] = true
      } else {
	this.failures++;
	validationMap[k] = false
      }
    }
  }


  // add error checking...
  /*
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    srcFile.eachLine {
      def cols = it.split(/,/)
      validList.add(cols[0])
    }
    return validList
  }
  */
  
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
	  System.err.println "EthnicNameValidation: bad value in authority list. ${e}"
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
      if (cols[1] ==~ /urn:cite:hmt:peoples.+/) {
	if (debug > 0) {   System.err.println "Ethnic name column : " +  cols[1] }
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

  /*
  // read file, return contents as a map
  LinkedHashMap populateTokensMap(File srcFile) {
    LinkedHashMap occurrences = [:]
    def ethnics = srcFile.readLines().findAll { l -> l ==~ /.+,urn:cite:hmt:peoples.+/}
     ethnics.each { p ->
      def cols = p.split(/,/)
      if (debug > 0) {   System.err.println "Ethnic name column : " + cols }
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
  */
  
}
