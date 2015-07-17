package org.homermultitext.utils

import java.text.Normalizer
import java.text.Normalizer.Form


import edu.holycross.shot.safecsv.SafeCsvReader

import edu.harvard.chs.cite.CtsUrn
import edu.unc.epidoc.transcoder.TransCoder

import edu.holycross.shot.greekutils.GreekMsString

class LexicalValidation implements HmtValidation {

  Integer debug = 0

  boolean verbose = false
  boolean log = false
  File dbLog

  

  /** Map of token values to occurrences expressed as CTS URNs with subreferences.
   */
  LinkedHashMap tokensMap = [:]
  

  /** Map of the same token values to classification.  Classification is
   * one of the strings "success", "fail", or "byz".
   */ 
  LinkedHashMap validationMap = [:]

  /** Number of tokens classified as either "success" or "byzortho".  */
  Integer successes = 0
  /** Number of tokens classified as "fail".  */
  Integer failures = 0
  /** Total number of tokens.  */
  Integer total = 0

  /** Authority list for accepted Byzantine orthographies
   * for valid forms. */
  ArrayList byzOrthoAuthList = []

  /** Authority list for accepted modern alternate orthographies
   * for valid forms. */
  ArrayList modernOrthoAuthList = []



  /** Constructor with all required data sources.
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
    if (verbose) { System.err.println "Lexical validation got " + tokensMap.size() + " tokens"}

    byzOrthoAuthList = populateAuthorityList(byzOrthoAuthListFile)
    modernOrthoAuthList = populateLexMap(lexMappingFile)
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
    
    tokensMap = populateTokensMap(tokensFile)
    if (verbose) { System.err.println "Lexical validation got " + tokensMap.size() + " tokens"}

    byzOrthoAuthList = populateAuthorityList(byzOrthoAuthListFile)

    modernOrthoAuthList = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    if (verbose) {System.err.println "Validated " + validationMap.size() + " entries"}
    
  }
  
  LexicalValidation(File tokensFile, File byzOrthoAuthListFile, File lexMappingFile, String morphCmd) {
    verbose = true
    
    tokensMap = populateTokensMap(tokensFile)
    System.err.println "Lexical validation got " + tokensMap.size() + " tokens"

    byzOrthoAuthList = populateAuthorityList(byzOrthoAuthListFile)

    modernOrthoAuthList = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    System.err.println "Validated " + validationMap.size() + " entries"

  }

  // ///////////////////////////////////////////////////////// 
  // 
  /// methods required to implement HmtValidation interface
  

  
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
   * "success", "fail" or "byz".
   */
  LinkedHashMap computeScores(File srcFile, String parserCmd)
  throws Exception {
    // Map of tokens to validation result
    LinkedHashMap scoreBoard = [:]
    
    TransCoder tobeta = new TransCoder()
    tobeta.setConverter("BetaCode")
    tobeta.setParser("Unicode")

    Integer good = 0
    Integer bad = 0
    Integer lexCount = 0

    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      // normalize before doing any string comparisons:
      String psg = Normalizer.normalize(lexLine[0], Form.NFC)
      String tokenType = Normalizer.normalize(lexLine[1], Form.NFC)
      
      if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	lexCount++;
	boolean continueAnalysis = true
	CtsUrn tokenUrn
	try {
	  tokenUrn = new CtsUrn(psg)
	  continueAnalysis = true
	} catch (Exception e) {
	  failures = failures + 1
	  String errMsg = "LexicalValidation:compute: ${psg} not a valid CTS URN (failures ${failures}): " + e
	  System.err.println errMsg
	  if (log) { dbLog.append(errMsg + "\n") }
	  scoreBoard[psg]  = "fail"
	  

	  continueAnalysis = false
	}

	
	String tokenString
	if (continueAnalysis) {
	  if (tokenUrn.hasSubref()) {
	    tokenString = tokenUrn.getSubref()
	  } else {
	    continueAnalysis = false
	    String errMsg = "LexicalValidation:compute: ${psg} does not a have valid subreference."
	    System.err.println errMsg
	    if (log) { dbLog.append(errMsg + "\n") }
	    
	    scoreBoard[psg]  = "fail"
	    failures = failures + 1
	  }
	}

	

	
	GreekMsString token
	if (continueAnalysis) {
	  
	  String msg = "\nvalidate " + tokenUrn
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg + "\n") }

