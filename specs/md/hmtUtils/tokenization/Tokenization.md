# Tokenization of Greek texts #


## Overview ##

The `hmt-utils` library includes multiple ways of analyzing the lexical content of editions and translations in the HMT project's archival collection.

The HMT project archives its texts as XML documents complying with version [P5 of the TEI guidelines](http://www.tei-c.org/Guidelines/).   (For details about the format of these archival editions, see the HMT project's [guide for editors](http://homermultitext.github.io/hmt-editors-guide/).)   In contrast to earlier versions of the TEI guidelines, version P5 does not permit editors to organize the text nodes of an XML edition to represent a single coherent version of a text:  the TEI `choice` element is the only way to capture alternate views (such as abbreviated vs. expanded forms, or editorial additions and deletions), and places mutually incompatible readings in the XML document's text nodes.  

For editions of Greek texts, the tokenization options of the `hmt-utils` address this by analyzing only the text content that is relevant for one of two perspectives on the XML source edition:  

- a *pure diplomatic view* representing exactly what is read in the manuscript  (e.g., abbreviations unexpanded, and Byzantine orthographic conventions recorded)
- an *editorial view* representing a diplomatic edition as presented to modern readers (e.g., abbreviations expanded, and orthography regularized to modern conventions)



The HMT utilities examine the XML markup from one of these perspectives to break up text content into an ordered series of pairings that match a substring of the source text with a token type.  By default, text content is treated as a `GreekString`, which the `greeklang` library can split into lexical tokens.   The HMT utilities take XML markup into consideration in order to identify other types of token  (e.g., numbers, or named entities).  In addition,  the HMT utilities impose restrictions based on the identification of the textual passage being analyzed:    while many markup options apply to all texts in the HMT archive, some are specific to Homeric poetry, or to secondary texts.    As of version `@version@` of `hmt-utils`, the two recognized categories of HMT texts are "Homeric epic" and "secondary" texts.

Based on the text's category and token type, each substring identified by the tokenization is further validated against the standards of the `greeklang` library.  


## Specification of character set and markup ##

 Legal characters for input and output are the limited set of Unicode defined in the `greeklang` library (see [its specification](http://neelsmith.github.io/greeklang/specs/greek/tokens/Tokens.html)).  All output is in Unicode NFC form.  TEI markup is restricted to usage defined in the HMT project editorial guidelines, which defines allowed characters and XML markup for each category of text.   As of version `@version@` of `hmt-utils`, the two recognized categories of HMT texts are "Homeric epic" and "secondary" texts.

## Classification of tokens ##


The following types or classes of tokens are recognized:

- *lexical* tokens
- *punctuation* tokens
- *numeric* tokens
- *personal names*
- *geographic names*
- *literal string of characters* ("word as word")
- *unintelligible* tokens ("*sic*")


## Tokenization systems

See detailed specification of:

1. features common to <a concordion:run="concordion" href="Common.html">all HMT tokenization systems</a>
1. tokenization of <a concordion:run="concordion" href="Diplomatic.html">pure diplomatic text</a>
2. tokenization of <a concordion:run="concordion" href="Editorial.html">editorial text</a>



