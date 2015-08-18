package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestPersToken extends GroovyTestCase {

  File namesList = new File("testdata/authlists/hmtnames.csv")
  String achilles = "urn:cite:hmt:pers.pers1"
  
  void testPersNameValidation() {
    PersNameValidation pnv = new PersNameValidation(namesList)
    assert pnv.validateToken(achilles) == "true"
    assert pnv.isValid(achilles)
  }
  
}