package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn



class TestTokenizationSample extends GroovyTestCase {



  void testTokenizeTiny() {
    String tiny = "δ'"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:18.2"
    String context = ""


    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(tiny, urn, context)
    // only 1 analysis:
    assert  analyses.size() == 1
    ArrayList analysis  = analyses[0]
    try {
      CtsUrn txtUrn = new CtsUrn(analysis[0])
      CiteUrn analysisUrn = new CiteUrn(analysis[1])
      assert analysisUrn.toString() == "urn:cite:hmt:tokentypes.lexical"

    } catch (Exception e){
      System.err.println("Failed for token ${analysis[0]}")
      System.err.println e.toString()
    }

  }


  void testTokenizeElement() {

    String tabLine =  """<l xmlns="http://www.tei-c.org/ns/1.0" n="2"><persName n="urn:cite:hmt:pers.pers712">Ἀντίλοχος</persName> δ' <persName n="urn:cite:hmt:pers.pers1">Ἀχιλῆϊ</persName> πόδας ταχὺς ἄγγελος ἦλθε·</l>"""

    def lineRoot = new XmlParser().parseText(tabLine)

    HmtEditorialTokenization toker = new HmtEditorialTokenization()

    ArrayList analyses = toker.tokenizeElement(lineRoot,"urn:cts:greekLit:tlg0012.tlg001.msA:18.2","",false)
    Integer expectedCount = 8
    assert  analyses.size() == expectedCount


    String expectedNs = "hmt"
    def expectedCollections = ["pers", "tokentypes"]
    analyses.each { analysis ->


      try {
        CtsUrn txtUrn = new CtsUrn(analysis[0])
        CiteUrn analysisUrn = new CiteUrn(analysis[1])
        assert analysisUrn.getNs() == expectedNs
        assert expectedCollections.contains(analysisUrn.getCollection())

      } catch (Exception e){
        System.err.println("Failed for token ${analysis[0]}")
        System.err.println e.toString()
      }
    }
  }
}
