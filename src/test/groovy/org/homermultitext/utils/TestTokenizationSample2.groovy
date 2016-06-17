package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

class TestTokenizationSample2 extends GroovyTestCase {


  // Real-world sample of complex markup from Iliadic text
  File tabsDir = new File("testdata/tabs/")
  File tabSrc = new File(tabsDir, "Iliad-small.txt")
  String separatorStr = "#"


  void testEditorialTokenizer() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    def analyses = toker.tokenizeTabFile(tabSrc,separatorStr)

    // test size of output:
    Integer expectedTokens = 70
    assert analyses.size() == expectedTokens

    String expectedNs = "hmt"
    def expectedCollections = ["tokentypes", "pers", "peoples"]
    analyses.each { toke ->
      try {
        CtsUrn urn = new CtsUrn(toke[0])
        CiteUrn analysisUrn = new CiteUrn(toke[1])
        assert analysisUrn.getNs() == expectedNs
        assert expectedCollections.contains(analysisUrn.getCollection())

      } catch (Exception e) {
        System.err.println("Failed on URN " + toke[0])
        println e.toString()
      }
    }

  }

}
