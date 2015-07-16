package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTinyTokens extends GroovyTestCase {
  


  void testTokenizeTiny() {
    String tiny = "δ'"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:18.2"
    String context = ""

    
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(tiny, urn, context)
    println "Analyses for ${tiny} == " + analyses 
    // only 1 analysis:
    assert  analyses.size() == 1
    ArrayList analysis  = analyses[0]
    println analysis
  }


  void testTinyTab() {
    String tabLine =  """<l xmlns="http://www.tei-c.org/ns/1.0" n="2"><persName n="urn:cite:hmt:pers.pers712">Ἀντίλοχος</persName> δ' <persName n="urn:cite:hmt:pers.pers1">Ἀχιλῆϊ</persName> πόδας ταχὺς ἄγγελος ἦλθε·</l>"""

    def lineRoot = new XmlParser().parseText(tabLine)

    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 5
    ArrayList tokens = toker.tokenizeElement(lineRoot,"urn:cts:greekLit:tlg0012.tlg001.msA:18.2","",false)
    println "TOKENS " + tokens
  }
}