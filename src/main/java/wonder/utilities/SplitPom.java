package wonder.utilities;
/*
 * Splitpom was to introduce me to the Dom and get something useful out of 
 * the experience. After playing around with java nodes, I  realized most people
 * would just write a xls program; so I did: parent1 & child1. But then a simple
 * design pattern I learned from Mr. Google made it much simpler and more maintainable:
 * parent2 & child2.
 * 
 * 
 * 
 * Portions Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
 * reserved.
 * The Apache Software License, Version 1.1
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 **/

import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author gb
 *
 */
public class SplitPom {

	static Logger log = Logger.getLogger(SplitPom.class);
	static PrintWriter pw ;

	/** All output will use this encoding */
	static final String outputEncoding = "UTF-8";

	/** Output goes here */
	private PrintWriter out;

	/** Indent level */
	private int indent = 0;

	/** Indentation will be in multiples of basicIndent  */
	private final String basicIndent = "  ";

	/** Constants used for JAXP 1.2 */
	static final String JAXP_SCHEMA_LANGUAGE =
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA =
			"http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE =
			"http://java.sun.com/xml/jaxp/properties/schemaSource";

	SplitPom(PrintWriter out) {
		this.out = out;
	}

	private static void usage() {
		System.err.println("Usage: SplitPom [-options] <file.xml>");
		System.err.println("       -dtd = DTD validation");
		System.err.println("       -xsd | -xsdss <file.xsd> = W3C XML Schema validation using xsi: hints--but modPomList won't validate");
		System.err.println("           in instance document or schema source <file.xsd>");
		System.err.println("       -ws = do not create element content whitespace nodes");
		System.err.println("       -co[mments] = do not create comment nodes");
		System.err.println("       -cd[ata] = put CDATA into Text nodes");
		System.err.println("       -e[ntity-ref] = create EntityReference nodes");
		System.err.println("       -usage or -help = this message");
		System.exit(1);
	}

