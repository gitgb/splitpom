<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0"
	xmlns:p="http://maven.apache.org/POM/4.0.0"
	xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xslt="http://xml.apache.org/xslt"
	exclude-result-prefixes=" p"

	>
	<xsl:output
		method="xml"	xslt:indent-amount="4"	
		indent="yes" />
	<xsl:strip-space  elements="*"/>	
	
	<xsl:template match="@*|node()">
		<!-- This is the Identity design pattern. It can match everything and pass it thru. The more specific 
			templates have higher priority, so for their matching elements they take precedence. This allows just matching 
			things that need to modified. Less work. -->
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="text()"> <!-- Formating -->
		<xsl:value-of select="normalize-space()"/>
		<xsl:apply-templates select="@*|node()" />
	</xsl:template>
		
	<!-- Modify original templates below ********************** -->
		<xsl:template match="/p:project">
		<xsl:copy>
			<xsl:comment>
				This parent pom was auto generated by splitting the main pom into parent/child poms.
			</xsl:comment>
			<xsl:apply-templates  />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="/p:project/p:artifactId/text()">generic-woparent</xsl:template>
	
	<xsl:template match="/p:project/p:name">
		<xsl:copy>Wonder generic woparent</xsl:copy>
		<xsl:element name="description">
			Includes the generic woparent pom for all Wonder/WebObjects projects. Has repositories needed, dependency
			management, etc. Override in your project's pom when necessary items like distributionManagement.
			Sections from the wonder master pom are passed through to create the parent pom. Also, a
			wonder.version parameter is created, its value based off the wonder master pom.
		</xsl:element>
		
	</xsl:template>
		
	<xsl:template match="/p:project/p:properties"><xsl:message>Matched properties</xsl:message>
		<xsl:copy>
		
			<xsl:element name="wonder.version">
				<xsl:value-of select="/p:project/p:version" />
			</xsl:element>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="/p:project/p:properties[not(p:skip.apple.frameworks)]">
		<xsl:message>No p:skip.apple.frameworks</xsl:message>
		<xsl:copy>
		 <xsl:element name="skip.apple.frameworks"  >false</xsl:element>
		 <xsl:element name="wonder.version"><xsl:value-of select="/p:project/p:version" /></xsl:element> 
		<xsl:apply-templates select="@*|node()" />
		 </xsl:copy>
	</xsl:template>	
	
	<xsl:template match="/p:project/p:properties/p:skip.apple.frameworks">
		<xsl:copy>false</xsl:copy>
	</xsl:template>
	
	<xsl:template match="/p:project/p:properties/p:maven.test.skip"></xsl:template>

	<xsl:template match="/p:project/p:properties/comment()"></xsl:template>

	<xsl:template match="/p:project/p:build/p:plugins/p:plugin[ p:artifactId/text()='maven-wolifecycle-plugin']/p:configuration/p:skipAppleProvidedFrameworks/text()">
		<xsl:message>FOUND  plugin; <xsl:value-of select="."></xsl:value-of></xsl:message>
		<xsl:apply-templates  select="node()" />
		${skip.apple.frameworks}
	</xsl:template>
	
	<!-- to change project.version to wonder.version -->
	<xsl:template match="p:dependencies/p:dependency/p:version[text()='${project.version}']">
		 
		<xsl:element name="{local-name()}">${wonder.version}</xsl:element>
	</xsl:template>
	
	<xsl:template match="p:modules"> <!-- snip -->
	</xsl:template>

</xsl:stylesheet>