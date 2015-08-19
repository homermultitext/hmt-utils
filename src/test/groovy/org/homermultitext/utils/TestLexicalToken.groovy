package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestLexicalToken extends GroovyTestCase {

  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"

  // test values
  //
  //alternate moden orthography
  String nereids = "urn:cts:greekLit:tlg0012.tlg001.msA:18.38@Νηρηΐδες"  
  // valid byzantine orthography
  String ho = "urn:cts:greekLit:tlg0012.tlg001.msA:1.56@ο"
  

  LexicalValidation lex = new LexicalValidation(byz,lexMap,morphCmd)
  
  void testValidForms() {
    String wrath = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@μῆνιν"
  
    assert lex.validateToken(wrath) == "success"
    assert lex.validateToken(nereids) == "alt"
    assert lex.validateToken(ho) == "byz"
  }


  void testInvalidForms() {
    String bogus = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1@bogus"
    assert lex.validateToken(bogus) == "fail"

    String notAUrn = "not-a-urn"
    assert lex.validateToken(notAUrn) == "fail"
  }


  void testSecondTierTracking() {
    String byzHoString = "ο"
    String expectedUrn =  "urn:cite:hmt:byzortho.1983"
    assert lex.urnForByzOrtho(byzHoString) == expectedUrn

    String altNereids = "Νηρηΐδες"
    println "${altNereids} -> " + lex.urnForAltOrtho(altNereids)
    
  }
  
}