	public static void main(String[] args) throws Exception {

		Logger.getRootLogger().addAppender(null) ;
		String filename = null;
		boolean dtdValidate = false;
		boolean xsdValidate = false;
		String schemaSource = null;

		boolean ignoreWhitespace = false;
		boolean ignoreComments = false;
		boolean putCDATAIntoText = false;
		boolean createEntityRefs = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-dtd")) {
				dtdValidate = true;
			} else if (args[i].equals("-xsd")) {
				xsdValidate = true;
			} else if (args[i].equals("-xsdss")) {
				if (i == args.length - 1) {
					usage();
				}
				xsdValidate = true;
				schemaSource = args[++i];
			} else if (args[i].equals("-ws")) {
				ignoreWhitespace = true;
			} else if (args[i].startsWith("-co")) {
				ignoreComments = true;
			} else if (args[i].startsWith("-cd")) {
				putCDATAIntoText = true;
			} else if (args[i].startsWith("-e")) {
				createEntityRefs = true;
			} else if (args[i].equals("-usage")) {
				usage();
			} else if (args[i].equals("-help")) {
				usage();
			} else {
				filename = args[i];

				// Must be last arg
				if (i != args.length - 1) {
					usage();
				}
			}
		}
		if (filename == null) {
			usage();
		}

		// Step 1: create a DocumentBuilderFactory and configure it
		DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();

		// Set namespaceAware to true to get a DOM Level 2 tree with nodes
		// containing namesapce information.  This is necessary because the
		// default value from JAXP 1.0 was defined to be false.
		dbf.setNamespaceAware(true);

		// Set the validation mode to either: no validation, DTD
		// validation, or XSD validation
		dbf.setValidating(dtdValidate || xsdValidate);
		if (xsdValidate) {
			try {
				dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			} catch (IllegalArgumentException x) {
				// This can happen if the parser does not support JAXP 1.2
				System.err.println(
						"Error: JAXP DocumentBuilderFactory attribute not recognized: "
								+ JAXP_SCHEMA_LANGUAGE);
				System.err.println(
						"Check to see if parser conforms to JAXP 1.2 spec.");
				System.exit(1);
			}
		}

		// Set the schema source, if any.  See the JAXP 1.2 maintenance
		// update specification for more complex usages of this feature.
		if (schemaSource != null) {
			dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File(schemaSource));
		}

		// Optional: set various configuration options
		dbf.setIgnoringComments(ignoreComments);
		dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
		dbf.setCoalescing(putCDATAIntoText);

		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);

		// The opposite of creating entity ref nodes is expanding them inline
		dbf.setExpandEntityReferences(!createEntityRefs);

		// Step 2: create a DocumentBuilder that satisfies the constraints
		// specified by the DocumentBuilderFactory
		DocumentBuilder db = dbf.newDocumentBuilder();

		// Set an ErrorHandler before parsing
		OutputStreamWriter errorWriter =  new OutputStreamWriter(System.err, outputEncoding);
		db.setErrorHandler( new MyErrorHandler(new PrintWriter(errorWriter, true)));

		OutputStreamWriter outWriter = new OutputStreamWriter(System.out, outputEncoding);
		pw = new PrintWriter(outWriter, true) ; 


		// Step 3: parse the input file
		Document pomDoc = db.parse(new File(filename)) ;
		pomDoc.normalize() ;
		ClassLoader cl = SplitPom.class.getClassLoader();
		log.debug("cl is " + cl.toString()) ;
		
		// xsl looks like easier to maintain, so use it instead of messing with the DOM
		// get xsl stylesheets
		InputStream parentXSL = cl.getResourceAsStream("parent2.xsl") ;	
		InputStream childXSL = cl.getResourceAsStream("child2.xsl") ;
		StreamSource parent = new StreamSource(parentXSL);
		StreamSource child = new StreamSource(childXSL);
		
		// now get transformers
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer parentTransformer = tFactory.newTransformer(parent);
        Transformer childTransformer = tFactory.newTransformer(child);
        
        // now transform
        DOMSource source = new DOMSource(pomDoc);
        StreamResult result = new StreamResult(System.out);
        parentTransformer.transform(source, result);
        File gw = new File("./generic-woparent"); // create a dir
		if(!gw.mkdirs() ){
			log.info("Error creating directory; does it already exist?");
		}
		result = new StreamResult( new File("./generic-woparent/pom.xml"));
		parentTransformer.transform(source, result);
		result = new StreamResult( new File("./child.pom.xml"));
		childTransformer.transform(source, result);
		
/*
 * The commented out code will manipulate the DOM
 */
