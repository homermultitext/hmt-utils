package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestValidator extends GroovyTestCase {


  File geoNamesList = new File("testdata/authlists/hmtplaces.csv")
  String mycenae = "urn:cite:hmt:place.place5"
  String mycenaeanEthnic = "urn:cite:hmt:peoples.place5"

  File persNamesList = new File("testdata/authlists/hmtnames.csv")
  String achilles = "urn:cite:hmt:pers.pers1"



  void testSimpleValidators() {
    HmtValidator hmtv = new HmtValidator()

    EthnicNameValidation ethnicv = new EthnicNameValidation(geoNamesList)
    assert ethnicv.isValid(mycenaeanEthnic)
    assert ethnicv.validateToken(mycenaeanEthnic) ==  "true"


    hmtv.addResults(HmtValidatedType.ETHNIC_NAME, ethnicv)
    assert hmtv.validationSet.size() == 1

    def validatorKeys =  hmtv.validationSet.keySet()
    assert validatorKeys[0] == HmtValidatedType.ETHNIC_NAME

    PersNameValidation pnv = new PersNameValidation(persNamesList)
    assert pnv.validateToken(achilles) == "true"
    assert pnv.isValid(achilles)


    hmtv.addResults(HmtValidatedType.PERSONAL_NAME, pnv)
    assert hmtv.validationSet.size() == 2


    PlaceNameValidation plnv = new PlaceNameValidation(geoNamesList)
    assert plnv.isValid(mycenae)
    assert plnv.validateToken(mycenae) == "true"
    hmtv.addResults(HmtValidatedType.GEOGRAPHIC_NAME, plnv)
    assert hmtv.validationSet.size() == 3

    assert hmtv.validationSet.keySet().sort() == [HmtValidatedType.PERSONAL_NAME, HmtValidatedType.GEOGRAPHIC_NAME, HmtValidatedType.ETHNIC_NAME]

  }

/*
  File tokens = new File("testdata/tokens/tokens-small-sample.csv")
  //File tokens = new File("testdata/tokens/tokens.csv")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"

  File logger = new File("testvalidator-log.txt")
*

//  void testValidator() {
    // Removed until morphological validation restored
    /*
    HmtValidator v = new HmtValidator(tokens,authSrc, byz,lexMap, morphCmd, logger)
    assert v.persv.validates()
    assert v.placev.validates()
    assert v.ethnicv.validates()

    v.writeReports(new File("testdata/reportsoutput"), "239r")
  }  */


}
