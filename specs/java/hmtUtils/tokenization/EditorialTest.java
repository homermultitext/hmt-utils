package hmtUtils.tokenization;


import org.concordion.integration.junit3.ConcordionTestCase;
import org.homermultitext.utils.*;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.SortedSet;
import edu.holycross.shot.greekutils.GreekWord;

import java.io.File;

public class EditorialTest extends ConcordionTestCase {

    
    String docPath = "/build/concordion-results/hmtUtils/tokenization/";
    String separatorStr = "#";

    /** Hands back a String parameter so we can save links using concordion's
     * #Href variable for use in later computations. */
    public String setHref(String path) {
	return (path);
    }



    /** Reads a HMT tabulated file, and counts tokens.
     * @param src Name of file, as a String.
     * @returns Number of tokens.
     */
    public Integer countTokensInTab(String src) 
	throws Exception {
	Integer total = -1;

	HmtGreekTokenization toker = new HmtGreekTokenization();
	String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	File f = new File(buildPath + src);
	if (f.exists()) {
	    try {
		ArrayList results = toker.tokenizeTabFile( f,  separatorStr);
		System.err.println("RESULSTS IS A " + results.getClass() + " of size " + results.size());
		total = results.size();
	    } catch (Exception e) {
		System.err.println("TokenizationTest: catastrophe " + e.toString());		
	    }
	} else {
	    throw new Exception("TokenizationTest: no such file " + f);
	}
	return total;
    }

    public String echoStr(String raw) {
	HmtGreekTokenization toker = new HmtGreekTokenization();
	ArrayList tokenList = toker.splitString(raw);
	return (raw);
    }


    
    /** Gets an ordered list of tokens from a raw string.
     *  Working on a raw string only does white-space tokenization:
     * no contextual analysis.
     * @param raw String to tokenize.
     * @returns ArrayList of Strings.
     */
    public Iterable<String>  getTokensInString(String raw) {
	HmtGreekTokenization toker = new HmtGreekTokenization();
	return toker.splitString(raw);
    }

    
    public String echoTab(String src)
    throws Exception {
	String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	try {
	    File f = new File(buildPath + src);
	    if (f.exists()) {
		HmtGreekTokenization toker = new HmtGreekTokenization();
		ArrayList results = toker.tokenizeTabFile( f,  separatorStr);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < results.size();  i++) {
		    //ArrayList elem = results.get(i);
		    //sb.append("There are " + elem.size() + " components in "  );
		    sb.append(results.get(i).toString() + "\n");
		}

		return (sb.toString());
	    } else {
		return ("From docpath " + docPath + ", file " + src + " does not exist.");
	    }
	} catch (Exception e) {
	    System.err.println ("Yikes, exception. " + e.toString());
	}
	return ("Something did not go well.");
    }

    
}


