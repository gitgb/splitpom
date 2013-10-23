SplitPom - create parent and child poms.
========================================

Takes the wonder main pom as input and modifies it by splitting it into 2 poms: 
./generic-woparent/pom.xml and ./child.pom.xml. 

The parent pom has information useful to build any womaven project. You get all this info by referring
to the generated parent pom. The main thing here is to create the parent pom then reference it in your project.

Prerequsites
------------

Have a reference to the wocommunity repository in your pom or settings.xml.

Use
----
Steps:
* 	Get the version of wonder:wonder:pom.xml that you want, 
*	Create the uber jar by: download; cd dir; mvn clean package
*	Split pom: java -jar target/uber-splitpom-1.0.0-SNAPSHOT.jar ./path/to/wonder-main/pom.xml
*	Create wonder app: mvn archetype:generate -DarchetypeCatalog=local
* 	Change some things in the new project's pom.xm like the following things: 
*		Add parent pom reference ( maybe add the file to your new  project).
*		delete wonder.version property, it's handled by the parent pom.
*		delete plugins, the parent provides them.
	




 
