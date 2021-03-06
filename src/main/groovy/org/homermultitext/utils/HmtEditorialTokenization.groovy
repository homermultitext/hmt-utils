package org.homermultitext.utils

import org.apache.commons.io.FilenameUtils


import edu.holycross.shot.orthography.GreekMsString
import edu.holycross.shot.orthography.MilesianString

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.Orca


/** Class to tokenize text following Homer Multitext project conventions for
*   definition of character set and markup.
*/
class HmtEditorialTokenization {

  /** Debugging level, can be set dynamically for more
  * verbose spewing to stdout.
  */
  Integer debug = 0

  String orcaCollectionStr = "urn:cite:hmt:edtokens"

  /** Empty constructor.
   */
  HmtEditorialTokenization() {
  }


  // Given an ordered list of editorial tokens, creates
  // an ordered list of the cite library's Orca objects
  ArrayList tokenizationToOrcaList(ArrayList tokenList) {
    Calendar now = Calendar.getInstance()
    String versionStamp = "${now.get(Calendar.YEAR)}_${ now.get(Calendar.MONTH) + 1 }_${now.get(Calendar.DATE)}"

    def orcaList = []
    tokenList.eachWithIndex {t, i ->
      try {
	CtsUrn psgAnalyzed = new CtsUrn(t[0])
	CiteUrn analysisObject = new CiteUrn(t[1])
	CiteUrn analysisRecord = new CiteUrn("${orcaCollectionStr}.${i}.${versionStamp}")
	String textDeformation = psgAnalyzed.getSubref()
	Orca orca = new Orca(analysisRecord, psgAnalyzed, analysisObject, textDeformation)
	orcaList.add(orca)
	
      } catch (Exception e) {
	System.err.println "At ${i}, failed with tokenization pair " + t
	System.err.println e.toString()
      }
    }
    return orcaList
  }

  /** Gets a short phrase describing the kind of tokenization
   * done by this implementation of TokenizationSystem.
   * @returns A descriptive String.
   */
  String getDescription() {
    return "Tokenization of Greek editions following the HMT project's editorial conventions, and taking into consideration both TEI markup and Unicode character values."
  }



  // Actually need to look at last code point.  Sigh.
  // Still a problem here with/without white space following markup such
  // as named entities??
  //
  /** Closure splits a String on white space,
   * and checks for trailing punctuation.
   * @param str The String to tokenize.
   * @returns An ArrayList of Strings.
   */
  ArrayList splitString (str) {
    // results:
    ArrayList tokes = []

    // first split on white space:
    ArrayList splits = str.split(/[\s]+/)
    splits.each { s ->
      s = s.replaceAll("\u00B7"," \u0387")
      s.replaceAll(/^[ ]+/,"")
      s.replaceAll(/[ ]+$/,"")

      if (debug > 2) { System.err.println "Tokenizer examining : #" + s + "# of size " + s.length()}

      // then check for trailing punctuation
      if (s.length() > 0)  {
	int max = s.codePointCount(0, s.length() - 1)
	int codePoint = s.codePointAt(max)
	if (debug > 1 ) {println "Last code point in ${s} is " + codePoint}
	String cpStr =  new String(Character.toChars(codePoint))

	if (GreekMsString.isMsPunctuation(cpStr)) {
	  if (debug > 1) {println "== punctuation"}

	  // Save lexical component and puncutation component
	  // as two tokens. First, the lexical part:
	  String lexPart = ""
	  int limit = max - 1
	  if (debug > 2) { println "Num code points: " + max + " so cycle from 0 to " + limit }
	  if (limit >= 0) {
	    (0..limit).each { idx ->
	      int cp = s.codePointAt(idx)
	      String charAsStr =  new String(Character.toChars(cp))
	      if (debug > 2) { println "at ${idx}, cp " + cp + " = " + charAsStr}
	      lexPart = lexPart + charAsStr
	      if (debug > 2) { println "\t(lexpart now ${lexPart})"}
	    }
	    // strip any spaces between lexical part and punctuation!
	    lexPart = lexPart.replaceAll(/[ ]+$/,"")
	    // add the two separate parts as successive tokens:

	    if (lexPart.length() > 0) {
	      tokes.add(lexPart)
	      if (debug > 2) {    System.err.println "Stripped to " + lexPart + " and " + new String(Character.toChars(codePoint)) }
	    }
	    tokes.add(new String(Character.toChars(codePoint)))
	  } else {
	    if (s.length() > 0) {
	      tokes.add(s)
	    }
	  }

	} else {
	  if (debug > 2) {
	    println "${codePoint} = ${cpStr} NOT GreekMsPunctuation"
	  }
	  if (s ==~ /\s+/) {
	  } else {
	    tokes.add(s)
	  }
	}
      }
    }
    return tokes
  }



