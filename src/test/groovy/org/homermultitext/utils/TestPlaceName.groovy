package org.homermultitext.utils


import static org.junit.Assert.*
import org.junit.Test


class TestPlaceName extends GroovyTestCase {

  File tokens = new File("testdata/tokens/tokens.csv")
  File namesList = new File("testdata/authlists/hmtplaces.csv")
  
  
  
  void testPlaceNameValidation() {
    Integer expectedCount = 2
    Integer expectedFails = 0

    PlaceNameValidation pnv = new PlaceNameValidation(tokens, namesList, 0)

    // All valid tokens in this sampe, so:
    assert pnv.validates()
    assert pnv.tokensCount() == expectedCount
    assert pnv.successCount() == expectedCount
    assert pnv.failureCount() == expectedFails
  }

  void testBadPlaceName() {
    File badtokens = new File("testdata/tokens/badplace.csv")
    PlaceNameValidation pnv = new PlaceNameValidation(badtokens, namesList)
    // no valid tokens in this data set:
    assert pnv.tokensCount() == 0
  }
  
}