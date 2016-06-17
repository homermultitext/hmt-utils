package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn

class TestOrcaSample extends GroovyTestCase {


  // Real-world sample of complex markup from Iliadic text
  File tabsDir = new File("testdata/tabs/")
  File tabSrc = new File(tabsDir, "Iliad-small.txt")
  String separatorStr = "#"


  void testEditorialTokenizer() {
    HmtEditorialTokenization toker = new HmtEditorialTokenization()
    def analyses = toker.tokenizeTabFile(tabSrc,separatorStr)

    // test size of output:
    Integer expectedTokens = 70
    assert analyses.size() == expectedTokens

    def orcafied = toker.tokenizationToOrcaList(analyses)
    assert orcafied.size() == analyses.size()

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

  }

}
