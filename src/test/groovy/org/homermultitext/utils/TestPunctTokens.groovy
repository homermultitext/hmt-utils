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


  void testHighStops() {

    File lexMap = new File("testdata/authlists/lexmap.csv")
    File byz = new File("testdata/authlists/orthoequivs.csv")
    String morphCmd = "../morpheus/bin/morpheus"
    File log = new File("highstop-log.txt")
    
    File tokensFile = new File("testdata/tokens/250-punct.tokens")

    LexicalValidation lexicalv = new LexicalValidation(tokensFile, byz, lexMap, morphCmd, log)

    def occMap = lexicalv.getOccurrences()
    def valRes = lexicalv.getValidationResults()
    println "Number of tokens: " + lexicalv.tokensCount()
    println "No. keys in occurrence map: " + occMap.keySet().size()
    println "No. keys in validaion results: " + valRes.keySet().size()
    occMap.keySet().each { k ->
      println "For ${k} -> " + occMap[k].size() + " occurrences"
    }
    
  }

  
  
}