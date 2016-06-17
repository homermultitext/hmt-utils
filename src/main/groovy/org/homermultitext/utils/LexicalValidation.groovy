package org.homermultitext.utils

import java.text.Normalizer
import java.text.Normalizer.Form


import edu.holycross.shot.safecsv.SafeCsvReader

import edu.harvard.chs.cite.CtsUrn
import edu.unc.epidoc.transcoder.TransCoder

import edu.holycross.shot.orthography.GreekMsString

class LexicalValidation implements HmtValidation {

  Integer debug = 0

  boolean verbose = false
  boolean log = false
  File dbLog



  /** Map of token values to occurrences expressed as CTS URNs with subreferences.
   */
  LinkedHashMap tokensMap = [:]


  /** Map of the same token values to classification.  Classification is
   * one of the strings "success", "fail", "alt" or "byz".
   */
  LinkedHashMap validationMap = [:]

  /** Number of tokens classified as either "success", "alt" or "byz".  */
  Integer successes = 0
  /** Number of tokens classified as "fail".  */
  Integer failures = 0
  /** Total number of tokens.  */
  Integer total = 0

  /** Map with authority list for accepted Byzantine orthographies
   * for valid forms.  Valid Byzantine forms are keys; URNs in
   * Byzantine orthography collection are the values.
   */
  LinkedHashMap byzOrthoAuthority = [:]



  /** Map with authority list for valid alternate modern orthographies.
   * Valid forms are keys; URNs in alternate orthography collection are
   * the values.
   */
  LinkedHashMap modernOrthoAuthority = [:]


  String parserCommandPath = ""



  /** Constructor with all required data sources and boolean parameter for verbose setting.
   * @param File with output of HMT tokenization.  Each line is a comma-delimited
   * pairing of a CTS URN for a token (including subreference) to a classification
   * of the token.
   * @param byzOrthoAuthListFile Authority file for accepted Byzantine orthographic
   * variants, in five-column comma-delimited format used in
   * https://github.com/homermultitext/byzortho.
   * @param lexMappingFile Authority file for accepted modern orthographic variants,
   * in five-column  comma-delimited format used in https://github.com/homermultitext/lexmapping.
   * @param morphCmd Path to use in executing morpheus parser with a system call.
   * @param chatty True to spew to standard output.
   */
  LexicalValidation(File tokensFile, File byzOrthoAuthListFile, File lexMappingFile, String morphCmd, boolean chatty) {
    verbose = chatty

    tokensMap = populateTokensMap(tokensFile)
    if (verbose) { System.err.println "5-arg constructor: Lexical validation got " + tokensMap.size() + " tokens"}
    parserCommandPath = morphCmd

    byzOrthoAuthority = populateByzAuthorityList(byzOrthoAuthListFile)
    modernOrthoAuthority = populateLexMap(lexMappingFile)

    if (verbose) { System.err.println "Will compute scores with morph cmd " + parserCommandPath}
    validationMap = computeScores(tokensFile, morphCmd)


    if (verbose) {
      System.err.println "Validated " + validationMap.size() + " entries"

    }
  }



  /** Constructor including file for logging.
   * @param File with output of HMT tokenization.  Each line is a comma-delimited
   * pairing of a CTS URN for a token (including subreference) to a classification
   * of the token.
   * @param byzOrthoAuthListFile Authority file for accepted Byzantine orthographic
   * variants, in five-column comma-delimited format used in
   * https://github.com/homermultitext/byzortho.
   * @param lexMappingFile Authority file for accepted modern orthographic variants,
   * in five-column  comma-delimited format used in https://github.com/homermultitext/lexmapping.
   * @param morphCmd Path to use in executing morpheus parser with a system call.
   * @param logFile Writable file for logging.
   */
  LexicalValidation(File tokensFile, File byzOrthoAuthListFile, File lexMappingFile, String morphCmd, File logFile) {
    verbose = false
    dbLog = logFile
    log = true

    parserCommandPath = morphCmd
    tokensMap = populateTokensMap(tokensFile)
    if (verbose) { System.err.println "Lexical validation got " + tokensMap.size() + " tokens"}

    byzOrthoAuthority = populateByzAuthorityList(byzOrthoAuthListFile)

    modernOrthoAuthority = populateLexMap(lexMappingFile)


    validationMap = computeScores(tokensFile, morphCmd)
    if (verbose) {System.err.println "Validated " + validationMap.size() + " entries"}

  }



