SplitPom - create parent and child poms.
========================================

Takes the wonder main pom as input and modifies or refactors it by splitting it into 2 poms: 
./generic-woparent/pom.xml and ./child.pom.xml. 

The parent pom has information useful to build any womaven project. You get all this info by referring
to the generated parent pom. The main thing here is to create the parent pom then reference it in your project.

It is very good to not have to track the versions for all these jars.

This also adds a plugin to help manage versions:

```			<plugin>
				<!-- use at: http://mojo.codehaus.org/versions-maven-plugin/ -->
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>versions-maven-plugin</artifactId>
				<version>1.3.1</version>
			</plugin>
```

Prerequsites
------------

Have a reference to the wocommunity repository in your pom or settings.xml.

Use
----
Steps:
* 	Get the version of the main wonder:wonder:pom.xml that you want, 
*	Create the uber jar by:  1) download;  2) cd dir;  3) mvn clean install
*	Split the pom by: java -jar target/uber-splitpom-1.0.0-SNAPSHOT.jar ./path/to/wonder-main/pom.xml
*	Create your new wonder app: mvn archetype:generate -DarchetypeCatalog=local
* 	Change some things in the newly created project's pom.xm like the following things: 
*		a) Add parent pom reference ( maybe add the file to your new  project).
*		b) delete wonder.version property, it's handled by the parent pom.
*		c) delete plugins, the parent provides them.

Also there is a pomcurl.sh, which is a:

 	script to pull a version of wonder pom from the repository, then
	create the generic-woparent/pom.xml and  the child.pom.xml.
	First, make the uber jar (mvn clean package) and copy the uber jar to ~/, then use it.
	Example: (after setup) ./pomcurl.sh 5.8.2 
	The above will get the wonder version 5.8.2 pom, and split it.

That lets you switch easily to different versions of Wonder, with the correct versions of plugins, jars, etc., as it was the same versions which built that version of Wonder.



 