//		InputStream s =  cl.getResourceAsStream("modPomList.xml") ;
//
//		if( s == null){
//			log.debug(" no modPomList.xml ! ");
//			return;
//		}
//		Document modDoc = db.parse( s );
//
//		// create new docs for output
//		Document parentDoc = db.newDocument() ;
//		Document childDoc = db.newDocument() ;
//
//		makeFile( "generic-woparent/pom.xml", pomDoc, modDoc, "parentPom", parentDoc);
//		makeFile( "childpom.xml", pomDoc, modDoc, "childPom", childDoc);

	}


	/**
	 * @param fname
	 * @param pomDoc
	 * @param modDoc
	 * @param mtag
	 * @param outputDoc
	 */
	public static void makeFile(String fname, Document pomDoc,  Document modDoc, String mtag, Document outputDoc){

		// transfer all nodes , to create templates
		Node toAppend = outputDoc.importNode( pomDoc.getFirstChild(), true);
		dump( toAppend.getChildNodes(), pw);
		outputDoc.appendChild(toAppend) ; // put everything in new output doc

		// fix up--add nodes, modify, delete 
		// given docs, lets find the projects
		Node outn =  outputDoc.getFirstChild() ;
		if(  !(outn != null && outn.getNodeName().matches("project")) ){
			log.fatal("NO project in parent pom! ");
		}
		log.debug("outn is "+outn.getNodeName()) ;

		Node mn = modDoc.getFirstChild(); // what shall we modify?
		if( !mn.getNodeName().matches("modList")){
			log.fatal("NO modList in modlist.xml! ");
		}
		log.debug("first mod node is "+mn.getNodeName()) ;
		mn = ElementNodeUtilities.findSubNode(mtag, mn) ;
		log.debug("Now mn is "+mn.getNodeName()) ;
		mn = ElementNodeUtilities.findSubNode("project", mn) ;
		log.debug("Now mn is "+mn.getNodeName()) ;

		modifyWithList (  outn	, mn ) ;
		outputDoc.normalize() ;

		// one little fixup
		if(mtag.matches("parentPom") ){
			File gw = new File("./generic-woparent");
			if(!gw.mkdirs() ){
				log.error("Error creating directory");
			}
			// make parent versions match
			Node pn = ElementNodeUtilities.getNextNamedSibling( pomDoc.getFirstChild().getFirstChild() , "version" ) ;
			String version = ElementNodeUtilities.getText(pn) ; // the real version
			log.debug(" looking for version, found "+pn.getNodeName()+ ", value="+ElementNodeUtilities.getText(pn)) ;

			pn = ElementNodeUtilities.getNextNamedSibling( outputDoc.getFirstChild().getFirstChild() , "properties" ) ;
			new SplitPom(pw).echo(pn) ;
			pn =  ElementNodeUtilities.findSubNode("wonder.version", pn ) ;
			Node nn = pn.getOwnerDocument().createTextNode(version);
			pn.appendChild(nn);
			new SplitPom(pw).echo(pn) ;

			// more fixups: make dependancy project.version into wonder.version
			pn = ElementNodeUtilities.getNextNamedSibling( outputDoc.getFirstChild().getFirstChild() , "dependencyManagement" ) ;
			pn = ElementNodeUtilities.findSubNode("dependencies", pn).getFirstChild() ;
			new SplitPom(pw).echo(pn) ;
			while( pn != null){ 
				pn = ElementNodeUtilities.getNextNamedSibling(pn, "dependency") ;
				if( pn == null ) break ;
				log.debug("pn's artifactId is "+ElementNodeUtilities.getText( ElementNodeUtilities.findSubNode("artifactId", pn) )  ) ; 
				//new SplitPom(pw).echo(pn) ;
				Node vn = ElementNodeUtilities.findSubNode("version", pn);
				if( vn == null) {
					log.error("no version, shoud quit);") ;
					break;
				}
				else {
					String oversion = ElementNodeUtilities.getText(vn);
					if(oversion.matches("\\$\\{project\\.version}") ){ // change it
						vn.getFirstChild().setNodeValue("${wonder.version}") ;
					}
				}
			} ;
		}
		if(mtag.matches("childPom") ){
			// make parent versions match
			Node pn = ElementNodeUtilities.getNextNamedSibling( pomDoc.getFirstChild().getFirstChild() , "version" ) ;
			String version = ElementNodeUtilities.getText(pn) ;
			log.debug(" looking for version, found "+pn.getNodeName()+ ", value="+ElementNodeUtilities.getText(pn)) ;
			new SplitPom(pw).echo(pn) ;
			// now set value
			pn = outputDoc.getFirstChild() ;
			pn =  ElementNodeUtilities.findSubNode("parent", pn) ;
			pn =  ElementNodeUtilities.findSubNode("version", pn ) ;

			pn.getFirstChild().setNodeValue(version);
			new SplitPom(pw).echo(pn) ;

		}
		//new SplitPom(pw).echo(outn); // final
		// now write it out
		write( outputDoc, fname );

	}


	/**
	 * This looks at a node in the imput document, pn, and the a node in the modPomList.xml
	 * and makes modifications in  pn's document as directed by mn.
	 * @param pn - input pom's node
	 * @param mn - modPomList's node
	 */
	public static void modifyWithList(  Node pn, Node mn)  {

		if(  mn.getFirstChild() == null){
			log.error("Nothing on modlist for "+mn.getParentNode().getNodeName()) ;
			return;
		}
		Node parent = pn ;
		mn =  mn.getFirstChild() ; // should be ws

		// loop thru mod nodes and fix up template pom
		while( mn.getNextSibling() != null) {
			pn = parent.getFirstChild();
			if(mn.getNodeType() != Node.ELEMENT_NODE ){
				mn = ElementNodeUtilities.getNextSiblingElement(mn); // this we modify
			}
			log.debug("Now mn is "+mn.getNodeName()) ;

			//pn = ElementNodeUtilities.getNextSiblingElement(pn ) ; // skip 1st child, ws prob
			log.debug("Now pn is "+pn.getNodeName()) ;
			if( !pn.getNodeName().matches(mn.getNodeName() ) ) { // find matching node
				pn = ElementNodeUtilities.getNextNamedSibling( pn, mn.getNodeName()) ; 
			}
			if(pn == null){ // no matching node
				// add in node
				pn = ElementNodeUtilities.addNode(mn, parent) ;
				mn = mn.getNextSibling();
				continue;
			}
			log.debug("Now pn is "+pn.getNodeName()) ;

			// Do we remove it?
			if(ElementNodeUtilities.removeNode(mn, pn) ) {
				log.debug("removed node " + mn.getNodeName() );
				//new SplitPom(pw).echo(parent);
				mn = mn.getNextSibling();
				continue;			}

			// how many nodes?
			NodeList nl = mn.getChildNodes();
			log.debug("Node list has "+ Integer.valueOf( nl.getLength() ).toString() )  ;

			// check into node
			NodeList pnl = pn.getChildNodes()  ;
			if( pnl.getLength() == 1 && pn.getFirstChild().getNodeType() == Node.TEXT_NODE){
				// safe to replace it

				new SplitPom(pw).echo(pn);
				ElementNodeUtilities.replaceNode(  mn, pn);
				dump( pn.getChildNodes(), pw);
				Node newn = ElementNodeUtilities.findSubNode( mn.getNodeName(), parent);
				new SplitPom(pw).echo(newn);
				mn = mn.getNextSibling();
				continue;
			}else {
				// lets recurse
				modifyWithList( pn, mn) ;
				mn = mn.getNextSibling();
				continue ;
			}
		}	
	}

	/**
	 * @param doc
	 * @param filename
	 */
	public static void write(Document doc, String filename){

		try {
			// Use a Transformer for output
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			doc.normalize();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
			result = new StreamResult( new File(filename));
			transformer.transform(source, result);
		} catch (TransformerConfigurationException tce) {
			// Error generated by the parser
			System.out.println("\n** Transformer Factory error");
			System.out.println("   " + tce.getMessage());

			// Use the contained exception, if any
			Throwable x = tce;

			if (tce.getException() != null) {
				x = tce.getException();
			}

			x.printStackTrace();
		} catch (TransformerException te) {
			// Error generated by the parser
			System.out.println("\n** Transformation error");
			System.out.println("   " + te.getMessage());

			// Use the contained exception, if any
			Throwable x = te;

			if (te.getException() != null) {
				x = te.getException();
			}

			x.printStackTrace();
		}

	}

	public static void dump (NodeList nl, PrintWriter pw){
		for(int i = 0; i<nl.getLength() ; i++){
			Node n = nl.item(i);
			pw.println( "i="+Integer.valueOf(i).toString()+ " node name="+ n.getNodeName() ) ;
			if( n.getNodeName().matches("module") ){
				pw.println( "Text in "+n.getNodeName()+ ": "+ ElementNodeUtilities.getText(n ) ) ;
			}
		}
	}

	/**
	 * Echo common attributes of a DOM2 Node and terminate output with an
	 * EOL character.
	 */
	private void printlnCommon(Node n) {
		out.print(" nodeName=\"" + n.getNodeName() + "\"");

		String val = n.getNamespaceURI();
		if (val != null) {
			out.print(" uri=\"" + val + "\"");
		}

		val = n.getPrefix();
		if (val != null) {
			out.print(" pre=\"" + val + "\"");
		}

		val = n.getLocalName();
		if (val != null) {
			out.print(" local=\"" + val + "\"");
		}

		val = n.getNodeValue();
		if (val != null) {
			out.print(" nodeValue=");
			if (val.trim().equals("")) {
				// Whitespace
				out.print("[WS]");
			} else {
				out.print("\"" + n.getNodeValue() + "\"");
			}
		}
		out.println();
	}

	/**
	 * Indent to the current level in multiples of basicIndent
	 */
	private void outputIndentation() {
		for (int i = 0; i < indent; i++) {
			out.print(basicIndent);
		}
	}

	/**
	 * Recursive routine to print out DOM tree nodes
	 */
	private void echo(Node n) {
		// Indent to the current level before printing anything
		outputIndentation();

		int type = n.getNodeType();
		switch (type) {
		case Node.ATTRIBUTE_NODE:
			out.print("ATTR:");
			printlnCommon(n);
			break;
		case Node.CDATA_SECTION_NODE:
			out.print("CDATA:");
			printlnCommon(n);
			break;
		case Node.COMMENT_NODE:
			out.print("COMM:");
			printlnCommon(n);
			break;
		case Node.DOCUMENT_FRAGMENT_NODE:
			out.print("DOC_FRAG:");
			printlnCommon(n);
			break;
		case Node.DOCUMENT_NODE:
			out.print("DOC:");
			printlnCommon(n);
			break;
		case Node.DOCUMENT_TYPE_NODE:
			out.print("DOC_TYPE:");
			printlnCommon(n);

			// Print entities if any
			NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
			indent += 2;
			for (int i = 0; i < nodeMap.getLength(); i++) {
				Entity entity = (Entity)nodeMap.item(i);
				echo(entity);
			}
			indent -= 2;
			break;
		case Node.ELEMENT_NODE:
			out.print("ELEM:");
			printlnCommon(n);

			// Print attributes if any.  Note: element attributes are not
			// children of ELEMENT_NODEs but are properties of their
			// associated ELEMENT_NODE.  For this reason, they are printed
			// with 2x the indent level to indicate this.
			NamedNodeMap atts = n.getAttributes();
			indent += 2;
			for (int i = 0; i < atts.getLength(); i++) {
				Node att = atts.item(i);
				echo(att);
			}
			indent -= 2;
			break;
		case Node.ENTITY_NODE:
			out.print("ENT:");
			printlnCommon(n);
			break;
		case Node.ENTITY_REFERENCE_NODE:
			out.print("ENT_REF:");
			printlnCommon(n);
			break;
		case Node.NOTATION_NODE:
			out.print("NOTATION:");
			printlnCommon(n);
			break;
		case Node.PROCESSING_INSTRUCTION_NODE:
			out.print("PROC_INST:");
			printlnCommon(n);
			break;
		case Node.TEXT_NODE:
			out.print("TEXT:");
			printlnCommon(n);
			break;
		default:
			out.print("UNSUPPORTED NODE: " + type);
			printlnCommon(n);
			break;
		}

		// Print children if any
		indent++;
		for (Node child = n.getFirstChild(); child != null;
				child = child.getNextSibling()) {
			echo(child);
		}
		indent--;
	}

	// Error handler to report errors and warnings
	private static class MyErrorHandler implements ErrorHandler {
		/** Error handler output goes here */
		private PrintWriter out;

		MyErrorHandler(PrintWriter out) {
			this.out = out;
		}

		/**
		 * Returns a string describing parse exception details
		 */
		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}
			String info = "URI=" + systemId +
					" Line=" + spe.getLineNumber() +
					": " + spe.getMessage();
			return info;
		}

		// The following methods are standard SAX ErrorHandler methods.
		// See SAX documentation for more info.

		public void warning(SAXParseException spe) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(spe));
		}

		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
	}  

}//eoclass

