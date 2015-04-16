# Editorial tokenization system#

Compare diplomatic and editorial handling of:

- `supplied`
- `unclear`
- `gap` within `w`
- `del`
- `add`

### Within TEI `choice` element

Within `choice` elements:

- `orig` vs `reg`
- `expan` vs `abbr`
- `sic` vs `corr`




-




@openex@

### Examples: tabulated files ###

The tokenizer can operate on tabular files in the HMT project format.
Tokenizing <a href="../../../specs/data/ethnic.txt"  concordion:set="#ethnics = setHref(#HREF)">this data file</a> parses Its XML text node into a list of <strong  concordion:assertEquals="countTokensInTab(#ethnics)">8</strong> tokens.





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


