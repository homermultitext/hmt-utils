package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTinyLexical extends GroovyTestCase {

  // Tokens for one scholion:
  //File tokens = new File("testdata/tokens/msA_18_69.txt")
  //File tokens = new File("testdata/tokens/singleton.txt")
  File tokens = new File("testdata/tokens/brokenparse.txt")

  // Authority  lists:
  File lexMap = new File("testdata/authlists/lexmap.csv")
  File byz = new File("testdata/authlists/orthoequivs.csv")

  // Parser command:
  String morphCmd = "../morpheus/bin/morpheus"

  File log  = new File("tinylex.log")

  void testLexicalValidation() {
    // Turned of until morphological validation reimplemented
    /*
    LexicalValidation lexicalv = new LexicalValidation(tokens, byz, lexMap, morphCmd, log)

    //assertFalse lexicalv.validates()

    // all tokens accounted for:
    //assert lexicalv.successCount() + lexicalv.failureCount() == lexicalv.tokensCount()
    //assert lexicalv.tokensCount() == expectedCount
    println "Results: success/fail/totals:"
    println "${lexicalv.successCount()} / ${lexicalv.failureCount()} / ${lexicalv.tokensCount()} "


    def resMap = lexicalv.getValidationResults()


    println "Results map: "
    resMap.keySet().each { k ->
      println "${k}: ${resMap[k]}, k of class " + k.getClass()
    }

    println "From tokens map:"
    lexicalv.tokensMap.keySet().each { k ->
      println "${k}: ${lexicalv.tokensMap[k]}, k of class " + k.getClass()
    }

    //    File rept = new File("lexreport.html")
    //rept.setText(lexicalv.getLexicalTokensReport("scholion-A-18.69"), "UTF-8")



    // break down as expected:
    //assert lexicalv.successCount() == expectedSuccesses
    //assert lexicalv.failureCount() == expectedFails
    */
  }

}
