package org.homermultitext.utils


import edu.harvard.chs.cite.CiteUrn
import edu.holycross.shot.safecsv.SafeCsvReader
import edu.holycross.shot.greekutils.GreekMsString



/**
 * Implementation of the Validation interface to
 * validate the referential integrity of tokens referring
 * to personal names.
 */
class PersNameValidation implements HmtValidation {

  Integer debug = 0


  // map of token URNs to CTS URNs w subref (occurrences)
  LinkedHashMap tokensMap = [:]
  // map of the same token URNs to boolean (t = valid)
  LinkedHashMap validationMap = [:]
  Integer successes = 0
  Integer failures = 0
  Integer total = 0


  ArrayList authList = []

  
  PersNameValidation(File tokensFile, File authListFile) {
    tokensMap = populateTokensMap(tokensFile)
    authList = populateAuthorityList(authListFile)
    computeScores()
  }


  /// methods required to implement interface

  String label () {
    return "Validation of personal name identifiers"
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
      if (authList.contains(k)) {
	this.successes++;
	validationMap[k] = true
      } else {
	this.failures++;
	validationMap[k] = false
      }
    }
  }

  /*
  // add error checking...
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { tokenLine ->
    srcFile.eachLine {
      def cols = it.split(/,/)
      validList.add(cols[0])
    }
    return validList
    }*/

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
	  System.err.println "PersNameValidation: bad value in authority list. ${e}"
	  throw e
	}
      }
      count++;
    }
    return validList
  }
  
  /*  
  // read file, return contents as a map
  LinkedHashMap populateTokensMap(File srcFile) {
    LinkedHashMap occurrences = [:]
    def persNames = srcFile.readLines().findAll { l -> l ==~ /.+,urn:cite:hmt:pers.+/}
     persNames.each { p ->
      def cols = p.split(/,/)
      if (debug > 0) {   System.err.println "Per. name column : " + cols }
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

  
    LinkedHashMap populateTokensMap(File srcFile) {
    LinkedHashMap occurrences = [:]
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { cols ->
      if (cols[1] ==~ /urn:cite:hmt:pers.+/) {
	if (debug > 0) {   System.err.println "Personal name column : " +  cols[1] }
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
  
}
