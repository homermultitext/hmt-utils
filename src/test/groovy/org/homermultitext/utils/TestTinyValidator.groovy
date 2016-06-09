package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestTinyValidator extends GroovyTestCase {

  //File tokens = new File("testdata/tokens/tokens-small-sample.csv")
  //File tokens = new File("testdata/tokens/tokens.csv")

  File tokens = new File("testdata/tokens/msA_18_69.txt")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"

  File logger = new File("testtinyvalidator-log.txt")


  void testValidator() {
    /*
    HmtValidator v = new HmtValidator(tokens,authSrc, byz,lexMap, morphCmd, logger)
    assert v.persv.validates()
    assert v.placev.validates()
    assert v.ethnicv.validates()

    v.writeReports(new File("testdata/reportsoutput"), "tinyvalidation")
    */
  }

}
