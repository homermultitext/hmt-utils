package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTinyLexical extends GroovyTestCase {

  // Tokens for one scholion:
  File tokens = new File("testdata/tokens/msA_18_69.txt")

  // Authority  lists:
  File lexMap = new File("testdata/authlists/lexmap.csv")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  
  // Parser command:
  String morphCmd = "../morpheus/bin/morpheus"
  
  File log  = new File("tinylex.log")

  void testLexicalValidation() {
    LexicalValidation lexicalv = new LexicalValidation(tokens, byz, lexMap, morphCmd, log)
    
    //assertFalse lexicalv.validates()

    // all tokens accounted for:
    //assert lexicalv.successCount() + lexicalv.failureCount() == lexicalv.tokensCount()
    //assert lexicalv.tokensCount() == expectedCount
    println "Results: success/fail/totals:"
    println "${lexicalv.successCount()} / ${lexicalv.failureCount()} / ${lexicalv.tokensCount()} "


    def resMap = lexicalv.getValidationResults()

    resMap.keySet().each { k ->
      println "${k}: ${resMap[k]}"
    }

    File rept = new File("lexreport.html")
    rept.setText(lexicalv.getLexicalTokensReport("scholion-A-18.69"), "UTF-8")


    
    // break down as expected:
    //assert lexicalv.successCount() == expectedSuccesses
    //assert lexicalv.failureCount() == expectedFails
  }
  
}