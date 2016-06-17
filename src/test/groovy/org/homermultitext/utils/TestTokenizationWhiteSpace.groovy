package org.homermultitext.utils


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

import static org.junit.Assert.*
import org.junit.Test


class TestTokenizationWhiteSpace extends GroovyTestCase {

  // A long scholion with complex distribution of whitespace
  // in XML markup.
  File longScholion = new File("testdata/tokens/main-18-8.txt")

  void testWhiteSpace (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeTabFile(longScholion, "#", false)

    Integer expectedCount = 130
    assert analyses.size() == expectedCount

    String expectedNs = "hmt"
    def expectedCollections = ["tokentypes", "pers", "place", "peoples"]
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
