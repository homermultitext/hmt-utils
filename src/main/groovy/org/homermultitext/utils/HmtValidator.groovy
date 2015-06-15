package org.homermultitext.utils



import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
import org.homermultitext.citemanager.DseManager

import groovy.xml.StreamingMarkupBuilder



/**
*/
class HmtValidator  {

  int debug = 0

  
  String del = "http://www.homermultitext.org/delete.png"
  String check = "http://www.homermultitext.org/check.png"



  PersNameValidation persv
  PlaceNameValidation placev
  EthnicNameValidation ethnicv
  LexicalValidation lexv

  /** Map of output file names to Validation objects. */
  LinkedHashMap validations = [:]  

  HmtValidator(File tokens, File authListsDir, File byzOrtho, String morphCmd) {

    persv = new PersNameValidation(tokens, new File(authListsDir, "hmtnames.csv"))
    validations["personalnames.html"] = (persv)
    placev = new PlaceNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["placenames.html"] = placev
    ethnicv = new EthnicNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["ethnicnames.html"] = ethnicv
    lexv = new LexicalValidation(tokens, byzOrtho, morphCmd)
    validations["lexicaltokens.html"] = lexv
  }



  void writeReports(File reportsDir, String label) {
    if (! reportsDir.exists()) {
      reportsDir.mkdir()
    }
    File summary = new File(reportsDir,"${label}.html")
    summary.setText(getSummaryReport(label), "UTF-8")

    File persnames = new File(reportsDir, "personalnames.html")
    persnames.setText(getPersonalNamesReport(label), "UTF-8")

    File placenames = new File(reportsDir, "placenames.html")
    placenames.setText(getPlaceNamesReport(label), "UTF-8")

    File ethnicnames = new File(reportsDir, "ethnicnames.html")
    ethnicnames.setText(getEthnicNamesReport(label), "UTF-8")


    File lexicaltokens = new File(reportsDir, "lexicaltokens.html")
    lexicaltokens.setText(getLexicalTokensReport(label), "UTF-8")


  }

  


  String getSummaryReport(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("HMT validation of ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "HMT validation: ${label}"
	  }
	  article(role: "main") {
	    h1("HMT validation: ${label}  — summary")

	    table {
	      tr {
		th("Report")
		th("Success/Failure/Total")
		th("Details")
	      }
	      validations.keySet().each { k ->
		def v = validations[k]
		tr {
		  td(v.label())
		  td {
		    mkp.yield "${v.successCount()}/${v.failureCount()}/${v.tokensCount()}"
		    if (v.validates()) {
		      img(src : check)
		    } else {
		      img(src : del)
		    }
		  }
		  td {
		    a (href: k, "see details")
		  }
		}
	      }
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }

  
  String getPersonalNamesReport(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("Personal name identifiers: ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "Personal name identifiers: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Personal name identifiers: ${label}")

	    
	    LinkedHashMap occurrencesMap = persv.getOccurrences()
	    LinkedHashMap resultsMap = persv.getValidationResults()
	    
	    table {
	      tr {
		th("Reference")
		th("Valid?")
		th("Occurs in")
	      }

	      resultsMap.keySet().each { pname ->
		tr {
		  td(pname)
		  td {
		    if (resultsMap[pname] ==  true) {
		      img(src : check)
		    } else {
		      img(src : del)
		    }
		  }
		  td {
		    ArrayList occurrences = occurrencesMap[pname]
		    ul {
		      occurrences.each {
			li(it)
		      }
		    }
		  }
		}
	      }
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }

  




  String getPlaceNamesReport(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("Place name identifiers: ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "Place name identifiers: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Place name identifiers: ${label}")

	    
	    LinkedHashMap occurrencesMap = placev.getOccurrences()
	    LinkedHashMap resultsMap = placev.getValidationResults()

	    if (occurrencesMap.size() > 0) {
	      table {
		tr {
		  th("Reference")
		  th("Valid?")
		  th("Occurs in")
		}

		resultsMap.keySet().each { pname ->
		  tr {
		    td(pname)
		    td {
		      if (resultsMap[pname] ==  true) {
			img(src : check)
		      } else {
			img(src : del)
		      }
		    }
		    td {
		      ArrayList occurrences = occurrencesMap[pname]
		      ul {
			occurrences.each {
			  li(it)
			}
		      }
		    }
		  }
		}
	      }
	    } else {
	      p("No place names found.")
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }








    String getEthnicNamesReport(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("Ethnic name identifiers: ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "Ethnic name identifiers: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Ethnic name identifiers: ${label}")

	    
	    LinkedHashMap occurrencesMap = ethnicv.getOccurrences()
	    LinkedHashMap resultsMap = ethnicv.getValidationResults()

	    if (occurrencesMap.size() > 0) {
	      table {
		tr {
		  th("Reference")
		  th("Valid?")
		  th("Occurs in")
		}

		resultsMap.keySet().each { pname ->
		  tr {
		    td(pname)
		    td {
		      if (resultsMap[pname] ==  true) {
			img(src : check)
		      } else {
			img(src : del)
		      }
		    }
		    td {
		      ArrayList occurrences = occurrencesMap[pname]
		      ul {
			occurrences.each {
			  li(it)
			}
		      }
		    }
		  }
		}
	      }
	    } else {
	      p("No ethnic names found.")
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }






      String getLexicalTokensReport(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("Lexical tokens: ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "Ethnic: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Ethnic: ${label}")

	    
	    LinkedHashMap occurrencesMap = lexv.getOccurrences()
	    LinkedHashMap resultsMap = lexv.getValidationResults()

	    if (occurrencesMap.size() > 0) {
	      table {
		tr {
		  th("Reference")
		  th("Valid?")
		  th("Occurs in")
		}

		resultsMap.keySet().each { pname ->
		  tr {
		    td(pname)
		    td {
		      if (resultsMap[pname] ==  true) {
			img(src : check)
		      } else {
			mkp.yield(resultsMap[pname])
			img(src : del)
		      }
		    }
		    td {
		      ArrayList occurrences = occurrencesMap[pname]
		      ul {
			occurrences.each {
			  li(it)
			}
		      }
		    }
		  }
		}
	      }
	    } else {
	      p("No lexical tokens found. (???)")
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }
}