  /** Tokenizes a string of text taking account of the context given by tokenType.
   * @str String to tokenize
   * @urnBase The CTS URN value, as a String, of the citable node this string
   * belongs to.
   * @tokenType A String, possibly empty, classifying the tokens belonging to this
   * node.
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference,
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   */
  ArrayList tokenizeString (String str, String urnBase, String tokenType)
  throws Exception {
    if (debug > 2) {
      System.err.println "Tokenizing raw string " + str
    }
    return tokenizeString(str,urnBase,tokenType,true)
  }

  ArrayList tokenizeString (String str, String urnBase, String tokenType, boolean continueOnException)
  throws Exception {
    if (debug > 2) {
      System.err.println "continue? ${continueOnException} while tokenizing " + str
    }


    ArrayList classifiedTokens = []

    if (debug > 2) { System.err.println "Editorial:tokenizeString: tokenizing " + str + " and split it to " + splitString(str)}
    splitString(str).each { t ->
      if (debug > 2) { System.err.println "\tLook at " + t + " with type " + tokenType}
      ArrayList pairing

      if (t ==~ /[\s]+/) {
	// skip white space
      } else if (t.size() > 0)  {
	switch(tokenType) {

	case "urn:cite:hmt:tokentypes.waw":
	case "urn:cite:hmt:tokentypes.sic":
	pairing = ["${urnBase}@${t}", tokenType]
	break

	case "urn:cite:hmt:tokentypes.numeric":
	MilesianString ms
	try {
	  ms = new MilesianString(t, true)
	  pairing = ["${urnBase}@${t}", tokenType]
	} catch (Exception e) {
	  if (continueOnException) {
	    pairing = ["${urnBase}@${t}", "urn:cite:hmt:error.badGreekMsString"]
	  } else {
	    throw e
	  }
	}
	break

	default:
	GreekMsString gs
	if (debug > 1) { System.err.println "Tokenizer: try to figure out " + t}
	if (GreekMsString.isMsPunctuation(t)) {
	  pairing = ["${urnBase}@${t}", "urn:cite:hmt:tokentypes.punctuation"]
	  classifiedTokens.add(pairing)

	} else if ((tokenType ==~ /urn:cite:hmt:place.+/) || ( tokenType ==~ /urn:cite:hmt:pers.+/) ) {

	  try {
	    gs = new GreekMsString(t, true)
	    pairing = ["${urnBase}@${t}", tokenType]
	  } catch (Exception e) {
	    if (continueOnException) {
	      pairing = ["${urnBase}@${t}", "urn:cite:hmt:error.badGreekMsString"]
	    } else {
	      throw e
	    }
	  }
	  classifiedTokens.add(pairing)

	} else {
	  try {
	    gs = new GreekMsString(t, true)
	    pairing = ["${urnBase}@${t}", "urn:cite:hmt:tokentypes.lexical"]
	  } catch (Exception e) {
	    if (continueOnException) {
	      pairing = ["${urnBase}@${t}", "urn:cite:hmt:error.badGreekMsString"]
	    } else {
	      throw e
	    }
	  }
	  classifiedTokens.add(pairing)
	}
	break
	}
      }
    }
    return(classifiedTokens)
  }


