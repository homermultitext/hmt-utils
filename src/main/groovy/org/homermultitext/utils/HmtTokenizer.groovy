package org.homermultitext.utils

import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
import edu.holycross.shot.hocuspocus.Corpus


/**
 */
class HmtTokenizer {

  def debug = 0

  /** Readable directory with one or more .txt files in
   * CITE architecture tabular format.
   */
  File tabulatedDir = null

  /** Writable file for output of tokenization. */
  File tokensFile = null

  /** Default String to separate columns in tab-delimited files.*/
  String separator = "#"


  // Static string definitions
  /** String value of URN for lexical token collection.*/
  String lexurnbase = "urn:cite:perseus:lextoken"
  /** String value of URN for numeric token collection.*/
  String numurnbase = "urn:cite:hmt:numerictoken"
  /** String value of URN for HMT named entity collection.*/
  String nameurnbase = "urn:cite:hmt:namedentitytoken"
  /** String value of URN for HMT  literal string collection.*/
  String literalurnbase = "urn:cite:hmt:literaltoken"
  /** String value of URN for HMT  label token collection.*/
  String labelurnbase = "urn:cite:hmt:labeltoken"



  /** Constructor with explicit values for all settings required for
   * the standard task of tokenizing a set of tabulated files and writing
   * the resulting tokenization to files.
   *  @param tabsDir Directory with .txt files in hocuspocus tabulated format.
   *  @param outputFile Writable file where RDF statements in TTL format will be
   *  written.
   *  @param fieldBreak String value used as separator for fields of hocuspocus
   * tabulated file.
   */
  HmtTokenizer(File tabsDir, File outputFile, String fieldBreak) {
    this.tabulatedDir = tabsDir
    this.tokensFile = outputFile
    this.separator = fieldBreak
  }


  /** Constructor using default settings appropriate for tokenizing
   * String input.
   */
  HmtTokenizer() {
  }



  /** Cycles through all .txt files in tabulatedDir, applies 
   * an HmtGreekTokenization to them, and writes results to
   * 
   */
  void tokenizeTabs() 
  throws Exception {
    HmtGreekTokenization tokenSystem = new HmtGreekTokenization()
    if ((tokensFile == null) || (tabulatedDir == null)) {
      throw new Exception("HmtTokenize:tokenizeTabs: input and output settings not defined.")
    }
    tokensFile.setText("")

    def tabList = tabulatedDir.list({d, f-> f ==~ /.*.txt/ } as FilenameFilter )?.toList() 
    tabList.each { f ->
      File tabFile = new File(tabulatedDir,f)
      if (debug > 0) { System.err.println "TOKENIZE TAB FILE " + tabFile }
	
      tokenSystem.tokenize(tabFile, separator).each { tokenPair ->
	String rawCts = tokenPair[0]
	String analysis = tokenPair[1]

	CtsUrn urn
	String ctsval =  rawCts.replaceAll(/\n/,"")
	try {
	  urn = new CtsUrn(ctsval)
	} catch (Exception e) {
	  System.err.println "HmtTokenzier, tokenizeTabs:  unable to make URN from ${ctsval} in pair ${tokenPair}"
	  }


	if (urn) {

	  def checkVal = "${urn.getUrnWithoutPassage()}:${urn.getRef()}"
	  String subref = urn.getSubref1()

	  //  HmtGreekTokenization is a white-space tokenizer that 
	  //  keeps punctuation.  For analysis, we will throw out punctuation 
	  //  characters.  ·
	  if (!subref)  {
	    System.err.println "HmtTokenizer, tokenizeTabs:  null subref in pair ${tokenPair}"
	    

	  } else {
	    subref = subref.replaceAll(/^[\(\[]/,"")
	    subref = subref.replaceAll(/[.,;?·]$/, "")
	    String trimmed = trimWord(subref)

	    CiteUrn analysisUrn 
	    String collection
	    try {
	      analysisUrn = new CiteUrn(analysis)
	      collection =  analysisUrn.getCollection()
	    } catch (Exception e) {
	    }
	    

	    if (collection != "tokentypes") {
	      if (debug > 0) { System.err.println "FROM ${collection} appending " + analysis }
	      tokensFile.append("${analysis}\t${ctsval}\n")
		
	    } else {
	      if (debug > 0) { System.err.println "FROM ${collection}, analyzing: " + tokenPair[1] }
	      switch (tokenPair[1]) {
	      case "urn:cite:hmt:tokentypes.lexical":
	      tokensFile.append("${lexurnbase}.${trimmed}\t${ctsval}\n", "UTF-8")
	      break
                        
	      case "urn:cite:hmt:tokentypes.numeric":
	      tokensFile.append("${numurnbase}.${trimmed}\t${ctsval}\n", "UTF-8")
		break
	      
		/*
		  case "urn:cite:hmt:tokentypes.namedEntity":
		  tokensFile.append("${nameurnbase}.${parts[1]}\t${ctsval}\n", "UTF-8")
		  break
		*/
                        
	      case ":cite:hmt:tokentypes.waw":
	      tokensFile.append("${literalurnbase}.${trimmed}\t${ctsval}\n", "UTF-8")
	      break
              
	      default : 
	      System.err.println "Unrecognized token type: ${tokenPair[1]} of class ${tokenPair[1].getClass()}"
	      break
	      }
	    } 
	  }
	}
      }
    }
  }


  
  /** Removes 'ancient markup' from a StringBuffer, 
   * that is, non-whitespace characters that are valid in 
   * an HMT text, but not part of tokens (such as
   * puncutation).
   * @param s The text to trim.
   * @returns The content of the StringBuffer,
   * as a String, with inappropriate Unicode codepoints removed. 
   */
  String trimWord(String s) {
    // convert String to UTF-8 StringBuffer
    StringBuffer buff = new StringBuffer()
    if (s == null) {
      return null
    }

    byte[] byteArray = s.getBytes("UTF-8")
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray)
    InputStreamReader isr = new InputStreamReader(bais,"UTF8")
    Reader inputReader = new BufferedReader(isr)
    int ch
    while ((ch = inputReader.read()) > -1) {
      buff.append((char) ch);
    }
    inputReader.close()

    StringBuffer trimmed = new StringBuffer()
    if (buff.size() < 1) {
      return ""
    }
    int max = buff.codePointCount(0, buff.size() - 1)
    int idx = 0
    while (idx < max) {
      int cp = buff.codePointAt(idx)
      if (cp != null) {
	if (cp in HmtDefs.puncChars) {
	} else {
	  trimmed.append( new String(Character.toChars(cp)))
	}
      }
      idx = buff.offsetByCodePoints(idx,1)	
    }
    // get last char:
    int cp = buff.codePointAt(max)
    if (cp in HmtDefs.puncChars) {
    } else {
      trimmed.append( new String(Character.toChars(cp)))
    }
    return trimmed.toString()
  }

}

