package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestTokens extends GroovyTestCase {
  


  void testTokenizeName() {
    String pn = "Ζεὺς"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:11.3"
    String context = "urn:cite:hmt:pers.pers8"

    
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(pn, urn, context)

    println "Time to analyze: ${pn} from ${urn} in context ${context}"


    // only 1 analysis:
    assert  analyses.size() == 1
    ArrayList analysis  = analyses[0]
    assert analysis[0] == "${urn}@${pn}"
    assert analysis[1] == context
    
  }
  
}