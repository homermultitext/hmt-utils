package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestTokenizationFilePass extends GroovyTestCase {

  File tabsDir = new File("testdata/tabs")
  File tabSrc = new File(tabsDir, "three-lines.tab")

  File tabSrc2 = new File(tabsDir, "Iliad-small.txt")


  File outputFile = new File("build/three-lines-tokens.txt")
  String separatorStr = "#"


  void testEditorialTokenizer() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 3
    boolean continueOnException = true
    def tokenizationResults = toker.tokenizeTabFile(tabSrc,separatorStr,continueOnException )

    tokenizationResults.each {
      outputFile.append('"' + it[0] + '","' + it[1] + '"\n')
    }
    //Integer expectedTokens = 65
    //    assert tokenizationResults.size() == expectedTokens
  }



  void testEditorialTokenizer2() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 3
    boolean continueOnException = true
    def tokenizationResults = toker.tokenizeTabFile(tabSrc2,separatorStr,continueOnException )
  }

}
