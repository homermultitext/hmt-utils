package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

// tokenization *system*
class TestTokenSystem extends GroovyTestCase {

  File inputFile = new File("testdata/tabs/Iliad-3lns.txt")
  Integer expectedTokenCount = 14
  String separatorStr = "#"
  
  void testTokenizing() {
    HmtGreekTokenization toker = new HmtGreekTokenization()
    ArrayList results = toker.tokenize( inputFile,  separatorStr) 
    assert results.size() == expectedTokenCount
  }


  /*
  void testBadInit() {
    HmtGreekTokenization toker = new HmtGreekTokenization()
    shouldFail {
      ArrayList results = toker.tokenize( inputFile,  separatorStr) 
    } 
  }
  */
}