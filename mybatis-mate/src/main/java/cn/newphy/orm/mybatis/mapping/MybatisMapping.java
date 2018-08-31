package cn.newphy.orm.mybatis.mapping;

import cn.newphy.mate.util.XmlUtils;
import cn.newphy.mate.util.XsltUtils;
import cn.newphy.orm.mybatis.MybatisConfiguration;
import cn.newphy.orm.mybatis.exception.MybatisMateException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Templates;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ResultMap;

/**
 * 实体映射类
 * 
 * @author Newphy
 * @date 2018/8/1
 **/
public abstract class MybatisMapping<T> {
	private Log log = LogFactory.getLog(MybatisMapping.class);

	/**
	 * 配置信息
	 */
	protected final MybatisConfiguration configuration;
	/**
	 * 实体类
	 */
	protected final Class<T> entityClass;
	/**
	 * 表名
	 */
	protected String tableName;
	/**
	 * 字段名map
	 */
	protected Map<String, MybatisPropertyMapping> columnMap = new HashMap<>();
	/**
	 * 属性名map
	 */
	protected Map<String, MybatisPropertyMapping> propertyMap = new HashMap<>();
	/**
	 * 属性映射列表
	 */
	protected List<MybatisPropertyMapping> propertyMappings = new ArrayList<>();
	/**
	 * 主键字段映射
	 */
	protected IdPropertyMapping idMapping;
	/**
	 * 版本字段映射
	 */
	protected MybatisPropertyMapping versionMapping;
	/**
	 * EntityMappingResolver
	 */
	protected EntityClassResolver entityClassResolver;
	/**
	 * 对应ResultMap
	 */
	private ResultMap resultMap;


	MybatisMapping() {
		this(null, null);
	}

	MybatisMapping(MybatisConfiguration configuration, Class<T> entityClass) {
		this.configuration = configuration;
		this.entityClass = entityClass;
		this.entityClassResolver = EntityClassResolver.getInstance(configuration);
		initialize();
	}

	/**
	 * 获得名字空间
	 * @return
	 */
	@XmlElement
	public String getNamespace() {
		return "EntityDao." + getNamespacePostfix();
	}

	/**
	 * 获取对应ResultMap
	 * @return
	 */
	@XmlTransient
	public ResultMap getResultMap() {
		return resultMap;
	}

	/**
	 * 获取名字空间后缀
	 * @return
	 */
	protected abstract String getNamespacePostfix();

	/**
	 * 初始化属性映射
	 */
	protected abstract void initPropertyMappings();

	/**
	 * 构建ResultMap
	 * @return
	 */
	protected abstract ResultMap buildResultMap(String resultMapId);



	protected void addPropertyMapping(MybatisPropertyMapping propertyMapping) {
		propertyMappings.add(propertyMapping);
		columnMap.put(propertyMapping.getColumn(), propertyMapping);
		propertyMap.put(propertyMapping.getProperty(), propertyMapping);
	}


	private void initialize() {
		initTableName();
		// 初始化propertyMappings
		initPropertyMappings();
		// 注册新的ResultMap
		registerResultMap();
		// build mapper文件
		buildXmlMapper();
	}

	private void initTableName() {
		TableMeta tableMeta = entityClassResolver.getTableMeta(entityClass);
		if (tableMeta == null) {
			throw new IllegalStateException("实体[" + entityClass.getCanonicalName() + "]表名未指定");
		}
		this.tableName = tableMeta.getTableName();
	}


	/**
	 * 注册ResultMap
	 */
	private void registerResultMap() {
		String id = getNamespace() + ".ResultMap";
		this.resultMap = buildResultMap(id);
		configuration.getConfiguration().addResultMap(resultMap);
	}


	/**
	 * build ResultMap相关的Mapper操作
	 */
	private void buildXmlMapper() {
		log.debug(String.format("初始化[%s]的Mapping文件", getNamespace()));
		try {
			Templates templates = configuration.getDialect().getMapperTemplates();
			String entityXml = XmlUtils.bean2Xml(this, "UTF-8");
			String mapperXml = XsltUtils.transform(templates, entityXml);
			if (log.isDebugEnabled()) {
				log.debug(String.format("---------------------- %s Mapping ----------------------", getNamespace()));
				log.debug(mapperXml);
			}
			String resource = "/entitydao/mapping/" + getNamespace() + "Mapping.xml";
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(new ByteArrayInputStream(mapperXml.getBytes()),
				configuration.getConfiguration(), resource, configuration.getConfiguration().getSqlFragments());
			xmlMapperBuilder.parse();
		} catch (Exception e) {
			throw new MybatisMateException(String.format("构建EntityDao[%s]的Mapper文件时出错", getNamespace()), e);
		}
	}


	/**
	 * @return the versionMapping
	 */
	@XmlElement
	public MybatisPropertyMapping getVersionMapping() {
		return versionMapping;
	}

	/**
	 * 根据字段名获得PropertyMapping
	 * @param column
	 * @return
	 */
	public MybatisPropertyMapping getResultMappingByColumn(String column) {
		return columnMap.get(column);
	}

	/**
	 * 根据属性名获得PropertyMapping
	 * @param property
	 * @return
	 */
	public MybatisPropertyMapping getResultMappingByProperty(String property) {
		return propertyMap.get(property);
	}

	/**
	 * 获得Id映射字段
	 *
	 * @return
	 */
	@XmlElement
	public IdPropertyMapping getIdMapping() {
		return idMapping;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	@XmlElementWrapper(name="propertyMappings")
	@XmlElement(name = "propertyMapping")
    public List<MybatisPropertyMapping> getPropertyMappings() {
		return propertyMappings;
	}

	/**
	 * 根据propertyName获取属性映射
	 * @param propertyName
	 * @return
	 */
	public MybatisPropertyMapping getPropertyMapping(String propertyName) {
		return this.propertyMap.get(propertyName);
	}

	@XmlElement
	public Class<T> getEntityClass() {
		return entityClass;
	}

	@XmlElement
	public String getSimpleName() {
		return entityClass.getSimpleName();
	}
}
