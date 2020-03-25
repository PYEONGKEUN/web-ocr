package com.poseungcar.webocr.config;



import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration

//서비스를 스캔하는 패키지 위치
@ComponentScan(basePackages = { "com.poseungcar.webocr.service" })
@ComponentScan(basePackages = {"com.poseungcar.webocr.serviceImpl"})
//매퍼 스캔
@MapperScan(basePackages = { "com.poseungcar.webocr.DAO"})

@PropertySource({"classpath:profiles/${spring.profiles.active}/application.properties"})

@EnableScheduling
@EnableTransactionManagement
public class RootConfig {
	
	@Value("${db.url}")
	private String dbURL;
	@Value("${db.userName}")
	private String dbUserName;
	@Value("${db.password}")
	private String dbPassword;
	@Value("${db.driverClassName}")
	private String dbDriverClassName;
	
	@Bean
	public DataSource dataSource() {
		DataSource ds=new DataSource();
		ds.setDriverClassName(dbDriverClassName);
		ds.setUrl(dbURL); 
		ds.setUsername(dbUserName); 
		ds.setPassword(dbPassword); 
		ds.setInitialSize(2);
		ds.setMaxActive(10);
		ds.setMaxIdle(10);

		
		//ValidationQuery
		// 일정시간마다 DB와 연결 확인
		// mysql은 기본값으로 8시간동안
		//DB에 요청이 없으면 DB 연결을 끊어 버린다

		ds.setTestWhileIdle(true);
		ds.setValidationQuery("select 1");
		ds.setMinEvictableIdleTimeMillis(60000*3);
		ds.setTimeBetweenEvictionRunsMillis(10*1000);
		ds.setValidationQueryTimeout(28800);
		ds.setValidationInterval(28800);
		
		
		return ds;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource());
		return (SqlSessionFactory) sqlSessionFactory.getObject();
	}
	
	@Bean
	public SqlSession sqlSession() {
		SqlSessionTemplate template=null;
		try {
			template = new SqlSessionTemplate(sqlSessionFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}

	@Bean
	public DataSourceTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	



}	