  /** Recursively tokenizes a well-formed fragment of a document following HMT project
   * editorial conventions.  Tokenization considers both markup and type of characters.
   * @param node The root node of the fragment to tokenize.
   * @urnBase The CTS URN value, as a String, of the citable node this XML fragment
   * belongs to.
   * @tokenType A String, possibly empty, classifying the tokens belonging to this
   * node.
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference,
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:punctuation
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   * urn:cite:hmt:error
   */
  ArrayList tokenizeElement(Object node, String urnBase, String tokenType, boolean continueOnException)
  throws Exception {
    ArrayList classifiedTokens = []

    if (node instanceof java.lang.String) {
      if (debug > 2) { System.err.println "\ttokenizeElement: RAW STRING: "  + node }
      classifiedTokens = classifiedTokens +  tokenizeString(node, urnBase, tokenType)


    } else {
      if (debug > 2) { System.err.println "\ttokenizeElement: " + node.name() }
      String nodeName
      if (node.name() instanceof java.lang.String) {
	nodeName = node.name()
      } else {
	nodeName = node.name().getLocalPart()
      }


      switch (nodeName) {
      case "del":
      case "abbr":
      case "figDesc":
      case "ref":
      case "note":
      // Completely ignore contents.
      break


      case "div":
      if (node.'@type' == "ref") {
	// omit
      } else {
	node.children().each { child ->
	  tokenizeElement(child, urnBase, "", continueOnException).each { tokenList ->
	    classifiedTokens.add(tokenList)
	  }
	}
      }
      break

      case "num" :
      node.children().each { child ->
	tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.numeric", continueOnException).each { tokenList ->
	  classifiedTokens.add(tokenList)
	}
      }
      break

      case "sic":
      String wd = XmlNode.collectText(node)
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	classifiedTokens.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.sic"])
      }
      break


      case "w":
      String wd = XmlNode.collectText(node)
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	classifiedTokens.add(["${urnBase}@${wd}", tokenType])
      } else {
	classifiedTokens.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.lexical"])
      }
      break

      case "persName":
      String wd = XmlNode.collectText(node)
      if (debug > 2) { System.err.println "\ttokenizeElement: PERSNAME NODE: "  + node.text() }
      GreekMsString gs
      try {
	//	String nameText1 = n.collectText().replaceFirst(/^[\s]+/, "")
	String nameText1 = wd.replaceFirst(/^[\s]+/, "")
	String nameText = nameText1.replaceFirst(/[\s]+$/, "")

	if (debug > 0) { System.err.println "tokenizeElement: Trying to make GreekMsString from persname " + nameText }

	gs = new GreekMsString(nameText, true)
	classifiedTokens.add(["${urnBase}@${nameText}", "${node.'@n'}"])

      } catch (Exception e) {
	System.err.println "tokenizeElement: FAILED to classify personal name " + node.'@n'
	System.err.println "Continue?  " + continueOnException
	if (continueOnException) {
	  System.err.println " So pairing as error"
	  def pairing = ["${urnBase}@${node.text()}", "urn:cite:hmt:error.badGreekMsString"]
	  classifiedTokens.add(pairing)

	} else {

	  System.err.println "Since continueOnException = ${continueOnException}, QUITTING on exception"
	  throw e
	}
      }
      break


      case "placeName":
      String wd = XmlNode.collectText(node)
      GreekMsString gs

      try {
	//String placeText1 = n.collectText().replaceFirst(/^[\s]+/, "")
	String placeText1 = wd.replaceFirst(/^[\s]+/, "")
	String placeText = placeText1.replaceFirst(/[\s]+$/, "")
	gs = new GreekMsString(placeText, true)
	classifiedTokens.add(["${urnBase}@${placeText}", "${node.'@n'}"])

      } catch (Exception e) {
	System.err.println "tokenizeElement: FAILED to classify placeName " + node
	if (continueOnException) {
	  System.err.println " So pairing as error"
	  def pairing = ["${urnBase}@${node.text()}", "urn:cite:hmt:error.badGreekMsString"]

	  classifiedTokens.add(pairing)
	} else {
	  System.err.println "QUITTING on exception"
	  throw e
	}
      }

      break

