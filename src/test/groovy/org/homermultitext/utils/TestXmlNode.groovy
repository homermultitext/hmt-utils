package org.homermultitext.utils

import java.text.Normalizer
import java.text.Normalizer.Form

import static org.junit.Assert.*
import org.junit.Test

class TestXmlNode extends GroovyTestCase {


  String abbrExpanChoice = """<div  type='lemma'>
<p>τεῖχος μέν ρ' ἄλοχοι τε φίλαι καὶ 
<choice> <abbr>νηπ</abbr><expan>νήπια</expan></choice> 
τέκνα ῥύατ' ἐφεσταῶτος·</p>
</div>
"""


  String expanXml = "<expan>νήπια</expan>"
  String expan = Normalizer.normalize("νήπια", Form.NFC)
  
  void testNode() {
    String actual = XmlNode.collectText(expanXml).replaceFirst(" ","")
    assert actual == expan    
    /*
    println "Here's expan ${expan}:"
    int max = expan.codePointCount(0, expan.length() - 1)
    (0..max).each { idx ->
      int cp = expan.codePointAt(idx)
      String charAsStr =  new String(Character.toChars(cp))
      println "at index ${idx}, cp " + cp + " = " + charAsStr
    }
    */

    /*
    println "Here's actual ${actual}:"
    max = actual.codePointCount(0, actual.length() - 1)
    (0..max).each { idx ->
      int cp = actual.codePointAt(idx)
      String charAsStr =  new String(Character.toChars(cp))
      println "at index ${idx}, cp " + cp + " = " + charAsStr
    }
    */

    
    //    assert expan == XmlNode.collectText(expanXml)
    

    def root = new XmlParser().parseText(abbrExpanChoice)
    root.p.choice.expan.each { ex ->
      assert expan == XmlNode.collectText(ex).replaceFirst(" ","")
    }
    
    
    println "Here's a snatch : " + XmlNode.collectText(abbrExpanChoice)
  }
  
}