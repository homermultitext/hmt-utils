package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestStringTokenization extends GroovyTestCase {

  File tabSrc = new File("testdata/tabs/Iliad-6k.txt")
  String separatorStr = "#"

  
  void testString() {
    String tabData = tabSrc.getText()

    HmtTokenizer tokenizer = new HmtTokenizer()
    //tokenizer.tokenizeTabs()
  }
  
}