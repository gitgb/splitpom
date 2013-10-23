<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:p="http://maven.apache.org/POM/4.0.0" xmlns="http://maven.apache.org/POM/4.0.0">

	<xsl:output method="xml" indent="yes" />
	
	<xsl:template match="/p:project">
		<xsl:copy><!-- copy element then copy its attributes -->
			<xsl:copy-of select="@*" />
			<xsl:comment>
				This child pom was auto generated by splitting pom into parent/child poms
			</xsl:comment>
			<xsl:element name="parent">
			<xsl:element name="groupId">wonder</xsl:element>
			<xsl:element name="artifactId">generic-woparent</xsl:element>
			<xsl:element name="version"><xsl:value-of select="/p:project/p:version"></xsl:value-of></xsl:element>
			<xsl:element name="relativePath">./generic-woparent</xsl:element>
			
			</xsl:element>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	<xsl:template match="/p:project/p:modelVersion">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:groupId">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:artifactId">
	<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:version">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:packaging">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:name">
		<xsl:copy-of select="." />

	</xsl:template>
	<xsl:template match="/p:project/p:url">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:inceptionYear">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:organization">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:licenses">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:developers">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:contributors">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:mailingLists">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:prerequisites">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:modules">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:scm">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:distributionManagement">
		<xsl:copy-of select="." />
	</xsl:template>
	<xsl:template match="/p:project/p:properties">
	<xsl:copy-of select="." />
	</xsl:template>

	<xsl:template match="text()" />
</xsl:stylesheet>