  /** Minimal constructor with authority lists and parser command, but not data set
   * identified.  Useful for analyziing dynamically generated material.
   */
  LexicalValidation(File byzOrthoAuthListFile, File lexMappingFile, String morphCmd) {
    byzOrthoAuthority = populateByzAuthorityList(byzOrthoAuthListFile)
    modernOrthoAuthority = populateLexMap(lexMappingFile)
    parserCommandPath = morphCmd
  }

  /** Constructor with all required data sources.
   */
  LexicalValidation(File tokensFile, File byzOrthoAuthListFile, File lexMappingFile, String morphCmd) {
    verbose = true
    parserCommandPath = morphCmd

    tokensMap = populateTokensMap(tokensFile)
    System.err.println "Lexical validation got " + tokensMap.size() + " tokens"

    byzOrthoAuthority = populateByzAuthorityList(byzOrthoAuthListFile)
    modernOrthoAuthority = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    System.err.println "Validated " + validationMap.size() + " entries"

  }


  /** Looks up URN in orthography authority list
   * for a given Byzantine form.
   * @param byzFormRaw Byzantine form to look up.
   * @returns CITE URN in Byzantine orthography collection.
   * @throws Exception if byzFormRaw does not appear in the
   * authority list.
   */
  String urnForByzOrtho(String byzFormRaw)
  throws Exception {
    String byzForm =  Normalizer.normalize(byzFormRaw, Form.NFC)
    if (byzOrthoAuthority.keySet().contains(byzForm)) {
      return byzOrthoAuthority[byzForm]
    } else {
      throw new Exception("LexicalValidation: ${byzForm} not a recognized Byzantine form.")
    }
  }



  /** Looks up URN in authority list for alternate
   * modern orthographies.
   * @param altFormRaw Alternate orthographic form o look up.
   * @returns CITE URN in alternate orthography collection.
   * @throws Exception if altFormRaw does not appear in the
   * authority list.
   */
  String urnForAltOrtho(String altFormRaw)
  throws Exception {
    String altForm =  Normalizer.normalize(altFormRaw, Form.NFC)
    if (modernOrthoAuthority.keySet().contains(altForm)) {
      return modernOrthoAuthority[altForm]
    } else {
      throw new Exception("LexicalValidation: ${altForm} not a recognized alternate orthography.")
    }
  }


  // /////////////////////////////////////////////////////////
  //
  /// methods required to implement HmtValidation interface


  String validateToken(String tokenString) {
    CtsUrn tokenUrn
    try {
      tokenUrn = new CtsUrn(tokenString)
      return validateToken(tokenUrn)

    } catch (Exception e) {
      String errMsg = "LexicalValidation: ${tokenString} not a valid CTS URN"
      System.err.println errMsg
      if (log) { dbLog.append(errMsg + "\n") }
      return "fail"
    }
  }

  String validateToken(CtsUrn tokenUrn) {
    String result = ""

    boolean continueAnalysis = true
    String subrefString

    if (tokenUrn.hasSubref()) {
      subrefString = tokenUrn.getSubref()
    } else {
      continueAnalysis = false
      String errMsg = "LexicalValidation:compute: ${psgUrnString} does not a have valid subreference."
      System.err.println errMsg
      if (log) { dbLog.append(errMsg + "\n") }
      result = "fail"
    }

    GreekMsString token
    if (continueAnalysis) {
      String msg = "\nvalidate " + tokenUrn
      if (verbose) { System.err.println msg}
      if (log) { dbLog.append(msg + "\n") }

      try {
	token = new GreekMsString(subrefString, "Unicode")
	if (token.toString().size() < 1) {
	  continueAnalysis = false
	}
      } catch (Exception e) {
	String errMsg =  "LexicalValidation:compute: ${subrefString} is not a valid Greek String: " + e
	System.err.println errMsg
	if (log) { dbLog.append(errMsg + "\n") }
	result = "fail"
	continueAnalysis = false
      }
    }


    if (continueAnalysis) {
      String asciiToken = token.toString(false)
      if (debug > 1) {
	System.err.println "LexicalValidation: token ${token}, ascii ${asciiToken}"
      }

      if (byzOrthoAuthority.keySet().contains(token.toString(true))) {
	result = "byz"

      } else if (modernOrthoAuthority.keySet().contains(token.toString())) {
	result = "alt"


      } else {
	// NS WORK HERE
	/*
	def command = "${parserCommandPath} ${asciiToken}"
	String xcodeMsg =  "Analyzing ${token} with ${command}..."
	System.err.println xcodeMsg
	if (log) { dbLog.append(xcodeMsg) }

	def proc = command.execute()
	proc.waitFor()
	def reply = proc.in.text.readLines()

	if (reply[1] ==~ /.*unknown.+/) {
	  result = "fail"

	} else {
	  result = "success"

	}
	*/
	result = "unanalyzed"
      }
    }
    return result
  }


