package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import edu.holycross.shot.orthography.GreekMsString

import edu.unc.epidoc.transcoder.TransCoder

class TestTokenizationStringSplitting extends GroovyTestCase {

  void testHighStops() {
    String str  = "δίῳ·"
    String dot2 = "πυκίλαι·"
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList splits =  toker.splitString(str)
    assert splits.size() == 2
    assert GreekMsString.isMsPunctuation(splits[1])


    toker.debug = 5
    ArrayList splits2 = toker.splitString(dot2)
    println "For ${dot2}, got ${splits2}"

    boolean needViz = false
    if (needViz) {
      TransCoder tobeta = new TransCoder()
      tobeta.setConverter("UnicodeC")
      tobeta.setParser("BetaCode")
      String asc = tobeta.getString(str)
      println "${str} == ascii " + asc
      TransCoder frombeta = new TransCoder()
      frombeta.setConverter("BetaCode")
      frombeta.setParser("Unicode")
      println "Backwards is ${asc} == ascii ${frombeta.getString(asc)}"
    }
  }

  void testPeriod() {
    String phrase = "γνώσεται. ἀσπασίως γὰρ ἀφίξεται "
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    toker.debug = 5

    Integer expectedTokens = 5
    ArrayList splits =  toker.splitString(phrase)


    System.err.println "${phrase} yields ${splits.size()} tokens:"
    System.err.println splits


    assert splits.size() == expectedTokens
  }
}
