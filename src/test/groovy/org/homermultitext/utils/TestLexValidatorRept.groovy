package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestLexValidatorRept extends GroovyTestCase {

  File tokens = new File("testdata/tokens/msA_18_69.txt")
  //File tokens = new File("testdata/tokens/tokens.csv")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"



  void testLexValidatorReporting() {
    // Turned off until support for morphology is reimplemented
    /*
    HmtValidator v = new HmtValidator(tokens,authSrc, byz,lexMap, morphCmd)

    File rept = new File("lex-rept.html")
    rept.setText(v.getLexicalTokensReport("scholion-A-18.69"),"UTF-8")
    */
  }

}
