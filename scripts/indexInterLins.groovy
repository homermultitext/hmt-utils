/*
index interlinear glosses to text they comment on.
Input should an HMT edition of interlinear scholia.  Iliadic books are in tei <group>s; 
within these, documents are in tei <text> elements. The body of these contain tei
<div>s for each scholion.

 */


String usage = "groovy indexInterLins.groovy <XMLFILE>"

if (args.size() != 1) {
  System.err.println usage
  System.exit(-1)
}


String baseUrn = "urn:cts:greekLit:tlg5026.msAil.hmt:"

groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")
File xmlFile = new File(args[0])
def root = new XmlParser().parse(xmlFile)



root[tei.text][tei.group].each { bk ->
  String bkNum = bk.'@n'
  bk[tei.text][tei.body][tei.div].each { schol ->
    String scholRef = "${bkNum}." + schol.'@n'
    String iliadRef = ""
    String gloss = ""
    
    print "${baseUrn}${scholRef}"
    schol[tei.div].each { sect ->
      
      switch (sect.'@type') {
      case "ref":
      sect[tei.p].each {
	  iliadRef = it.text()
      }
      break

      case "comment":
      sect[tei.p].each {
	gloss = it.text()
      }
      gloss  = gloss.replaceAll("\n","")
      gloss  = gloss.replaceAll("[\t ]+"," ")
      
      break

      default:
      break
      }
    }
    println "\t${gloss}\t${iliadRef}"
  }

}

