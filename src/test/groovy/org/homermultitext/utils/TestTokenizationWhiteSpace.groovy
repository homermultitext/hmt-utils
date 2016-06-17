package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTokenizationWhiteSpace extends GroovyTestCase {

  void testWhiteSpace (){
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 0


    File tab2 = new File("testdata/tokens/main-18-8.txt")
    ArrayList analyses2 = toker.tokenizeTabFile(tab2, "#", false)

    println "W white space in 18.8:" +  analyses2
    analyses2.each {
      println it
    }


  }



}
