package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

class TestTokenizationTypes extends GroovyTestCase {

  File ethnicSrc = new File("specs/resources/data/ethnic.txt")
  File placeSrc = new File("specs/resources/data/place.txt")
  String separatorStr = "#"

  void testEditorialTokenizer() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    boolean continueOnException = true

    Integer expectedPlaces = 7
    def placeResults = toker.tokenizeTabFile(placeSrc,separatorStr,continueOnException )
    assert placeResults.size() == expectedPlaces


    Integer expectedEthnics = 8
    def ethnicResults = toker.tokenizeTabFile(ethnicSrc,separatorStr,continueOnException )
    assert ethnicResults.size() == expectedEthnics


  }

}
