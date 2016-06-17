package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.cite.CtsUrn


class TestTokenizationChoice extends GroovyTestCase {


  String urnBase = "urn:cts:greekLit:tlg5026.msA.hmt:18.185"

  // Source XML:
  String abbrExpanChoice = """<div  type='lemma'>
<p>τεῖχος μέν ρ' ἄλοχοι τε φίλαι καὶ
<choice> <abbr>νηπ</abbr><expan>νήπια</expan></choice>
τέκνα ῥύατ' ἐφεσταῶτος\u0387</p>
</div>
"""
  // Expected editorial tokenization:
  def expectedLexTokens = ["τεῖχος", "μέν", "ρ'", "ἄλοχοι", "τε", "φίλαι",
			"καὶ", "νήπια", "τέκνα", "ῥύατ'", "ἐφεσταῶτος"]


  void testEditorialChoices() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    def elementRoot = new XmlParser().parseText(abbrExpanChoice)
    def tokens = toker.tokenizeElement(elementRoot, urnBase, "", true)

    // 11 words and one punctuation token:
    assert tokens.size() == 12

    def trailingPunct = tokens[11]
    assert trailingPunct[1] == "urn:cite:hmt:tokentypes.punctuation"

    def lextokens = []
    tokens.each { t ->
      if (t[1] == "urn:cite:hmt:tokentypes.lexical") {
	CtsUrn urn = new CtsUrn(t[0])
	lextokens.add(urn.getSubref())
      }
    }
    assert lextokens.size() == 11
    lextokens.eachWithIndex { t, i ->
      assert t == expectedLexTokens[i]
    }


  }

}
