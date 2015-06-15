package org.homermultitext.utils


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

  
  EthnicNameValidation(File tokensFile, File authListFile) {
    tokensMap = populateTokensMap(tokensFile)
    authList = populateAuthorityList(authListFile)
    computeScores()
  }


  /// methods required to implement interface

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
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    srcFile.eachLine {
      def cols = it.split(/,/)
      validList.add(cols[0])
    }
    return validList
  }
  
  
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

  
}