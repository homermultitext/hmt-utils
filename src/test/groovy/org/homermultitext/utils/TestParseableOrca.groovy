package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import org.homermultitext.citemanager.DseManager

class TestParseableOrca extends GroovyTestCase {

  File tabs = new File("testdata/tabs/Iliad-small.txt")

  
  File authSrc = new File("testdata/authlists")
  File byz = new File("testdata/authlists/orthoequivs.csv")
  File lexMap = new File("testdata/authlists/lexmap.csv")
  String morphCmd = "../morpheus/bin/morpheus"
    

  String analysisName = "urn:cite:citedemo:parseable"
  
  void testOrca() {
    ParseableStringOrca pso = new ParseableStringOrca(analysisName, byz, lexMap, morphCmd)

    
    pso.orcafyTabFile(tabs)
    pso.analysisList.each {
      println it.getTransformedText()
    }

    println "Byz mappings for this collection: "
    def bMap =     pso.getByzMappings()
    bMap.keySet().each { k ->
      println "${k} -> " + bMap[k]
    }



    println "Modern ortho mappings for this collection: "
    def altMap = pso.getAltMappings()
    altMap.keySet().each { k ->
      println "${k} -> " + altMap[k]
    }
  }
  
}