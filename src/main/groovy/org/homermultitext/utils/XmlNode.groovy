package org.homermultitext.utils

import java.text.Normalizer
import java.text.Normalizer.Form

/** XmlNode:  A class representing a well-formed XML node of text in Greek following markup conventions of the HMT project.
*/
class XmlNode {

  Integer debug = 0

  static String collectText(String xmlString)
  throws Exception {
    groovy.util.Node parsedNode
    try {
      parsedNode = new XmlParser().parseText(Normalizer.normalize(xmlString, Form.NFC))

    } catch (Exception e) {
      throw new Exception("GreekNode: could not parse content ${xmlString}")
    }
    return XmlNode.collectText(parsedNode)

  }

  /** Recursively walks through all descendants of an XML node
   * and collects the content of text nodes.
   * @param n The parsed node from which text will be extracted.
   * @return A String with the text content of the object node.
   */
  static String collectText(groovy.util.Node n) {
    return XmlNode.collectText(n,"",false)
  }


  /** Recursively walks through all descendants of an XML node
   * and collects the content of text nodes. In handling white space,
   * XML elements are taken to mark new, white-space delimited tokens
   * except where markup identified by the magicNode() method
   * groups together a token with mixed content model.
   * @param n The parsed node from which text will be extracted.
   * @param allText The String of previously accumulated text content,
   * to which the content of any further text nodes will be added.
   * @param inWord Flag indicating whether or not we're within a "magic" node
   * with mixed content model.
   * @return A String with the text content of the object node.
   */
  static String collectText(Object n, String allText, boolean inWord) {
    if (n.getClass().getName() == "java.lang.String") {
      allText = allText + n

    } else {
      if (magicNode(n)) {
	inWord = true
      }
      n.children().each { child ->
	if (!inWord) {
	  allText += " "
	}
	allText = collectText(child, allText,inWord)
      }
      if (magicNode(n)) {
	inWord = false
      }

      // normalize to NFC:
      return Normalizer.normalize(allText, Form.NFC)
    }
  }




  /** Determines if a given element is a normal element, or specially
   * identifies a wrapper for single tokens with mixed content models.
   * This implementation uses the F1K convention that elements named 'seg'
   * with a type attribute having the value 'word' uniquely groups tokens,
   * or words, with mixed content models.
   * @param n The node to examine.
   * @returns  True if the node is a wrapper for mixed content model.
   */
  static boolean magicNode(groovy.util.Node n) {
    String localName
    if (n.name() instanceof java.lang.String) {
      localName = n.name()
    } else {
      localName = n.name().getLocalPart()
  }
    if (localName == "w") {
      return true
    } else {
      return false
    }
  }


}
