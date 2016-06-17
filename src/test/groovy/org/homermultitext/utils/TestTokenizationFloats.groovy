package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

class TestTokenizationFloats extends GroovyTestCase {

  void testBreve (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 0

    // Sample data with horrible Unicode combining
    // characters like breve
    File tab = new File("testdata/tokens/breve.tab")
    ArrayList analyses = toker.tokenizeTabFile(tab, "#", false)

    Integer expectedCount = 12
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
