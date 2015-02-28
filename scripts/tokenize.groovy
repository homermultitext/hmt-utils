/*
* A groovy CLI script for tokenizing texts following HMT project conventions.
*
* Prerequisites:  you must have the following libraries on your classpath:
* (for now, at least):
*
* hmt-utils.jar
* cite.jar
* f1k.jar
* 
* Usage: groovy tokenize.groovy TABSDIR OUTFILE 
*
*/

import org.homermultitext.utils.HmtTokenizer

String separator = "#"


if (args.size() != 2) {
   System.err.println "Usage: groovy tokenize.groovy TABSDIR OUTFILE"
} else {
  File tabs = new File(args[0])
  File tokens = new File(args[1])
  HmtTokenizer toker = new HmtTokenizer(tabs, tokens, separator)
  toker.tokenizeTabs()
}

