# Some examples of usage for basic editorial tokenization

Until more formal documentation is complete, here are some brief examples of usage.



## Shortest possible route to happiness

### Constructor

No arguments needed:

    HmtEditorialTokenization analyzer = new HmtEditorialTokenization()


### From a tabular file

Supply a File of tabular data, and identify the string separating columns:

    File tabularFile = new File("FILENAME")
    String separator = "#"
    ArrayList analyses = analyzer.tokenizeTabFile(tabularFile,separator)



### Analyses to ORCA structures

The ordered list of analyses can be converted to an ordered list of ORCA class objects:

    ArrayList orcafied = toker.tokenizationToOrcaList(analyses)


## Under the hood

### Results of tokenization

For each of the above examples, the result of al is an ordered list of paired Strings.  The first is a CTS URN  value and the second is a classifying CITE URN value.  So you could see them all like this:

    analyses.each { analysis ->
     try {
      CtsUrn urn = new CtsUrn(analysis[0])
      CiteUrn analysisUrn = new CiteUrn(analysis[1])
      ... do your thing

    } catch (Exception e) {
      ... of course this will never happen
    }



### Results of orcafication

The result is an ordered list of ORCA objects (from the cite library).  All the member elements of an ORCA object are URNs of appropriate type.  Consider the following extract from a unit test:


    String expectedTg = "tlg0012"
    String expectedWork = "tlg001"
    String expectedVersion = "msA"
    String expectedAnalysisNs = "hmt"
    def expectedAnalysisTypes = ["tokentypes", "pers", "peoples"]
    orcafied.each { orca ->
      assert orca.passageAnalyzed.getTextGroup() == expectedTg
      assert orca.passageAnalyzed.getWork() == expectedWork
      assert orca.passageAnalyzed.getVersion() == expectedVersion
      assert orca.analysisObject.getNs() == expectedAnalysisNs
      assert expectedAnalysisTypes.contains(orca.analysisObject.getCollection())
    }

### Analyze a raw String

Given a raw string, the tokenizer needs to know the CTS URN of the passage; a context parameter can specify the current context (e.g, within a named entity for persons), or be empty as here for simple lexical tokens:

    String txt = "οὐλομένην· ἡ μυρί'"
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:1.2"
    String context = ""

    ArrayList analyses = analyzer.tokenizeString(txt, urn, context)

### Analyze a parsed groovy XML Node

This recursive method needs to know the current CTS URN of the passage; a context parameter can specify the current context (e.g, within a named entity for persons), or be empty as here for simple lexical tokens; and a boolean parameter indicates whether to continue or quit parsing if an exception is encountered.

    String tabLine =  """<l xmlns="http://www.tei-c.org/ns/1.0" n="2"><persName n="urn:cite:hmt:pers.pers712">Ἀντίλοχος</persName> δ' <persName n="urn:cite:hmt:pers.pers1">Ἀχιλῆϊ</persName> πόδας ταχὺς ἄγγελος ἦλθε·</l>"""
    def lineRoot = new XmlParser().parseText(tabLine)

    String context = ""
    String urn = "urn:cts:greekLit:tlg0012.tlg001.msA:18.2"
    boolean continueOnException = false

    ArrayList analyses = analyzer.tokenizeElement(lineRoot, urn, context,continueOnException)
