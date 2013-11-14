#! /bin/sh          
# 
# FUNCTIONAL AREA OR ABSTRACT: (Shouldn't I use Perl ;>)
# 	script to pull a version of wonder pom from the repository, then
#	create the generic-woparent/pom.xml and  the child.pom.xml.
#	First, make (mvn clean package) and copy the uber jar to ~/
#	Example: (after setup) ./pomcurl.sh 5.8.2 
#	The above will get the wonder version 5.8.2 pom, and split it.
# 
# ENVIRONMENT/PROJECT:
# 	wonder utilities
# 
# MODIFIED BY:
# VERS	DATE		AUTH.	REASON
# 1.0	11/9/13		GSB	Original
# 
# 
# !-*/

if test $# -le 0;  then echo "`basename $0`: $@" 1>&2; cat <<eoerrmsg >&2
 this does take 1 parms  
 par 1 - VERSION OF POM, eg, 6.0.0-SNAPSHOT
 
eoerrmsg
exit 1
fi

# error message function
errmsg () {
	echo "`basename $0`: $@" 1>&2
exit 1
}

# Config setup
#http://maven.wocommunity.org/content/groups/public/wonder/wonder/5.8.2/wonder-5.8.2.pom
HOST="http://maven.wocommunity.org"  # make match your source host
#URLTEMPLATE="/nexus/service/local/repositories/wo-repository.releases/content/wonder/wonder/VERSION/wonder-VERSION.pom"
URLTEMPLATE="/content/groups/public/wonder/wonder/VERSION/wonder-VERSION.pom"
echo "uber jar should be already at ~/ "
echo "Edit host and urltemplate (already set for nexus repos) "
echo
# Now edit template to match request

template="$URLTEMPLATE"

echo "$template"

#usually 2 versions to replace:
template=${template/VERSION/$1}
template=${template/VERSION/$1}
echo "Will use $template"

URL="$HOST$template"
echo
echo url
echo $URL
DIR="wonderpom$1"
echo 
echo Make dir  $DIR
mkdir -vp $DIR
rm -fv  "$DIR/pom.xml"
curl $URL >> "$DIR/pom.xml"

( cd $DIR; java -jar ~/uber-splitpom-1.0.0-SNAPSHOT.jar pom.xml )


echo "My work is done"
exit 
