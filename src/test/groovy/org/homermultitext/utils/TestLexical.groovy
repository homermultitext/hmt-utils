package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestLexical extends GroovyTestCase {

  File tokens = new File("testdata/tokens/tokens.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  
  String morphCmd = "../morpheus/bin/morpheus"

  /*
THese should be right for *unique* tokens:
  Integer expectedCount = 501
  Integer expectedFails = 111
  Integer expectedSuccesses = 390
  */
  void testLexicalValidation() {
    LexicalValidation lexicalv = new LexicalValidation(tokens, byz, lexMap, morphCmd)
    
    assertFalse lexicalv.validates()

    // all tokens accounted for:
    assert lexicalv.successCount() + lexicalv.failureCount() == lexicalv.tokensCount()



    //assert lexicalv.tokensCount() == expectedCount
    println "Results: success/fail/totals:"
    println "${lexicalv.successCount()} / ${lexicalv.failureCount()} / ${lexicalv.tokensCount()} "
    
    // break down as expected:
    //assert lexicalv.successCount() == expectedSuccesses
    //assert lexicalv.failureCount() == expectedFails
  }
  
}