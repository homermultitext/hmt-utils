package org.homermultitext.utils



import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
import org.homermultitext.citemanager.DseManager

import groovy.xml.StreamingMarkupBuilder

import edu.holycross.shot.safecsv.SafeCsvReader


/**  A class for assessing compliance of text contents with
 * HMT project conventions.  The methods further analyze the output of
 * a HMT project classified tokenization, depending on the class
 * assigned to each token.
*/
class HmtValidator  {

  int debug = 0

  /** Icon used in HTML reports for bad tokens. */
  String del = "http://www.homermultitext.org/delete.png"
  /** Icon used in HTML reports for good tokens. */
  String check = "http://www.homermultitext.org/check.png"


  // Classes implementation the HmtValidation interface
  /** Validation of personal names. */
  PersNameValidation persv
  /** Validation of place names. */
  PlaceNameValidation placev
  /** Validation of names of ethnic groups. */
  EthnicNameValidation ethnicv
  /** Validation of lexical tokens. */
  LexicalValidation lexv


  ArrayList badStrings = []
  
  /** Map of output file names to Validation objects. */
  LinkedHashMap validations = [:]  

  /** Constructor requiring data sources for complete validation.
   * @param tokens File with output of a HMT project tokenization.
   * @param authListsDir "data" subdirectory of a clone of the hmt-authlists
   * repository. This is the source for valid identifiers for all named entities.
   * @param byzOrtho "orthoequivs.csv" file in the byzortho repository.
   * @param morphCmd String name of a parser to execute with "command.execute()"
   * (in the LexicalValidation class).
   */
  HmtValidator(File tokens, File authListsDir, File byzOrtho, File lexMapping, String morphCmd) {

    persv = new PersNameValidation(tokens, new File(authListsDir, "hmtnames.csv"))
    validations["personalnames.html"] = (persv)
    placev = new PlaceNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["placenames.html"] = placev
    ethnicv = new EthnicNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["ethnicnames.html"] = ethnicv
    lexv = new LexicalValidation(tokens, byzOrtho, lexMapping, morphCmd)
    validations["lexicaltokens.html"] = lexv

    tokens.eachLine { l ->
      if (debug > 2) { System.err.println "Check " + l}
      if (l ==~ /urn:cite:hmt:error.badGreekMsString/) {
	badStrings.add(l)
	if (debug > 0) { System.err.println "\tadded ${l} to " + badStrings}
      }
    }
  }



  HmtValidator(File tokens, File authListsDir, File byzOrtho, File lexMapping, String morphCmd, File logFile) {

    persv = new PersNameValidation(tokens, new File(authListsDir, "hmtnames.csv"))
    validations["personalnames.html"] = (persv)
    placev = new PlaceNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["placenames.html"] = placev
    ethnicv = new EthnicNameValidation(tokens, new File(authListsDir, "hmtplaces.csv"))
    validations["ethnicnames.html"] = ethnicv
    lexv = new LexicalValidation(tokens, byzOrtho, lexMapping, morphCmd, logFile)
    validations["lexicaltokens.html"] = lexv

    tokens.eachLine { l ->
      if (debug > 2) { System.err.println "Check " + l}
      if (l ==~ /.+urn:cite:hmt:error.badGreekMsString.*/) {
	badStrings.add(l)
	if (debug > 1) { System.err.println "\tadded ${l} to " + badStrings}
      }
    }

  }


  
  /** Writes a coordinated suite of HTML reports.
   * @param reportsDir A writable directory where the output
   * will be written.
   * @param label A String to use as the base of a file name for the
   * summary report, and in constructing labels within the HTML
   * reports.  Conventionally, this should identify the physical 
   * text-bearing surface or surfaces whose tokenized content is
   * being analyzed by HmtValidator methods.
   */
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

    File badstrings = new File(reportsDir, "invalidstrings.html")

