package org.homermultitext.utils

import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

class ParseableStringOrca {


  // 
  /** Ordered list of ORCA objects with parseable strings. */
  ArrayList tokenList = []

  LinkedHashMap byzMappings = [:]

  String tabSeparator = "#"

  String orcaName


  File byzantineAuthority
  File alternateOrthoAuthority
  String parserCommand
  // 
  ParseableStringOrca(String analysisName, File byzAuthList, File altOrthList, String morphCmd) {
    orcaName = analysisName
    byzantineAuthority = byzAuthList
    alternateOrthoAuthority = altOrthList
    parserCommand = morphCmd
    
  }


  
  ArrayList orcafyTabFile(File tabFile)
  throws Exception {
    
    HmtEditorialTokenization tokenizer  = new HmtEditorialTokenization()
    LexicalValidation lex = new LexicalValidation(byzantineAuthority, alternateOrthoAuthority, parserCommand)
    tokenizer.tokenizeTabFile(tabFile, tabSeparator).each { tokenization ->
      CtsUrn urn
      CiteUrn analysis
      try {
	urn = new CtsUrn(tokenization[0])
	analysis = new CiteUrn(tokenization[1])
      } catch (Exception e) {
	throw e
      }


      switch (analysis.getCollection()) {
      case "tokentypes":
      
      System.err.println "Now validate ${urn} as " + lex.validateToken(urn)      
      break

      case "pers":
      case "place":
      case "peoples":
      System.err.println "Already analyzed: create ORCA for ${urn.getSubref()}"
      break

      default :
      System.err.println "===>${analysis.getCollection()}"
      break
      
      
      }

      
    }
  }

  LinkedHashMap mapByzForms(File tabFile) {
  }
  
}
