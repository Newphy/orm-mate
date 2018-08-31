<?xml version="1.0"?>
<?altova_samplexml file:///E:/workspace/IDEA/ormate/mybatis-mate/src/main/resources/template/entity.xml?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="-//mybatis.org//DTD Mapper 3.0//EN" doctype-system="http://mybatis.org/dtd/mybatis-3-mapper.dtd"/>
	<xsl:template match="/">
		<xsl:variable name="namespace" select="entity/namespace"/>
		<xsl:variable name="type" select="entity/type"/>
		<xsl:variable name="simpleName" select="entity/simpleName"/>
		<xsl:variable name="tableName" select="entity/tableName"/>
		<xsl:variable name="idColumn" select="entity/propertyMappings/PropertyMapping[pk='true']"/>
		<xsl:variable name="versionColumn" select="entity/propertyMappings/PropertyMapping[versionable='true']" />
		<mapper namespace="{$namespace}">
			<parameterMap type="{$type}" id="{$simpleName}ParameterMap"/>
			<sql id="sqlColumns">
				<trim suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping">
						<xsl:value-of select="column"/>,
					</xsl:for-each>
				</trim>
			</sql>
			<sql id="sqlColumnsT">
				<trim suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping">
						t.<xsl:value-of select="column"/>,
					</xsl:for-each>
				</trim>
			</sql>
			<insert id="save" parameterMap="{$simpleName}ParameterMap">
				<xsl:if test="$idColumn">
					<xsl:attribute name="useGeneratedKeys">true</xsl:attribute>
					<xsl:attribute name="keyProperty"><xsl:value-of select="$idColumn/property"/></xsl:attribute>
				</xsl:if>
		INSERT INTO <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
		<trim prefix="(" suffix=")" suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping[insertable='true']">
						<if>
							<xsl:attribute name="test">null != <xsl:value-of select="property"/></xsl:attribute>
							<xsl:value-of select="column"/>,
				</if>
					</xsl:for-each>
				</trim>
				<trim prefix="VALUES(" suffix=")" suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping[insertable='true']">
						<if>
							<xsl:attribute name="test">null != <xsl:value-of select="property"/></xsl:attribute>
					#{<xsl:value-of select="property"/>},
				</if>
					</xsl:for-each>
				</trim>
			</insert>
			<insert id="batchSave" parameterType="java.util.Collection">
			INSERT INTO <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
			<trim prefix="(" suffix=")" suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping[insertable='true']">
						<if>
							<xsl:attribute name="test">null != <xsl:value-of select="property"/></xsl:attribute>
							<xsl:value-of select="column"/>,
					</if>
					</xsl:for-each>
				</trim>
		VALUES
		<foreach item="entity" index="index" collection="list" separator=",">
					<trim prefix="(" suffix=")" suffixOverrides=",">
						<xsl:for-each select="entity/propertyMappings/PropertyMapping[insertable='true']">
							<if>
								<xsl:attribute name="test">null != <xsl:value-of select="property"/></xsl:attribute>
						#{entity.<xsl:value-of select="property"/>},
					</if>
						</xsl:for-each>
					</trim>
				</foreach>
			</insert>
			<update id="update" parameterMap="{$simpleName}ParameterMap">
				UPDATE <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
				<set>
					<trim suffixOverrides=",">
						<xsl:for-each select="entity/propertyMappings/PropertyMapping[updatable='true']">
							<xsl:if test="column != $idColumn/column">
								<xsl:value-of select="column"/> = #{<xsl:value-of select="property"/>},
							</xsl:if>
						</xsl:for-each>
					</trim>
				</set>
				<where>
					<xsl:value-of select="$idColumn/column"/> = #{<xsl:value-of select="$idColumn/property"/>}
				</where>
			</update>
			
			<update id="updateOptimistic" parameterMap="{$simpleName}ParameterMap">
				UPDATE <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
				<set>
					<trim suffixOverrides=",">
					<xsl:for-each select="entity/propertyMappings/PropertyMapping[updatable='true']">
						<xsl:choose>
							<xsl:when test="$versionColumn/column = column">
								<xsl:value-of select="column"/> = <xsl:value-of select="column" /> + 1,
							</xsl:when>
							<xsl:when test="$idColumn/column != column">
								<xsl:value-of select="column"/> = #{<xsl:value-of select="property"/>},
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>			
					</trim>
				</set>
				<where>
					<xsl:value-of select="$idColumn/column" /> = #{<xsl:value-of select="$idColumn/property" />} AND <xsl:value-of select="$versionColumn/column" /> = #{<xsl:value-of select="$versionColumn/property"/>}
				</where>
			</update>
			
			<update id="batchUpdate" parameterType="java.util.Collection">
				<foreach collection="list" item="item" index="index" open="" close="" separator=";">
					UPDATE <xsl:value-of select="entity/tableName" /> 
						<set>
							<trim suffixOverrides=",">
								<xsl:for-each select="entity/propertyMappings/PropertyMapping[updatable='true']">
									<xsl:if test="column != $idColumn/column">
										<xsl:value-of select="column"/> = #{item.<xsl:value-of select="property"/>},
									</xsl:if>
								</xsl:for-each>					
							</trim>
						</set>
						WHERE ${idMapping.column} = #${r'{'}item.${idMapping.property}}
				</foreach>
			</update>
			
		<delete id="delete" parameterMap="{$simpleName}ParameterMap">
			DELETE FROM <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
			<where>
				<xsl:value-of select="$idColumn/column" /> = #{<xsl:value-of select="$idColumn/property" />}
			</where>
		</delete>	
		
		<delete id="batchDelete" parameterType="java.util.Collection">
			DELETE FROM <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text> 
			<where>
				<xsl:value-of select="$idColumn/column"/> IN
				<foreach item="item" index="index" collection="list"  open="(" separator="," close=") ">
					${idMapping.column}  = #{item.<xsl:value-of select="$idColumn/property" />}
				</foreach>
			</where>
		</delete>
		
		<delete id="deleteById" >
			DELETE FROM <xsl:value-of select="entity/tableName"/><xsl:text> </xsl:text>
			<where>
				<xsl:value-of select="$idColumn/column"/> = #{0}
			</where>
		</delete>	

	<select id="get"  resultMap="{$simpleName}ResultMap">
		SELECT  <include refid="sqlColumns" />
		FROM <xsl:value-of select="entity/tableName" /> WHERE ${idMapping.column} = #${r'{'}0}
 	</select>
 	
	<select id="getBy"  resultMap="{$simpleName}ResultMap">
		SELECT  <include refid="sqlColumnsT" />
		FROM <xsl:value-of select="entity/tableName" /> t WHERE 1=1
		<include refid="sqlCondition" />
		<include refid="sqlOrderBy" />
 	</select> 
 	
 	<select id="getOneBy"  resultMap="{$simpleName}ResultMap">
		SELECT  <include refid="sqlColumnsT" />
		FROM <xsl:value-of select="entity/tableName" /> t WHERE 1=1
		<include refid="sqlCondition" /> LIMIT 1
 	</select> 		
 	
	<select id="getAll" resultMap="{$simpleName}ResultMap"  >
			SELECT <include refid="sqlColumns" /> FROM <xsl:value-of select="entity/tableName" /> t <include refid="sqlOrderBy" />
	</select> 	
    
	<sql id="sqlCondition">
		<xsl:for-each select="entity/propertyMappings/PropertyMapping">
			<if>
				<xsl:attribute name="test">
					<xsl:value-of select="property"/> != null <xsl:if test="javaType = 'java.lang.String'"> and ''!= <xsl:value-of select="property"/></xsl:if>
				</xsl:attribute>
				AND	t.<xsl:value-of select="column"/> = #{<xsl:value-of select="property" />}
			</if>
		</xsl:for-each>
	</sql>  

	<sql id="sqlOrderBy">
		<foreach collection="__orders" item="order" index="index" open="ORDER BY " close="" separator=", ">
			${order.column} ${order.direction}
		</foreach>
	</sql>  	   


	<select id="query" resultMap="{$simpleName}ResultMap"  >
			SELECT <include refid="sqlColumns" /> FROM <xsl:value-of select="entity/tableName" /> t WHERE 1=1
			<include refid="sqlCondition" />
	</select>

	<select id="count" resultType="int" >
			SELECT count(1) FROM <xsl:value-of select="entity/tableName" /> t WHERE 1=1
			<include refid="sqlCondition" />
	</select>

		</mapper>
	</xsl:template>
</xsl:stylesheet>