    badstrings.setText(getBadStrings(label), "UTF-8")

    
  }

  

  /** Constructs a String of HTML for a page summarizing results
   * of all validations.
   * @param label A String to use as the base of a file name for the
   * summary report.  Conventionally, this should identify the physical 
   * text-bearing surface or surfaces whose tokenized content is
   * being analyzed by HmtValidator methods.
   */
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


	      validations.keySet()?.sort().each { k ->
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
	      tr {
		if (badStrings.size() > 0) {
		  td("Invalid Greek strings (${badStrings.size()} tokens)")
		  td {
		    img(src : del)
		  }
		  td {
		    a (href: "invalidstrings.html", "see details")
		  }
		} else {
		  td ("Tokenizer found no invalid Greek strings")
		  td {
		    img(src : check)
		  }
		  td()
		}
	      }
	    }
	  }
	}
      }
    }
    return reportXml.toString()
  }

  
  /** Constructs a String of HTML for a page detailing results of
   * validation of personal names.
   * @param label A String used within the report to identify
   * the physical text-bearing surface or surfaces whose tokenized content is
   * being analyzed.
   */
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

	      resultsMap.keySet()?.sort().each { pname ->
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
		      occurrences?.sort().each {
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

  



  /** Constructs a String of HTML for a page detailing results of
   * validation of place names.
   * @param label A String used within the report to identify
   * the physical text-bearing surface or surfaces whose tokenized content is
   * being analyzed.
   */
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

		resultsMap.keySet()?.sort().each { pname ->
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
			occurrences?.sort().each {
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







  /** Constructs a String of HTML for a page detailing results of
   * validation of names of ethnic groups.
   * @param label A String used within the report to identify
   * the physical text-bearing surface or surfaces whose tokenized content is
   * being analyzed.
   */
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

		resultsMap.keySet()?.sort().each { pname ->
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
			occurrences?.sort().each {
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
	    mkp.yield "Lexical tokens: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Lexical tokens: ${label}")
	    LinkedHashMap occurrencesMap = lexv.getOccurrences()
	    LinkedHashMap resultsMap = lexv.getValidationResults()


	    Integer count = 0
	    h2("Failures: ${lexv.failureCount()} distinct tokens not analyzed")

	    if (lexv.failureCount() > 0) {
	      table {
		tr {
		  th("Reference")
		  th("Valid?")
		  th("Occurs in")
		}

		resultsMap.keySet()?.sort().each { fullRef ->
		  String lex
		  if (resultsMap[fullRef] == "fail") {
		    CtsUrn tokenUrn
		    try {
		      tokenUrn = new CtsUrn(fullRef)
		      lex = tokenUrn.getSubref()
		    } catch (Exception e) {
		      //throw e
		      lex = "Invalid URN: " + tokenUrn
		    }

		    count++
		    tr {
		      td("${count}.  ${lex}")
		      td {
			mkp.yield(resultsMap[fullRef])
			img(src : del)
		      }
		      td {
			ArrayList occurrences = occurrencesMap[lex]
			ul {
			  occurrences?.sort().each {
			    li(it)
			  }
			}
		      }
		    }
		  }
		}
		// content for table of failures goes above this
	      }
	    }

	    if (lexv.successCount() > 0) {
	      count = 0
	      h2("Successes: ${lexv.successCount()} distinct tokens analyzed")

	      table {
		tr {
		  th("Reference")
		  th("Valid?")
		  th("Occurs in")
		}
		resultsMap.keySet()?.sort().each { fullRef ->
		  String lex
		  if (resultsMap[fullRef] != "fail") {
		    count++
		    CtsUrn tokenUrn
		    try {
		      tokenUrn = new CtsUrn(fullRef)
		      lex = tokenUrn.getSubref()
		    } catch (Exception e) {
		      //throw e
		      lex = "Invalid URN: " + tokenUrn
		    }

		    tr {
		      td("${count}.  ${lex}")
		      td {
			if (resultsMap[fullRef] ==  "success") {
			  img(src : check)
			} else if (resultsMap[fullRef] ==  "byz") {
			  img(src : check)
			  mkp.yield("(byzantine orthography)")
			} else if (resultsMap[fullRef] ==  "punctuation") {
			  img(src : check)
			  mkp.yield("(punctuation)")
			} else {
			  mkp.yield(resultsMap[fullRef])
			  img(src : del)
			}
		      }
		      td {
			ArrayList occurrences = occurrencesMap[lex]
			ul {
			  occurrences?.sort().each {
			    li(it)
			  }
			}
		      }
		    }
		  }
		}
		// content for success table goes above this
	      }
	    }

	  }
	}
      }
    }
    return reportXml.toString()
  }

  /** Constructs a String of HTML for a page listing tokens
   * with invalid Greek string values.
   * @param label A String used within the report to identify
   * the physical text-bearing surface or surfaces whose tokenized content is
   * being analyzed.
   */
  String getBadStrings(String label) {
    def reportXml = new groovy.xml.StreamingMarkupBuilder().bind {
      html {
	head {
	  title ("Invalid Greek string values: ${label}")
	  link(type: "text/css", rel: "stylesheet", href: "css/hmt-core.css", title: "HMT CSS")
	}
	body {
	  header(role: "banner") {
	    mkp.yield "Invalid Greek string values: ${label}"
	    nav(role: "navigation") {
	      ul {
		li {
		  a(href: "${label}.html", "summary of ${label}")
		}
	      }
	    }
	  }
	  article(role: "main") {
	    h1("Invalid Greek string values: ${label}")
	    ul {
	      badStrings.each { bad ->
		String badreport
		def parts = bad.split('@')
		if (parts.size() < 2) {
		  badreport = "?? can't make sense of " + bad
		} else {
		  badreport = "${parts[1].replaceFirst(/\[.+/,'')} from ${parts[0]}"
		}
		li(badreport)
	      }
	    }
	  }
	}
      }
    }
    return reportXml.toString()    
  }

  
}

      