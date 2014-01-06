SplitPom - create parent and child poms.
========================================

Takes the wonder main pom as input and modifies or refactors it by splitting it into 2 poms: 
./generic-woparent/pom.xml and ./child.pom.xml. 

The parent pom has information useful to build any womaven project. You get all this wonderful info by referring
to the generated parent pom. The main thing here is to create the parent pom then reference it in your project pom.

It is very good to not have to track all the different versions for all these jars; the parent pom does this for you.

This splitting also adds a useful plugin to help manage versions, and here that addition is explained:
 http://mojo.codehaus.org/versions-maven-plugin/

```
			<plugin>
				<!-- use at: http://mojo.codehaus.org/versions-maven-plugin/ -->
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>versions-maven-plugin</artifactId>
				<version>1.3.1</version>
			</plugin>
```
Use
----

Steps to setup for a version of Wonder:

*   Create the uber jar by:  1) download;  2) cd dir;  3) mvn clean install
*   Copy the uber jar to ~/
*   Run the pomcurl.sh script, which creates the split poms and puts them in a sub-directory: Example: (after setup)  ./pomcurl.sh  5.8.2 would create a sub-directory which holds the refactored main pom for version 5.8.2 of Wonder.
*   cd to where the reusabe parent pom.xml is and install this parent into your local repository: (cd wonderpom5.8.2/.generic-woparent; mvn install). This parent pom can be used to build any Wonder project for version 5.8.2 of Wonder. Choose whatever version of Wonder you want.

Steps to utilize the wonder parent pom:
*	Create your new wonder app: mvn archetype:generate -DarchetypeCatalog=local
* 	Change some things in the newly created project's pom.xm like the following things: 
*		a) Add parent pom reference ( maybe add the file to your new  project).
```
		<parent>
		       <groupId>wonder</groupId>
 		       <artifactId>generic-woparent</artifactId>
		       <version>5.8.2</version>
    	    		<relativePath>./generic-woparent</relativePath>
    		</parent>
```
*		b) delete wonder.version property, it's handled by the parent pom.
*		c) delete plugins, the parent provides them.
*		d) set the property wonder.classifier  to  null, nothing,  or delete that property.


This refactoring out the parent pom lets you switch easily to different versions of Wonder, with the correct versions of plugins, jars, etc., as it was the same versions of dependent jars which built that version of Wonder. This also got me familiar with the DOM and XLS and XSLT.



 
