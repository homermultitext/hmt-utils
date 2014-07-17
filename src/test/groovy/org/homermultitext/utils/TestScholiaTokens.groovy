package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestScholiaTokens extends GroovyTestCase {

  File tabsDir = new File("/Users/nsmith/Desktop/tabulated/scholia")
  File outputFile = new File("/Users/nsmith/Desktop/hmt-scholia-tokens.txt")
  String separatorStr = "#"

  
  void testFullCorpusTokenizer() {
    HmtTokenizer tokenizer = new HmtTokenizer(tabsDir, outputFile, separatorStr)
    tokenizer.tokenizeTabs()
    // Now add tests on content of outputFile...
  }
  
}