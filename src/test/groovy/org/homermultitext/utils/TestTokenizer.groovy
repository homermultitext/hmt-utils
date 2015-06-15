package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestTokenizer extends GroovyTestCase {

  File tabsDir = new File("testdata/tabs/")
  File tabSrc = new File(tabsDir, "Iliad-small.txt")
  File outputFile = new File("testdata/out/tokens.txt")
  String separatorStr = "#"

  
  void testEditorialTokenizer() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    def tokenizationResults = toker.tokenizeTabFile(tabSrc,separatorStr)
  }
  
}