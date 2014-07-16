package org.homermultitext.utils

import java.text.Normalizer

class HmtTokenAnalyst {


  File orthoFile
  File tokensFile
  
  HmtTokenAnalyst(File tokens, File ortho) {
    this.tokensFile = tokens
    this.orthoFile = ortho
  }

  String normalizedForByzForm(String byzForm) {
    String byz = Normalizer.normalize(byzForm, Normalizer.Form.NFC)    
    String orthoData = Normalizer.normalize(orthoFile.getText("UTF-8"), Normalizer.Form.NFC)    
    String records = orthoData.find (/^.+,${byz},.+$/)
    


    File hack = new File ("/tmp/hackout.txt")
    hack.setText("Look for ${byz} in:\n ${orthoData}\nwith results ${records}", "UTF-8")
    //    def lines = orthoData.split("\n")
    //def records = lines.find { ln -> }
    return records

  }


}
