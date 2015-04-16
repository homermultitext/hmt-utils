# Editorial tokenization system#

The Editorial tokenization system works with archival editions of the Homer Multitext project.  Legal characters are the limited set of Unicode defined in the `greeklang` library (see [its specification](http://neelsmith.github.io/greeklang/specs/greek/tokens/Tokens.html)).  TEI markup is restricted to usage defined in the HMT project editorial guidelines.  Permitted characters and XML markup are further restricted by category of text.  As of version `@version@` of `hmt-utils`, the two recognized categories of HMT texts are "Homeric epic" and "secondary" texts.

The editorial tokenization system examines the XML markup to break up text content into an ordered series of pairings that match a substring of the source text with a token type.  Based on the text's category and token type, the substring is further validated against the standards of the `greeklang` library.

By default, text content is treated as a `GreekString`, which the `greeklang` library can split into lexical tokens. 


Special handling of other content depends on context.

## Allowed anywhere

- `persName`
-  `placeName`
- `w`
- `rs` (@type = 'ethnic')

Compare diplomatic and editorial handling of:

- `supplied`
- `unclear`
- `gap` within `w`
- `del`
- `add`

### `choice`s

Within `choice` elements:

- `orig` vs `reg`
- `expan` vs `abbr`
- `sic` vs `corr`




## Allowed in scholia only


- `num`: treated as `MilesianString`s in the `greeklang` library
- `ref` (@type = "urn" and @n=urn value)
- `q`
- `cit`
- `rs` (type = `waw`)
- `figDesc`
-  `note`


The contents of the following permitted TEI elements are omitted from analysis:



-




@openex@

### Examples: tabulated files.
Tokenize from a tabulated file: use this  file.
<a href="../../../specs/data/ethnic.txt"  concordion:set="#ethnics = setHref(#HREF)">this data file</a>

Its XML text node contains <strong  concordion:assertEquals="countTokensInTab(#ethnics)">8</strong> tokens

See <strong assertEquals="echo(#ethnics)">its file name</strong>



@closeex@

To find lexical entities within a raw string:

- verify that characters are valid
- split on white space
-  strip punctuation




## Splitting strings ##


Both the editorial and diplomatic tokenization systems include utility methods to split strings of text on white space.



@openex@

### Examples: splitting strings ###

The string

<pre concordion:set="#raw">Ζεὺς  δ' Ἔριδα  προΐαλλε θοὰς ἐπι νῆας Ἀχαιῶν</pre>

yields the following ordered set of tokens:



 <table concordion:verifyRows="#token : getTokensInString(#raw)">
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


