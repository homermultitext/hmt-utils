package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestValidator extends GroovyTestCase {

  File tokens = new File("testdata/tokens/tokens-small-sample.csv")
  //File tokens = new File("testdata/tokens/tokens.csv")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  String morphCmd = "../morpheus/bin/morpheus"
    
  
  
  void testValidator() {
    HmtValidator v = new HmtValidator(tokens,authSrc, byz, morphCmd)
    assert v.persv.validates()
    assert v.placev.validates()
    assert v.ethnicv.validates()

    v.writeReports(new File("testdata/reportsoutput"), "239r")
    
		   

    
  }
  
}