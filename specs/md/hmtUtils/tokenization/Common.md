# Components shared by all tokenization systems #

## Input and output ##

The tokenization process applies to passages of texts identified by a CTS URN.  The tokenizers support two forms of input:

1.  a *string of text*  to parse, accompanied by the passage's URN, and optionally by a CITE URN identifying the kind of context
2.  a *delimited text file* representing the full OHCO2 model of a text as defined in the `hocuspocus` library (<http://cite-architecture.github.io/hocuspocus/>)

In both cases, the output is an ordered list of token analyses. Each token analysis has two parts, a CTS URN for the substring, and a CITE URN analyzing the type of the token.



## Parsing strings of texts ##


At a minimum, a request to tokenize a string of text must identify the CTS URN as well as the text content to analyze.  In addition, it is possible to identify the type of context being analyzed with a CITE URN taken from one of the values   `urn:cite:hmt:tokentypes.lexical`, `urn:cite:hmt:tokentypes.numeric`, `urn:cite:hmt:tokentypes.waw`, `urn:cite:hmt:tokentypes.sic`, or by a CITE URN in either of the two collections `urn:cite:hmt:place` or `urn:hmt:pers`.    If the context is *not* identified with one of these URNs, the content of XML text nodes is treated by default as a `GreekString` object, which the `greeklang` library can split into lexical tokens. 

@openex@

### Examples ###

If we parse the string of characters, <em concordion:set="#str4">προΐαλλε θοὰς ἐπι νῆας</em>
from the text passage <strong concordion:set="#urn">urn:cts:greekLit:tlg0012.tlg001.msA:11.3</strong>,
we get an ordered list of <strong concordion:assertEquals="countTokens(#str4,#urn)">4</strong> tokens.


<table concordion:verifyRows="#token : getTokens(#str4,#urn)">
<tr><th concordion:assertEquals="#token">Token string</th></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@προΐαλλε</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@θοὰς</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@ἐπι</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@νῆας</td></tr>

</table>


Their types are all the same:


<table concordion:verifyRows="#token : getTypes(#str4,#urn)">
<tr><th concordion:assertEquals="#token">Type</th></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>

</table>

If we parse the string <em concordion:set="#zeus">Ζεὺς</em> using the same CTS URN, and specify the context with the CITE URN <strong concordion:set="#pers8">urn:cite:hmt:pers.pers8</strong>, this is analyzed as:

- token <strong concordion:assertEquals="getToken(#zeus,#urn,#pers8)">urn:cts:greekLit:tlg0012.tlg001.msA:11.3@Ζεὺς</strong> 
- type  <strong concordion:assertEquals="getType(#zeus,#urn,#pers8)">urn:cite:hmt:pers.pers8</strong>



@closeex@


By default, requests to tokenize a string do not include explicit index values on the CTS URN substrings, since the most common use may not necessarily be to analyze an entire citable node of text, but explicit subreference indexing can optionally be included.



@openex@

tis t'ar sfwe qewn eridi cunehke maxesqai;

@closeex@


## Parsing delimited text files ##


The tabular representation of the HMT project editions preserves the full XML markup of the archival TEI documents, so the HMT tokenizers  can identify the appropriate context from the HMT project's markup conventions.   They can therefore formulate requests with both the CTS URN for the text passage, and a CITE URN classifying the context.  Analyses of delimited text files always include explicit indexes on CTS URN subreferences.

@openex@

### Examples ###


Tokenizing <a href="../../../specs/data/ethnic.txt"  concordion:set="#ethnics = setHref(#HREF)">this data file</a> parses Its XML text node into a list of <strong  concordion:assertEquals="countTokensInTab(#ethnics)">8</strong> tokens.



<table concordion:verifyRows="#token : getTokensInTab(#ethnics)">
<tr><th concordion:assertEquals="#token">Token string</th></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@Ζεὺς[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@δ'[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@Ἔριδα[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@προΐαλλε[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@θοὰς[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@ἐπι[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@νῆας[1]</td></tr>
<tr><td>urn:cts:greekLit:tlg0012.tlg001.msA:11.3@Ἀχαιῶν[1]</td></tr>
</table>

Their types are: 

<table concordion:verifyRows="#token : getTypesInTab(#ethnics)">
<tr><th concordion:assertEquals="#token">Types</th></tr>
<tr><td>urn:cite:hmt:pers.pers8</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:pers.pers156</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:tokentypes.lexical</td></tr>
<tr><td>urn:cite:hmt:peoples.place96</td></tr>
</table>

@closeex@




## Universally allowed elements and their mapping to token types ##

Elements to test:

- `w`

Illustrated above:

- `persName`
-  `placeName`
- `rs` (@type = 'ethnic')


## Elements allowed in "secondary" texts, but not in *Iliad* editions ##



- `num`: treated as `MilesianString`s in the `greeklang` library
- `ref` (@type = "urn" and @n=urn value)
- `q`
- `cit`
- `rs` (type = `waw`)
- `figDesc`
-  `note`












## Splitting strings ##


Both the editorial and diplomatic tokenization systems include utility methods to split strings of text on white space.



@openex@

### Examples ###

The string

<pre concordion:set="#raw">Ζεὺς  δ' Ἔριδα  προΐαλλε θοὰς ἐπι νῆας Ἀχαιῶν</pre>

yields the following ordered set of tokens:



 <table concordion:verifyRows="#token : splitString(#raw)">
<tr><th concordion:assertEquals="#token">Token string</th></tr>

<tr><td>Ζεὺς</td></tr>
<tr><td>δ'</td></tr>
<tr><td>Ἔριδα</td></tr>
<tr><td>προΐαλλε</td></tr>
<tr><td>θοὰς</td></tr>
<tr><td>ἐπι</td></tr>
<tr><td>νῆας</td></tr>
<tr><td>Ἀχαιῶν</td></tr>
</table>


@closeex@



