package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestEthnicToken extends GroovyTestCase {

  File namesList = new File("testdata/authlists/hmtplaces.csv")

  String mycenaeans = "urn:cite:hmt:peoples.place5"
  
  void testEthnicNameValidation() {
    EthnicNameValidation ethnicv = new EthnicNameValidation(namesList)
    assert ethnicv.isValid(mycenaeans)
    assert ethnicv.validateToken(mycenaeans) ==  "true"
    
  }
  
}