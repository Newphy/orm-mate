<?xml version="1.0"?>
<?altova_samplexml file:///E:/workspace/IDEA/ormate/mybatis-mate/src/main/resources/template/entity.xml?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"
    doctype-public="-//mybatis.org//DTD Mapper 3.0//EN" doctype-system="http://mybatis.org/dtd/mybatis-3-mapper.dtd"/>
  <xsl:template match="/">
    <xsl:variable name="namespace" select="mybatisMapping/namespace"/>
    <xsl:variable name="type" select="mybatisMapping/entityClass"/>
    <xsl:variable name="simpleName" select="mybatisMapping/simpleName"/>
    <xsl:variable name="tableName" select="mybatisMapping/tableName"/>
    <xsl:variable name="idColumn" select="mybatisMapping/idMapping"/>
    <xsl:variable name="versionColumn" select="mybatisMapping/versionMapping"/>
    <mapper namespace="{$namespace}">
      <parameterMap type="{$type}" id="ParameterMap"/>

      <sql id="sqlColumns">
        <trim suffixOverrides=",">
          <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping">
            <xsl:value-of select="column"/>,
          </xsl:for-each>
        </trim>
      </sql>

      <sql id="sqlColumnsT">
        <trim suffixOverrides=",">
          <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping">
            t.<xsl:value-of select="column"/>,
          </xsl:for-each>
        </trim>
      </sql>

      <!-- save -->
      <insert id="save" parameterMap="ParameterMap">
        <xsl:if test="$idColumn">
          <xsl:attribute name="useGeneratedKeys">true</xsl:attribute>
          <xsl:attribute name="keyProperty">
            <xsl:value-of select="$idColumn/property"/>
          </xsl:attribute>
        </xsl:if>
        INSERT INTO <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <trim prefix="(" suffix=")" suffixOverrides=",">
          <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[insertable='true']">
              <xsl:value-of select="column"/>,
          </xsl:for-each>
        </trim>
        <trim prefix="VALUES(" suffix=")" suffixOverrides=",">
          <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[insertable='true']">
              #{<xsl:value-of select="property"/>},
          </xsl:for-each>
        </trim>
      </insert>


      <insert id="batchSave" parameterType="java.util.Collection">
        <xsl:if test="$idColumn">
          <xsl:attribute name="useGeneratedKeys">true</xsl:attribute>
          <xsl:attribute name="keyProperty">
            <xsl:value-of select="$idColumn/property"/>
          </xsl:attribute>
        </xsl:if>
        INSERT INTO
        <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <trim prefix="(" suffix=")" suffixOverrides=",">
          <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[insertable='true']">
            <xsl:value-of select="column"/>,
          </xsl:for-each>
        </trim>
        VALUES
        <foreach item="entity" index="index" collection="list" separator=",">
          <trim prefix="(" suffix=")" suffixOverrides=",">
            <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[insertable='true']">
              #{entity.<xsl:value-of select="property"/>},
            </xsl:for-each>
          </trim>
        </foreach>
      </insert>

      <update id="update" parameterMap="ParameterMap">
        UPDATE <xsl:value-of select="$tableName"/> <xsl:text> </xsl:text>
        <set>
          <trim suffixOverrides=",">
            <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[updatable='true']">
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

      <update id="updateOptimistic" parameterMap="ParameterMap">
        UPDATE <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <set>
          <trim suffixOverrides=",">
            <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[updatable='true']">
              <xsl:choose>
                <xsl:when test="$versionColumn/column = column">
                  <xsl:value-of select="column"/> =
                  <xsl:value-of select="column"/> + 1,
                </xsl:when>
                <xsl:when test="$idColumn/column != column">
                  <xsl:value-of select="column"/> = #{<xsl:value-of select="property"/>},
                </xsl:when>
              </xsl:choose>
            </xsl:for-each>
          </trim>
        </set>
        <where>
          <xsl:value-of select="$idColumn/column"/> = #{<xsl:value-of select="$idColumn/property"/>} AND
          <xsl:value-of select="$versionColumn/column"/> = #{<xsl:value-of select="$versionColumn/property"/>}
        </where>
      </update>

      <update id="batchUpdate" parameterType="java.util.Collection">
        <foreach collection="list" item="entity" index="index" open="begin" close=";end;" separator=";">
          UPDATE <xsl:value-of select="mybatisMapping/tableName"/><xsl:text> </xsl:text>
          <set>
            <trim suffixOverrides=",">
              <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping[updatable='true']">
                <xsl:if test="column != $idColumn/column">
                  <xsl:value-of select="column"/>=#{entity.<xsl:value-of select="property"/>},
                </xsl:if>
              </xsl:for-each>
            </trim>
          </set>
          WHERE
          <xsl:value-of select="$idColumn/column"/>=#{entity.<xsl:value-of select="$idColumn/property"/>}
        </foreach>
      </update>

      <delete id="delete" parameterMap="ParameterMap">
        DELETE FROM <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <where>
          <xsl:value-of select="$idColumn/column"/>=#{<xsl:value-of select="$idColumn/property"/>}
        </where>
      </delete>

      <delete id="batchDelete" parameterType="java.util.Collection">
        DELETE FROM <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <where>
          <xsl:value-of select="$idColumn/column"/> IN
          <foreach item="entity" index="index" collection="list" open="(" separator="," close=") ">
            <xsl:value-of select="$idColumn/column"/>=#{entity.<xsl:value-of select="$idColumn/property"/>}
          </foreach>
        </where>
      </delete>

      <delete id="deleteById">
        DELETE FROM <xsl:value-of select="$tableName"/><xsl:text> </xsl:text>
        <where>
          <xsl:value-of select="$idColumn/column"/>=#{0}
        </where>
      </delete>

      <select id="get" resultMap="ResultMap">
        SELECT
        <include refid="sqlColumns"/>
        FROM <xsl:value-of select="mybatisMapping/tableName"/>
        WHERE <xsl:value-of select="$idColumn/column"/>=#{0}
      </select>

      <select id="getBy" resultMap="ResultMap">
        SELECT
        <include refid="sqlColumnsT"/>
        FROM
        <xsl:value-of select="mybatisMapping/tableName"/> t WHERE 1=1
        <include refid="sqlCondition"/>
        <include refid="sqlOrderBy"/>
      </select>

      <select id="getOneBy" resultMap="ResultMap">
        SELECT
        <include refid="sqlColumnsT"/>
        FROM
        <xsl:value-of select="mybatisMapping/tableName"/> t WHERE 1=1
        <include refid="sqlCondition"/>
        LIMIT 1
      </select>

      <select id="listAll" resultMap="ResultMap">
        SELECT
        <include refid="sqlColumns"/>
        FROM
        <xsl:value-of select="mybatisMapping/tableName"/>
        <include refid="sqlOrderBy"/>
      </select>

      <sql id="sqlCondition">
        <xsl:for-each select="mybatisMapping/propertyMappings/propertyMapping">
          <if>
            <xsl:attribute name="test">
              <xsl:value-of select="property"/> != null
            </xsl:attribute>
            AND t.<xsl:value-of select="column"/>=#{<xsl:value-of select="property"/>}
          </if>
        </xsl:for-each>
      </sql>

      <sql id="sqlOrderBy">
        <foreach collection="__orders" item="order" index="index" open="ORDER BY " close="" separator=", ">
          ${order.column} ${order.direction}
        </foreach>
      </sql>


      <select id="query" resultMap="ResultMap">
        SELECT
        <include refid="sqlColumns"/>
        FROM
        <xsl:value-of select="mybatisMapping/tableName"/> t WHERE 1=1
        <include refid="sqlCondition"/>
      </select>

      <select id="count" resultType="int">
        SELECT count(1) FROM
        <xsl:value-of select="mybatisMapping/tableName"/> t WHERE 1=1
        <include refid="sqlCondition"/>
      </select>

    </mapper>
  </xsl:template>
</xsl:stylesheet>