  /**
   * Gets a human-readable label for the validation class.
   * @returns
   */
  String label() {
    return "Validation of lexical tokens"
  }

  /**
   * Determines if validation was successful.
   * @returns True if all tokens are valid.
   */
    boolean validates() {
    return (total == successes)
  }

  /**
   * Counts valid tokens.
   * @returns Number of valid tokens.
   */
  Integer successCount() {
    return successes
  }

  /**
   * Counts invalid tokens.
   * @returns Number of invalid tokens.
   */
  Integer failureCount() {
    return failures
  }

  /**
   * Counts all tokens.
   * @returns Total number of lexical tokens analyzed.
   */
  Integer tokensCount() {
    return total
  }


  /** Maps all tokens to a validation result.
   * @returns A map keyed by token URNs.
   */
  LinkedHashMap getValidationResults() {
    return validationMap
  }


  /** Maps all tokens to a CTS URN identifying the occurrence of this
   * token in a passage of text.
   * @returns A map keyed by token URNs, mapping to text passages.
   */
  LinkedHashMap getOccurrences() {
    return tokensMap
  }



  /**
   * Subjects all lexical tokens in a source data set to second-tier
   * analysis, and records the results in a map.
   * @param srcFile A comma-delimited file pairing occurrences of tokens
   * to a classification of the token. The file should give a CTS URN with
   * subreference in the first column, and a CITE URN for the classification
   * in the second column. If the classification is "urn:cite:hmt:tokentypes.lexical",
   * then the subreference value is subjected to second-tier analysis.
   * @param parserCmd Path to execute morpheus parser with a system call.
   * @returns A map of lexical tokens to one of the String values
   * "success", "fail", "alt", or "byz".
   */
  LinkedHashMap computeScores(File srcFile, String parserCmd)
  throws Exception {
    // Map of tokens to validation result
    LinkedHashMap scoreBoard = [:]


    Integer good = 0
    Integer bad = 0
    Integer lexCount = 0

    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      // normalize before doing any string comparisons:
      String psgUrnString = Normalizer.normalize(lexLine[0], Form.NFC)
      String tokenType = Normalizer.normalize(lexLine[1], Form.NFC)

      if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	lexCount++;

	String evaluation = validateToken(psgUrnString)
	scoreBoard[psgUrnString] = evaluation
	switch (evaluation) {

	case "fail":
	failures = failures + 1
	String errMsg = "LexicalValidation: failed to analyze ${psgUrnString}"
	System.err.println errMsg
	if (log) { dbLog.append(errMsg + "\n") }
	break


	case "alt":
	successes = successes + 1
	String altMsg = "Alternate modern orthography OK"
	System.err.println altMsg
	if (log) {dbLog.append(altMsg + "\n")}
	break

	case "byz":
	successes = successes + 1
	String byzMsg = "Alternate Byzantine orthography OK"
	System.err.println byzMsg
	if (log) {dbLog.append(byzMsg + "\n")}
	break

	case "success":
	successes = successes + 1
	break

	default:
	System.err.println "UNKNOWN ANALYSIS FOR ${psgUrnString}: ${evaulation}"
	break
	}
      }
    }
    this.total = lexCount
    return scoreBoard
  }


  /** Loads date from a .csv source file mapping
   * modern orthography not recognized by morpheus
   * to an equivalent modern orthography.  The mapping
   * should give a URN for the mapping in the first column,
   * the valid but unrecognized form in the second column,
   * and the parseable equivalent in the third column.
   * @param srcFile .csv File with mapping data.
   * @returns A list of valid forms morpheus cannot parse.
   */
  LinkedHashMap populateLexMap(File srcFile) {
    LinkedHashMap validForms = [:]
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      String urn = lexLine[0]
      String normalForm = Normalizer.normalize(lexLine[1], Form.NFC)
      validForms[normalForm] = urn
    }
    return validForms
  }




  /** Loads date from a .csv source file mapping
   * Byzantine orthography not recognized by morpheus
   * to an equivalent modern orthography.  The mapping
   * should give a URN for the mapping in the first column,
   * the valid but unrecognized form in the second column,
   * and the parseable equivalent in the third column.
   * @param srcFile .csv File with mapping data.
   * @returns A list of valid forms morpheus cannot parse.
   */
  //ArrayList populateByzAuthorityList(File srcFile) {
  LinkedHashMap populateByzAuthorityList(File srcFile) {
    LinkedHashMap validForms = [:]
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      String urn = lexLine[0]
      String trimmed = lexLine[1].replaceAll(/^[ ]+/,"")
      trimmed = trimmed.replaceAll(/[ ]+$/,"")
      String normaler = Normalizer.normalize(trimmed, Form.NFC)

      if (debug > 5) { System.err.println "LEXVALID: populating with NFC #" + normaler + "# of size ${normaler.size()}"}
      //      validList.add(normaler)
      validForms[normaler] = urn
    }
    return validForms
  }



  /** Maps tokens to occurrences. First loads data
   * a from a .csv source file pairing occurrences of tokens
   * to a classification of the token. The .csv file
   * should give a CTS URN with subreference in the first column,
   * and a CITE URN for the classification in the second column.
   * If the classification is "urn:cite:hmt:tokentypes.lexical",
   * then the subreference value is used as a key to a list of passages
   * where that string occurs.
   * @param srcFile .csv File with classification of tokens.
   * @returns A map of string values to occurrences.
   */
  LinkedHashMap populateTokensMap(File srcFile) {
    if (debug > 1) { System.err.println "Populate tokens map from ${srcFile}"}
    LinkedHashMap occurrences = [:]

    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    Integer lineCount = 0
    srcReader.readAll().each { lexLine ->
      lineCount++;
      if (debug > 2) { System.err.println "LexicalValidation:populateTokens: ${lineCount} ${lexLine}" }
      if (lexLine.size() != 2) {
	System.err.println "Wrong number of columns (${lexLine.size()}) in line ${lexLine}"
      } else {
	// normalize before counting on string comparisons:
	String psgUrnString = Normalizer.normalize(lexLine[0], Form.NFC)
	String tokenType = Normalizer.normalize(lexLine[1], Form.NFC)
	psgUrnString = psgUrnString.replaceAll("\u00B7"," \u0387")
	psgUrnString = psgUrnString.replaceAll(/^[ ]+/,'')
	psgUrnString = psgUrnString.replaceAll(/[ ]+$/,'')


	if (debug > 0) {System.err.println "LexicalValidation: check on #${psgUrnString}#"}

	if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	  // First, make sure urn value is OK:
	  CtsUrn urn
	  String lex
	  try {
	    urn  = new CtsUrn(psgUrnString)
	    if (urn == null) {
	      lex = "error"
	    }
	  } catch (Exception e) {
	    System.err.println "LexicalValidation:populateTokensMap: failed on ${psgUrnString} " + e
	    lex = "error"
	  }

	  if (lex != "error") {
	    // check that urn has a subref?
	    lex = urn.getSubref()
	  }

	  if (occurrences[lex]) {
	    def psgs = occurrences[lex]
	    psgs.add(psgUrnString)
	    occurrences[lex] =  psgs
	  } else {
	    occurrences[lex] = [psgUrnString]
	  }
	}
      }
    }
    return occurrences
  }

}
