package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestByzCf extends GroovyTestCase {

  File tokens = new File("testdata/tokens/elsai.txt")
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"
    
  File logFile = new File("byzcflog.txt")
  
  void testValidator() {
    LexicalValidation lex = new LexicalValidation(tokens,byz,lexMap, morphCmd, logFile)
    def analyses = lex.getValidationMap()
    println "Analyses for elsai: " + analyses
  }
  
}