	  try {
	    token = new GreekMsString(tokenString, "Unicode")
	    if (token.toString().size() < 1) {
	      continueAnalysis = false
	    }
	  } catch (Exception e) {
	    String errMsg =  "LexicalValidation:compute: ${tokenString} is not a valid Greek String: " + e
	    
	    System.err.println errMsg
	    if (log) { dbLog.append(errMsg + "\n") }

	    scoreBoard[psg]  = "fail"
	    failures = failures + 1
	    continueAnalysis = false
	  }
	}

	
	if (continueAnalysis) {
	  String betaToken = tobeta.getString(tokenString.toLowerCase())

	  if (debug > 1) {
	    System.err.println "LexicalValidation:compute: token ${token}, beta ${betaToken}"
	  }

	  if (GreekMsString.isMsPunctuation(betaToken)) {
	    
	    String punctMsg = "${lexCount}: valid punctuation: " + token
	    if (verbose) { System.err.println punctMsg}
	    if (log) { dbLog.append(punctMsg + "\n") }
	    
	    scoreBoard[tokenUrn.toString()]  = "punctuation"
	    successes = successes + 1
	
	  } else if (byzOrthoAuthList.contains(token.toString())) {
	    String byzOrthoMsg = "${lexCount}: Byzantine orthography  for ${tokenUrn} ok: " + token
	    if (verbose) { System.err.println byzOrthoMsg}
	    if (log) { dbLog.append(byzOrthoMsg + "\n") }

	    scoreBoard[tokenUrn.toString()] = "byz"
	    successes = successes + 1

	  } else if (modernOrthoAuthList.contains(token.toString())) {
	    
	    String orthoMsg = "${lexCount}: modern orthography ok: " + token
	    if (verbose) { System.err.println orthoMsg}
	    if (log) { dbLog.append(orthoMsg + "\n") }

	    scoreBoard[tokenUrn.toString()] = "success"
	    successes = successes + 1
	
	  } else {

	    def command = "${parserCmd} ${betaToken}"
	    String xcodeMsg =  "${lexCount}: Analyzing ${token} with ${command}..."
	    if (verbose) { System.err.println xcodeMsg}
	    if (log) { dbLog.append(xcodeMsg) }

	    def proc = command.execute()
	    proc.waitFor()
	    def reply = proc.in.text.readLines()

	    if (reply[1] ==~ /.*unknown.+/) {
	      scoreBoard[tokenUrn.toString()]  = "fail"
	      failures = failures + 1
	      if (verbose) { System.err.println " fails."}
	      if (log) { dbLog.append(" fail.\n") }

	    } else {
	      scoreBoard[tokenUrn.toString()]  = "success"
	      successes = successes + 1
	      if (verbose) { System.err.println " success."}
	      if (log) { dbLog.append(" success.\n") }

	    }
	  }
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
  ArrayList populateLexMap(File srcFile) {
    ArrayList validList = []

    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      String normalUrn = Normalizer.normalize(lexLine[1], Form.NFC)
      validList.add(normalUrn)
    }
    return validList
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
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    SafeCsvReader srcReader = new SafeCsvReader(srcFile)
    srcReader.readAll().each { lexLine ->
      String normaler = Normalizer.normalize(lexLine[1], Form.NFC)
      validList.add(normaler)
    }
    return validList
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
	String psg = Normalizer.normalize(lexLine[0], Form.NFC)      
	String tokenType = Normalizer.normalize(lexLine[1], Form.NFC)      


	if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	  // First, make sure urn value is OK:
	  CtsUrn urn
	  String lex
	  try {
	    urn  = new CtsUrn(psg)
	    if (urn == null) {
	      lex = "error"
	    }
	  } catch (Exception e) {
	    System.err.println "LexicalValidation:populateTokensMap: failed on ${psg} " + e
	    lex = "error"
	  }

	  if (lex != "error") {
	    // check that urn has a subref?
	    lex = urn.getSubref()
	  }
	    /*
	    try {
	      GreekMsString msLex = new GreekMsString(lex, "Unicode")
	    } catch (Exception e) {
	      System.err.println "LexicalValidation:populateTokensMap: invalid Greek string in ${psg} " + e
	      lex = "error"
	    }
	      
	    */

	    
	  if (occurrences[lex]) {
	    def psgs = occurrences[lex]
	    psgs.add(psg)
	    occurrences[lex] =  psgs
	  } else {
	    occurrences[lex] = [psg]
	  }
	}
      }
    }
    return occurrences
  }
    
}
