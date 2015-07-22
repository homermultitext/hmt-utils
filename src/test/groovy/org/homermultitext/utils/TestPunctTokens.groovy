package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestPunctTokens extends GroovyTestCase {
  
  void testDoubleCross (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 0
    

    File tab = new File("testdata/tokens/doublecross.txt")
    ArrayList analyses = toker.tokenizeTabFile(tab, "#", false)

    ArrayList firstAnalysis = analyses[0]
    assert firstAnalysis[1] == "urn:cite:hmt:tokentypes.punctuation"
    
  }

  
  
}