      case "rs":
      if (node.'@type' == "waw") {
	node.children().each { child ->
	  tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.waw",continueOnException).each { tokenList ->
	    classifiedTokens.add(tokenList)
	  }
	}
      } else if (node.'@type' == "ethnic") {
	String wd = XmlNode.collectText(node)
	GreekMsString gs
	try {
	  //String placeText1 = n.collectText().replaceFirst(/^[\s]+/, "")
	  String placeText1 = wd.replaceFirst(/^[\s]+/, "")
	  String placeText = placeText1.replaceFirst(/[\s]+$/, "")
	  gs = new GreekMsString(placeText, true)
	  String urn = node.'@n'.replace("hmt:place", "hmt:peoples")
	  classifiedTokens.add(["${urnBase}@${placeText}", urn])

	  if (debug > 2) { System.err.println "\ttokenizeElement: ETHNIC NODE: "  + node.text() }

	} catch (Exception e) {
	  //println "FAILED to classify " + node
	  if (continueOnException) {
	    //println " So pair as error"
	    def pairing = ["${urnBase}@${node.text()}", "urn:cite:hmt:error.badGreekMsString"]
	    classifiedTokens.add(pairing)
	  } else {
	    //println "QUITTING on exception"
	    throw e
	  }
	}
      }
      break

      default:
      node.children().each { child ->
	tokenizeElement(child, urnBase, "", continueOnException).each { tokenList ->
	  classifiedTokens.add(tokenList)
	}
      }
      break
      }

    }
    return classifiedTokens
  }


  /** Tokenizes the tabular representation of a text
   * following HMT project editorial conventions.
   * Tokenization considers both markup and type of characters.
   * @param inputFile A document in tabular format to tokenize.
   * @param separatorStr The String value used to separate columns of
   * the tabular file.  Default value is "#".
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference,
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   * @throws Exception if last column of each row of the tabular file cannot be
   * parsed as a well-formed XML fragment.
   */
  ArrayList tokenizeTabFile(File inputFile, String separatorStr)
  throws Exception {
    return tokenizeTabString(inputFile.getText("UTF-8"), separatorStr, true)
  }

  ArrayList tokenizeTabFile(File inputFile, String separatorStr, boolean continueOnException)
  throws Exception {
    return tokenizeTabString(inputFile.getText("UTF-8"), separatorStr, continueOnException)
  }

  ArrayList indexSubReff(ArrayList tokens) {
    ArrayList indexedTokens = []
    def reffCount = [:]
    if (debug > 3) { System.err.println "\t\tindexSubReff: Indexing "+ tokens.size() + " pairings, ${tokens}" }
    tokens.each { t ->
      String subref = t[0]
      String tokenType = t[1]
      if (reffCount.keySet().contains(subref)) {
	reffCount[subref] = reffCount[subref] + 1
      } else {
	reffCount[subref] = 1
      }
      ArrayList pair = ["${subref}[${reffCount[subref]}]",  tokenType]
      if (debug > 0) {
	System.err.println "\t\tindexSubReff: New pair is  " +  pair
      }
      indexedTokens.add(pair)
    }
    return indexedTokens
  }


  /** Tokenizes following HMT project editorial conventions a String of data in
   * the CITE architecture tabular format.
   * Tokenization considers both markup and type of characters.
   * @param tabData A String of data in CITE tabular format..
   * @param separatorStr The String value used to separate columns of
   * the tabular file.  Default value is "#".
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference,
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   * @throws Exception if last column of each row of the tabular file cannot be
   * parsed as a well-formed XML fragment.
   */
  ArrayList tokenizeTabString(String tabData, String separatorStr) {
    return tokenizeTabString(tabData, separatorStr, true)
  }
  ArrayList tokenizeTabString(String tabData, String separatorStr, boolean continueOnException)
  throws Exception {


    def replyList = []
    tabData.readLines().each { l ->
      def cols = l.split(/${separatorStr}/)
      def urnBase = cols[0]
      if (cols.size() > 5) {
	String str = cols[5]

	// check for bad XML here!
	def root = new XmlParser().parseText(str)

	try {
	  if (debug > 1) {
	    System.err.println "tokenizeTabString: tokenize element with continue set to " + continueOnException
	  }
	  def rawTokens =  tokenizeElement(root, urnBase, "", continueOnException)
	  if (debug > 5) {System.err.println "tokenizeTabString: RAW TOKENS " + rawTokens}
	  replyList = replyList + indexSubReff(rawTokens)

	} catch (Exception e) {
	  System.err.println "HmtEditorialTokenization:tokenize: broke on ${root}"
	  System.err.println "exception ${e}"
	  System.err.println "FAILED TO PROCESS LINE: ${l}"
	  if (continueOnException) {
	    System.err.println "Need to find a way to continue!"
	  } else {
	    throw e
	  }
	}

      } else {
	if (debug > 0) { System.err.println "HmtEditorialTokenization: omit input line ${l}"}
      }
    }
    return replyList
  }


}
