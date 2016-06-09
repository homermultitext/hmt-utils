package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestSigmaInLexValidator extends GroovyTestCase {

  File tokens = new File("testdata/tokens/blaptw.txt")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"



  void testValidator() {
    // Removed until morphological validation restored
    //LexicalValidation lex = new LexicalValidation(tokens, byz,lexMap,morphCmd, true)

  }

}
