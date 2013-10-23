
SplitPom - create parent and child poms.
========================================

Takes the wonder main pom as input and modifies it by splitting it into 2 poms: 
./generic-woparent/pom.xml and ./child.pom.xml. 

The parent pom has information useful to build any womaven project. You get all this info by referring
to the generated parent pom.

Use
======
Steps:
* 	Get the version of wonder:wonder:pom.xml that you want, 
*	Create the uber jar: cd dir; mvn clean package
*	Split poms: java -jar target/uber-splitpom-0.0.1-SNAPSHOT.jar ./path/to/wonder-main/pom.xml
*	Create wonder app: mvn archetype:generate -DarchetypeCatalog=local
* 	Change some things in the new project's pom.xm.: 
*		Add parent pom reference ( maybe add the file to your new  project).
*		delete <wonder.version> property, it's handled by the parent pom.
*		delete plugins, the parent provides them.
	

project.version of parent coupled to child project.

wolifecycle:define-woapplication-resources
  Description: resources goal for WebObjects projects.
  Implementation:
  org.objectstyle.woproject.maven2.wolifecycle.DefineWOApplicationResourcesMojo
  Language: java

  Available parameters:

    classifier
      Classifier to add to the artifact generated. If given, the artifact will
      be an attachment instead.

    finalName
      Required: true
      User property: project.build.finalName
      The name of the generated package (framework or application).

    flattenResources
      Expression: flattenResources
      Flatten all Resources and WebServerResouces into the Resources folder of
      WO application/framework package.

    includeJavaClientClassesInWebServerResources
      Expression: includeJavaClientClassesInWebServerResources
      include JavaClientClasses in WebServerResources.

    readPatternsets
      Expression: readPatternsets
      Read patternsets.

    skipAppleProvidedFrameworks
      Expression: skipAppleProvidedFrameworks
      skip webobjects frameworks from apple.
