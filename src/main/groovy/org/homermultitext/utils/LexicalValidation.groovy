package org.homermultitext.utils

import java.text.Normalizer
import java.text.Normalizer.Form

import au.com.bytecode.opencsv.CSVReader

import edu.harvard.chs.cite.CtsUrn
import edu.unc.epidoc.transcoder.TransCoder

import edu.holycross.shot.greekutils.GreekMsString

class LexicalValidation implements HmtValidation {

  Integer debug = 0

  boolean verbose = false
  boolean log = false
  File dbLog

  
  // map of token URNs to CTS URNs w subref (occurrences)
  LinkedHashMap tokensMap = [:]
  
  // map of the same token URNs to boolean (t = valid)
  LinkedHashMap validationMap = [:]
  Integer successes = 0
  Integer failures = 0
  Integer total = 0


  ArrayList authList = []

  ArrayList lexMapList = []


  LexicalValidation(File tokensFile, File authListFile, File lexMappingFile, String morphCmd, boolean chatty) {
    tokensMap = populateTokensMap(tokensFile)
    System.err.println "Lexical validation got " + tokensMap.size() + " tokens"

    authList = populateAuthorityList(authListFile)

    lexMapList = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    System.err.println "Validated " + validationMap.size() + " entries"
    
    verbose = chatty    
  }

  LexicalValidation(File tokensFile, File authListFile, File lexMappingFile, String morphCmd, File logFile) {
    verbose = false
    dbLog = logFile
    log = true
    
    tokensMap = populateTokensMap(tokensFile)
    if (verbose) { System.err.println "Lexical validation got " + tokensMap.size() + " tokens"}

    authList = populateAuthorityList(authListFile)

    lexMapList = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    if (verbose) {System.err.println "Validated " + validationMap.size() + " entries"}
    
  }
  
  LexicalValidation(File tokensFile, File authListFile, File lexMappingFile, String morphCmd) {
    verbose = true
    tokensMap = populateTokensMap(tokensFile)
    System.err.println "Lexical validation got " + tokensMap.size() + " tokens"

    authList = populateAuthorityList(authListFile)

    lexMapList = populateLexMap(lexMappingFile)

    validationMap = computeScores(tokensFile, morphCmd)
    System.err.println "Validated " + validationMap.size() + " entries"

  }


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
   *
   */
  LinkedHashMap computeScores(File srcFile, String parserCmd) {
    // Map of tokens to validation result
    LinkedHashMap scoreBoard = [:]
    
    TransCoder tobeta = new TransCoder()
    tobeta.setConverter("BetaCode")
    tobeta.setParser("Unicode")


    Integer good = 0
    Integer bad = 0
    Integer lexCount = 0

    CSVReader srcReader = new CSVReader(new FileReader(srcFile))
    srcReader.readAll().each { lexLine ->
      String psg = lexLine[0]
      String tokenType = lexLine[1]
      
      if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	lexCount++;
	boolean urnOk = false
	CtsUrn tokenUrn
	String token
	String betaToken
      
	try {
	  tokenUrn = new CtsUrn(lexLine[0])
	  String msg = "\nvalidate " + tokenUrn
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg + "\n") }
	  if (tokenUrn.hasSubref()) {
	    token = tokenUrn.getSubref()
	    betaToken = tobeta.getString(token.toLowerCase())
	    urnOk = true
	  } else {
	    System.err.println "LexicalValidation: no subref on URN " + tokenUrn
	  }
	  
	} catch (Exception e) {
	  System.err.println ("LexicalValidation: failed to make CtsUrn: " + e)
	}

