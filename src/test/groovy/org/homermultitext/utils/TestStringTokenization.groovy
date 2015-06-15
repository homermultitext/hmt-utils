package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestStringTokenization extends GroovyTestCase {

  String separatorStr = "#"

  File tabSrc = new File("testdata/tabs/Iliad-6k.txt")
  Integer expectedSize = 6026

  void testString() {
    
    String tabData = tabSrc.getText()
    /*
    HmtEditorialTokenization tokenizer = new HmtEditorialTokenization()
    ArrayList tokens = tokenizer.tokenize(tabData, separatorStr)
    assert tokens.size() == expectedSize
    */
  }
  
}