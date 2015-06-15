package org.homermultitext.utils



import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
import org.homermultitext.citemanager.DseManager

import groovy.xml.StreamingMarkupBuilder



/** Class for validating all material associated with a given text-bearing surface (page of a MS, column of a papyrus...)
*/
class HmtValidator  {

  int debug = 0


  // Allow a range value?
  /** The folio to validate. */
  CiteUrn urn


  PersNameValidation persv
  PlaceNameValidation placev
  EthnicNameValidation ethnicv
  LexicalValidation lexv



  

  HmtValidator(File tokens, File authListsDir, File byzOrtho, String morphCmd) {
    persv = new PersNameValidation(tokens, new File(authListsDir, "hmtnames.csv"))
    placev = new PlaceNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    ethnicv = new EthnicNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))

    //lexv = new LexicalValidation(tokens, byzOrtho, morphCmd)
  }



  void writeReports(File reportsDir, String label) {
    if (! reportsDir.exists()) {
      reportsDir.mkdir()
    }


    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("HMT validation of ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "HMT-MOM: "
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href:"index.html", "summary")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    p("Summaries go here with links to details")
	  }
	}
	
      }
    }
    File report = new File(reportsDir,"index.html")
    report.setText(reportXml.toString(), "UTF-8")
  }
  
}

