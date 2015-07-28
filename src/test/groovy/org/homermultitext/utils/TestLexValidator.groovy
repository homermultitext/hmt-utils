package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestLexValidator extends GroovyTestCase {

  File tokens = new File("testdata/tokens/tokens-small-sample.csv")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"
    
  
  
  void testValidator() {
    LexicalValidation lex = new LexicalValidation(tokens,byz,lexMap,morphCmd)
  }
  
}