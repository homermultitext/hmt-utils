package org.homermultitext.utils

import static org.junit.Assert.*
import org.junit.Test

class TestValidationTypes extends GroovyTestCase {


  void testTypes() {
    assert HmtValidatedType.values().size()  == 4
  }

}
