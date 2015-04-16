package hmtUtils.tokenization;


import org.concordion.integration.junit3.ConcordionTestCase;
import org.homermultitext.utils.*;

import java.util.ArrayList;

import java.io.File;

public class CommonTest extends ConcordionTestCase {

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

	HmtEditorialTokenization toker = new HmtEditorialTokenization();
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

    

    /** Gets an ordered list of tokens from a raw string.
     *  Working on a raw string only does white-space tokenization:
     * no contextual analysis.
     * @param raw String to tokenize.
     * @returns ArrayList of Strings.
     */
    public Iterable<String>  splitString(String raw) {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	return toker.splitString(raw);
    }

    public Iterable<String>  getTokens(String str, String urn)
    throws Exception {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	ArrayList analyses = toker.tokenizeString(str, urn, "");
	ArrayList tokens = new ArrayList();
	for (int i = 0; i < analyses.size(); i++ ) {
	    ArrayList analysis = ((ArrayList)analyses.get(i));
	    tokens.add(analysis.get(0));
	}
	return tokens;
    }

    public Iterable<String>  getTypes(String str, String urn)
    throws Exception {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	ArrayList analyses = toker.tokenizeString(str, urn, "");
	ArrayList tokens = new ArrayList();
	for (int i = 0; i < analyses.size(); i++ ) {
	    ArrayList analysis = ((ArrayList)analyses.get(i));
	    tokens.add(analysis.get(1));
	}
	return tokens;
    }

    public String  getType(String str, String urn, String tokenType)
    throws Exception {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	ArrayList analyses = toker.tokenizeString(str, urn, tokenType);
	String token = "";
	for (int i = 0; i < analyses.size(); i++ ) {
	    ArrayList analysis = ((ArrayList)analyses.get(i));
	    token = analysis.get(1).toString();
	}
	return token;
    }



    public String  getToken(String str, String urn, String tokenType)
    throws Exception {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	ArrayList analyses = toker.tokenizeString(str, urn, tokenType);
	String token = "";
	for (int i = 0; i < analyses.size(); i++ ) {
	    ArrayList analysis = ((ArrayList)analyses.get(i));
	    token = analysis.get(0).toString();
	}
	return token;
    }

    
    
    public Integer  countTokens(String str, String urn)
    throws Exception {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	return toker.tokenizeString(str, urn, "").size();
    }

    
    
}



