package org.homermultitext.utils


/** Class defining some convenience variables for referring to Unicode
 * code points.
 */
class HmtDefs {

  /** Greek high stop, hex x387. */
  static int highstop = 903

  /** Latin mid-dot, xB7 */
  static int anotherHighStop = 183

  /** Double dagger, used in HMT for ... x2021 */
  static int doubledagger = 8225

  /** Punctuation characters defined in this class. */
  static ArrayList puncChars = [highstop,doubledagger,anotherHighStop]

}
