<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns:xslt="http://xml.apache.org/xslt" 
	>
	<xsl:output
		method="xml"	xslt:indent-amount="4"	
		indent="yes" />
	<xsl:strip-space  elements="*"/>
		
	<xsl:template match="node()|@*">
		<!-- This is the Identity design pattern. It can match everything and pass it thru. The more specific 
			templates have higher priority, so for their matching elements they take precedence. This allows just matching 
			things that need to modified. Less work. -->
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
		<xsl:template match="text()">
			<xsl:value-of select="normalize-space()"/>
			<xsl:apply-templates select="@*|node()" />
		</xsl:template>

</xsl:stylesheet>