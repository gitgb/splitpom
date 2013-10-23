package wonder.utilities;

import org.w3c.dom.*;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author gb
 *
 */
public class ElementNodeUtilities {

	static Logger log = Logger.getLogger(ElementNodeUtilities.class);

	public static Node  getNextSiblingElement(Node n){
		log.debug("Now n is "+n.getNodeName()) ;
		if( n.getNextSibling() == null) return null;
		n = n.getNextSibling() ;
		log.debug("Now n is "+n.getNodeName()) ;
		if ( n.getNodeType() !=  Node.ELEMENT_NODE ) return getNextSiblingElement(n);
	return n;
	}

	/**
	 * @param n
	 * @param name
	 * @return
	 */
	public static Node getNextNamedSibling(Node n,  String name){

		// Siblings exist?
		Node r = getNextSiblingElement(n) ;
		while( r != null && !r.getNodeName().matches(name)){ // search
			r = getNextSiblingElement(r) ;
			if(r == null) return r;
			log.debug("loop r is now "+r.getNodeName() ) ;
			
		}
		return r ;
	}
	
	/**
	 * @param mn
	 * @param pn
	 */
	public static void replaceNode(  Node mn, Node pn){
		log.debug("Will replace node "+ pn.getNodeName() + ", current text is "+ getText(pn) );
		// process node for new doc
 
		Node nnew = pn.getOwnerDocument().importNode(mn, true);
		// now remove old node, 
		Node parentNode = pn.getParentNode();
		
		//parentNode.removeChild(pn); 
		parentNode.replaceChild(nnew, pn);
		
	}

	/**
	 * @param mn
	 * @param pn
	 * @return
	 */
	public static boolean removeNode(  Node mn, Node pn){
		if( getText(mn).trim().matches("REMOVE") ){
			pn.getParentNode().removeChild(pn);
			return true;
		}
		return false;
		
	}
	
	/**
	 * @param mn
	 * @param pn
	 * @return
	 */
	public static Node addNode(  Node mn, Node pn){
		// pn has to be parent
		log.debug("Will add node "+ mn.getNodeName() + ", current text is "+ getText(mn) );
		// process node for new doc
 
		Node nnew = pn.getOwnerDocument().importNode(mn, true);

		return pn.appendChild(nnew);
		
		
	}
	
		/**
	}
	 * Return the text that a node contains. This routine:
	 * <ul>
	 * <li>Ignores comments and processing instructions.
	 * <li>Concatenates TEXT nodes, CDATA nodes, and the results of
	 *     recursively processing EntityRef nodes.
	 * <li>Ignores any element nodes in the sublist.
	 *     (Other possible options are to recurse into element 
	 *      sublists or throw an exception.)
	 * </ul>
	 * @param    node  a  DOM node
	 * @return   a String representing its contents
	 */
	public static String getText(Node node) {
		StringBuffer result = new StringBuffer();
		if (! node.hasChildNodes()) return "";

		NodeList list = node.getChildNodes();
		for (int i=0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.TEXT_NODE) {
				result.append(subnode.getNodeValue());
			}
			else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
				result.append(subnode.getNodeValue());
			}
			else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
				// Recurse into the subtree for text
				// (and ignore comments)
				result.append(getText(subnode));
			}
		}

		return result.toString();
	}



	/**
	 * Find the named subnode in a node's sublist.
	 * <ul>
	 * <li>Ignores comments and processing instructions.
	 * <li>Ignores TEXT nodes (likely to exist and contain
	 *         ignorable whitespace, if not validating.
	 * <li>Ignores CDATA nodes and EntityRef nodes.
	 * <li>Examines element nodes to find one with
	 *        the specified name.
	 * </ul>
	 * @param name  the tag name for the element to find
	 * @param node  the element node to start searching from
	 * @return the Node found
	 */
	public static  Node findSubNode(String name, Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			System.err.println("Error: Search node not of element type");
			//  System.exit(22);
		}

		if (! node.hasChildNodes()) return null;

		NodeList list = node.getChildNodes();
		for (int i=0; i < list.getLength(); i++) {
			Node subnode = list.item(i);
			System.out.println( "Node "+ subnode.toString() ) ;
			if (subnode.getNodeType() == Node.ELEMENT_NODE) {
				if (subnode.getNodeName().equals(name)) 
					return subnode;
			}
		}
		return null;
	}

}// eoc
