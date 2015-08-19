package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestLexicalToken extends GroovyTestCase {

  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"
    
  

  
  void testValidForms() {
    LexicalValidation lex = new LexicalValidation(byz,lexMap,morphCmd)
    String wrath = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@μῆνιν"
    assert lex.validateToken(wrath) == "success"
    String nereids = "urn:cts:greekLit:tlg0012.tlg001.msA:18.38@Νηρηΐδες"
    assert lex.validateToken(nereids) == "alt"
    String ho = "urn:cts:greekLit:tlg0012.tlg001.msA:1.56@ο"
    assert lex.validateToken(nereids) == "alt"
  }


    void testInvalidForms() {
      LexicalValidation lex = new LexicalValidation(byz,lexMap,morphCmd)
      String bogus = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@bogus"
      assert lex.validateToken(bogus) == "fail"

      String notAUrn = "not-a-urn"
      assert lex.validateToken(notAUrn) == "fail"
    
    }

  
}