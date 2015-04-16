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



    public String echoStr(String raw) {
	HmtEditorialTokenization toker = new HmtEditorialTokenization();
	ArrayList tokenList = toker.splitString(raw);
	return (raw);
    }


    
    public String echoTab(String src)
    throws Exception {
	String buildPath = new java.io.File( "." ).getCanonicalPath() + docPath;
	try {
	    File f = new File(buildPath + src);
	    if (f.exists()) {
		HmtEditorialTokenization toker = new HmtEditorialTokenization();
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


