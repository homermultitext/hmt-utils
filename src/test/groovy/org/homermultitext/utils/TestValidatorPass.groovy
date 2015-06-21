package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestValidatorPass extends GroovyTestCase {

  File tabsDir = new File("testdata/tabs")
  File tabSrc = new File(tabsDir, "VenetusA-Iliad-short.txt")
  File tokensFile = new File("build/venA-iliad-tokens.txt")
  String separatorStr = "#"


  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"

  
  void testCycleFromTabs() {
    // First, build a clean tokenization from tabulated source
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    // Insist on completely error-free tokenization!
    boolean continueOnException = false
    def tokenizationResults = toker.tokenizeTabFile(tabSrc,separatorStr,continueOnException )
    Integer idx = 0
    tokenizationResults.each {
      String outStr = "${it[0]},${it[1]}\n"
      tokensFile.append(outStr)
      idx++
    }
    System.err.println "Set up " + idx  + " tokens to validate."
    // Now let's validate:
    HmtValidator v = new HmtValidator(tokensFile,authSrc, byz,lexMap, morphCmd)
    v.writeReports(new File("build/vadliator"), "testvalidator")
  }
  
}