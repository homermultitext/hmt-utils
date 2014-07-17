package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestTokenizer extends GroovyTestCase {

  File tabsDir = new File("testdata/tabs")
  File outputFile = new File("testdata/out/tokens.txt")
  String separatorStr = "#"
  
  Integer expectedSize = 6725


  
  void testFullCorpusTokenizer() {
    HmtTokenizer tokenizer = new HmtTokenizer(tabsDir, outputFile, separatorStr)
    tokenizer.tokenizeTabs()
    assert outputFile.readLines().size() == expectedSize
  }


  void testBadInit() {
    HmtTokenizer tokenizer = new HmtTokenizer()
    // settings not intialized:
    shouldFail {
      tokenizer.tokenizeTabs()
    } 
  }

}