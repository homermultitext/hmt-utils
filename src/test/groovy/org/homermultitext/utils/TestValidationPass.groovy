package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestValidationPass extends GroovyTestCase {

  File tabsDir = new File("testdata/tabs")
  File tabSrc = new File(tabsDir, "tiniest.tab")
  File tokensFile = new File("build/venA-iliad-tokens.txt")
  String separatorStr = "#"


  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"


  //  Integer expectedNodes = 3
  Integer expectedNodes = 1

  void testCycleFromTabs() {
    // First, build a clean tokenization from tabulated source
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    // Insist on completely error-free tokenization!
    boolean continueOnException = false
    def tokenizationResults = toker.tokenizeTabFile(tabSrc,separatorStr,continueOnException )
    Integer idx = 1
    tokenizationResults.each {
      String outStr = '"' + it[0] + '","' + it[1] + '"\n'
      tokensFile.append(outStr)
      idx++
    }
    assert tabSrc.readLines().size() == expectedNodes

    System.err.println "From tabulated source with ${tabSrc.readLines().size()} citable node, set up " + idx  + " tokenization results to validate."
    // Now let's validate:
    //HmtValidator v = new HmtValidator(tokensFile,authSrc, byz,lexMap, morphCmd)
    //System.err.println "and then validated " + v.lexv.tokensCount()  + " lexical tokens."


    // is is true that sum of all validions totals should equal
    // total input?  or are there errors that show up in now validation?
    // Sum up:
    /*
      lexical
      persname
      placename
      ethnics
      punctuation
      numbers
     */

    //v.writeReports(new File("build/vadliator"), "testvalidator")
    // Turned off until support for morphology is reimplemented
  }

}
