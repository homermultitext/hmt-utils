package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestPlaceToken extends GroovyTestCase {

  File namesList = new File("testdata/authlists/hmtplaces.csv")
  String mycenae = "urn:cite:hmt:place.place5"
  
  
  void testPlaceNameValidation() {
    PlaceNameValidation pnv = new PlaceNameValidation(namesList)

    assert pnv.isValid(mycenae)
    assert pnv.validateToken(mycenae) == "true"
    
  }


  
}