	if ((! urnOk) || (token.size() < 1)) {
	  System.err.println "LexicalValidation:computeScores:${lexCount}: invalid URN value " + tokenUrn + " from token " + lexLine
	  scoreBoard[tokenUrn]  = "fail"
	  failures = failures + 1

	  
	} else if (GreekMsString.isValidMsChar(token)) {
	  String msg = "${lexCount}: valid punctuation: " + token
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg + "\n") }

	  scoreBoard[tokenUrn]  = "punctuation"
	  successes = successes + 1
	
	} else if (authList.contains(token)) {
	  String msg = "${lexCount}: Byzantine orthography  for ${tokenUrn} ok: " + token
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg + "\n") }

	  scoreBoard[tokenUrn] = "byz"
	  successes = successes + 1

	} else if (lexMapList.contains(token)) {
	  String msg = "${lexCount}: modern orthography ok: " + token
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg + "\n") }

	  scoreBoard[tokenUrn] = "success"
	  successes = successes + 1
	
	} else {
	  def command = "${parserCmd} ${betaToken}"
	  String msg =  "${lexCount}: Analyzing ${token} with ${command}..."
	  if (verbose) { System.err.println msg}
	  if (log) { dbLog.append(msg) }

	  def proc = command.execute()
	  proc.waitFor()
	  def reply = proc.in.text.readLines()

	  if (reply[1] ==~ /.*unknown.+/) {
	    scoreBoard[tokenUrn]  = "fail"
	    failures = failures + 1
	    if (verbose) { System.err.println " fails."}
	    if (log) { dbLog.append(" fail.\n") }

	  } else {
	    scoreBoard[tokenUrn]  = "success"
	    successes = successes + 1
	    if (verbose) { System.err.println " success."}
	    if (log) { dbLog.append(" success.\n") }

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

    CSVReader srcReader = new CSVReader(new FileReader(srcFile))
    srcReader.readAll().each { lexLine ->
      String normalUrn = Normalizer.normalize(lexLine[1], Form.NFC)
      validList.add(normalUrn)
    }
    return validList
  }


  
  // add error checking...
  ArrayList populateAuthorityList(File srcFile) {
    ArrayList validList = []
    CSVReader srcReader = new CSVReader(new FileReader(srcFile))
    srcReader.readAll().each { lexLine ->
      String normaler = Normalizer.normalize(lexLine[1], Form.NFC)
      validList.add(normaler)
    }
    return validList
  }


  LinkedHashMap populateTokensMap(File srcFile) {
    LinkedHashMap occurrences = [:]

    CSVReader srcReader = new CSVReader(new FileReader(srcFile))
    Integer lineCount = 0
    srcReader.readAll().each { lexLine ->
      lineCount++;
      System.err.println "popTOkes: ${lineCount} ${lexLine}"
      String psg
      String tokenType
      if (lexLine.size() != 2) {
	// OpenCSV BREAKS ON BIG UNICODE!
	System.err.println "populateTokenMap:lexline has ${lexLine.size()} cols???  #${lexLine}#"
	if (lexLine[1]  ==~ /\[[0-9]+\]/) {
	  String frankenstein = lexLine[0] + lexLine[1]
	  System.err.println "Try FRANKENSTEINED " + frankenstein
	  psg = Normalizer.normalize(frankenstein, Form.NFC)      
	  tokenType = Normalizer.normalize(lexLine[1], Form.NFC)      
	} else {
	  System.err.println "Couldn't make sense of second part, " + lexLine[1]
	  throw (new Exception("LexicalValidation:populateTokensMap: failed on ${lexLine}"))
	}
      } else {
	psg = Normalizer.normalize(lexLine[0], Form.NFC)      
	tokenType = Normalizer.normalize(lexLine[1], Form.NFC)      
      }
      if (tokenType == "urn:cite:hmt:tokentypes.lexical" ) {
	CtsUrn urn
	boolean urnOk
	String lex
	try {
	  urn  = new CtsUrn(psg)
	  urnOk = true
	  lex = urn.getSubref()
	} catch (Exception e) {
	  System.err.println "LexicalValidation:populateTokensMap: failed on ${psg} " + e
	  lex = "error"
	}
	if (occurrences[lex]) {
	  def psgs = occurrences[lex]
	  psgs.add(psg)
	  occurrences[lex] =  psgs
	} else {
	  occurrences[lex] = [psg]
	}
      }
    }
    return occurrences
  }

}
