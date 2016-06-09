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

  /** Set of validations, keyed by HmtValidatedType. */
  def validationSet = [:]

  
  /** Constructor.
   */
  HmtValidator() {
  }

  void addResults(Object hmtvtype, HmtValidation hmtvresult) {
    //    def pair = [hmtvtype,hmtvresult]
    validationSet [hmtvtype] = hmtvresult // += pair
  }

  
}


      