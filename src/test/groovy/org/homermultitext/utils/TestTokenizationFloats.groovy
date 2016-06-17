package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTokenizationFloats extends GroovyTestCase {

  void testBreve (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 0

    File tab = new File("testdata/tokens/breve.tab")
    ArrayList analyses = toker.tokenizeTabFile(tab, "#", false)

    StringBuilder tokens = new StringBuilder()
    analyses.each {
      tokens.append( it[0] + "," + it[1] + "\n")
    }

    File tokensFile = new File("floatTokens.txt")
    tokensFile.setText(tokens.toString(), "UTF-8")
  }



}
