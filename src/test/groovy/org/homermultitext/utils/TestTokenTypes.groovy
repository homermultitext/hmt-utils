package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTokenTypes extends GroovyTestCase {

  File inputFile = new File("testdata/tabs/ethnic.txt")
  String separatorStr = "#"
  
  void testEthnicTag() {
    HmtGreekTokenization toker = new HmtGreekTokenization()
    ArrayList results = toker.tokenize( inputFile,  separatorStr) 

    File can = new File("testdata/out/canit.txt")
    results.each {
      can.append(it + "\n", "UTF-8")
    }
  }



}