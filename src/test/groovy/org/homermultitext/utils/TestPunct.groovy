package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestPunct extends GroovyTestCase {
  


  void testPunctSplitting() {
    String txt = "οὐλομένην· ἡ μυρί'"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:1.2"
    String context = ""
    
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(txt, urn, context)

    // 3 words and 1 punct. mark:
    assert analyses.size() == 4

    
  }


  void testHighUnicode() {
    String txt = "⁑"
    String urn = "urn:cts:greekLit:tlg5026.msAint.hmt:18.4"
    String context = ""

    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    ArrayList analyses = toker.tokenizeString(txt, urn, context)
    assert analyses.size() == 1
    ArrayList endOfSchol = analyses[0]
    assert endOfSchol[1] == "urn:cite:hmt:tokentypes.punctuation"
    
    txt = "ονόματα·"
    urn = "urn:cts:greekLit:tlg5026.msA.hmt:18.21"
    ArrayList analyses2 = toker.tokenizeString(txt, urn, context)
    // 2 analyses:  a word, and a punctuation mark:
    assert analyses2.size() == 2
    ArrayList wordForm = analyses2[0]
    ArrayList highStop = analyses2[1]
    assert wordForm[1] == "urn:cite:hmt:tokentypes.lexical"
    assert highStop[1] == "urn:cite:hmt:tokentypes.punctuation"
    
  }

}