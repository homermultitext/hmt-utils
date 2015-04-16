# Components shared by all tokenization systems #


The HMT tokenization systems work with archival editions of the Homer Multitext project.  Legal characters are the limited set of Unicode defined in the `greeklang` library. (See [its specification](http://neelsmith.github.io/greeklang/specs/greek/tokens/Tokens.html)).  TEI markup is restricted to usage defined in the HMT project editorial guidelines.  Permitted characters and XML markup are further restricted by category of text.  


By default, text content is treated as a `GreekString`, which the `greeklang` library can split into lexical tokens. 


## Universally allowed elements and their mapping to token types ##


- `persName`
-  `placeName`
- `w`
- `rs` (@type = 'ethnic')


## Elements allowed in "secondary" texts, but not in *Iliad* editions ##



- `num`: treated as `MilesianString`s in the `greeklang` library
- `ref` (@type = "urn" and @n=urn value)
- `q`
- `cit`
- `rs` (type = `waw`)
- `figDesc`
-  `note`

