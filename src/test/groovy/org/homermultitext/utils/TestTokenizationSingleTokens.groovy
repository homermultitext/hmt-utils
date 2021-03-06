package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

class TestTokenizationSingleTokens extends GroovyTestCase {



  void testTokenizeName() {
    String pn = "Ζεὺς"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:11.3"
    String context = "urn:cite:hmt:pers.pers8"


    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(pn, urn, context)
    //println "Analyses for ${pn} == " + analyses
    // only 1 analysis:
    assert  analyses.size() == 1
    ArrayList analysis  = analyses[0]
    assert analysis[0] == "${urn}@${pn}"
    assert analysis[1] == context

  }

  void testSigmaElide() {
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:12.138"
    String str = "ὑψόσ'"
    String context = ""
    String expectedContext = "urn:cite:hmt:tokentypes.lexical"

    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(str, urn, context)
    //println "Analyses for ${str} == " + analyses
    ArrayList analysis  = analyses[0]
    assert analysis[0] == "${urn}@${str}"
    assert analysis[1] == expectedContext
  }

  void testHighStops() {
    String str  = "δίῳ·"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:18.257"
    String context = ""


    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(str, urn, context, false)

    assert analyses.size() == 2
    ArrayList punctAnalysis = analyses[1]
    assert punctAnalysis[1] == "urn:cite:hmt:tokentypes.punctuation"


    // test file with impostor high dot:
    File dotFile = new File("testdata/tokens/dot.txt")
    String dotStr = dotFile.getText("UTF-8")
    ArrayList dotFileAnalyses = toker.tokenizeString(dotStr,urn,context,false)
    assert dotFileAnalyses.size() == 4
    ArrayList lastAnalysis = dotFileAnalyses[3]
    assert lastAnalysis[1]  == "urn:cite:hmt:tokentypes.punctuation"
  }

  void testHighDot (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 0

    // test file with impostor high dot:
    File tabFile = new File("testdata/tokens/main-18-6.txt")
    ArrayList analyses = toker.tokenizeTabFile(tabFile, "#", false)
    Integer expectedCount = 26
    assert analyses.size() == expectedCount

    String expectedNs = "hmt"
    String expectedCollection = "tokentypes"
    analyses.each { toke ->
      try {
        CtsUrn urn = new CtsUrn(toke[0])
        CiteUrn analysisUrn = new CiteUrn(toke[1])
        assert analysisUrn.getNs() == expectedNs
        assert analysisUrn.getCollection() == expectedCollection

      } catch (Exception e) {
        System.err.println("Failed on URN " + toke[0])
        println e.toString()
      }
    }




  }



}
