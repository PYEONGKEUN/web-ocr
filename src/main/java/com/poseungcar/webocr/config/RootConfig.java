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
		//Datasource 성능 튜닝 및 validation 연결 유지
		
		
		ds.setInitialSize(100);                         //initialSize : 풀의 초기 생성되는 사이즈
		ds.setMaxActive(150);                           //maxActive : 최대 접속가능 커넥션 수 (사실상 가장 중요하다고 생각한다. DB에 동시에 접속 가능 커넥션이 많아야 성능이 좋아진다고 볼 수 있다.)
		ds.setMaxWait(10000);                           //connection 사용이 많아져서 connection pool이 비었을 때 대기시간 (단위 1/1000초) 기본값은 -1(무한대)이며,  서비스 특성에 맞게 설정하면된다. 일반적으로 기본값을 사용해도 큰 문제는 안될 것 같다.
		ds.setMaxIdle(30);                               //maxIdle : Idle상태의 최대 갯수                                                                                   
		ds.setMinIdle(20);                               //minIdle : idle상태의 최소 갯수                                                                                   
		ds.setTestOnBorrow(true);                        //testOnBorrow : 풀에서 커넥션을 가져올시 커넥션의 유효성 검사                                                                  
		ds.setTestOnReturn(true);                        //testOnReturn : 풀에 커넥션을 리턴할 때 커넥션의 유효성 검사                                                                  
		ds.setValidationQuery("select 1");               //validationQuery : validate Query                                                                          
		ds.setTestWhileIdle(true);                       //testWhileIdle : Idle상태에 커넥션의 유효성 검사                                                                       
		ds.setTimeBetweenEvictionRunsMillis(130000);     //timeBetweenEvictionRunsMillis : 설정된 주기를 통해 Evict(유효하지 않는 커넥션/정의된 시간이 만료된 커넥션을 풀에서 제거) 쓰레드를 수행             
		ds.setMinEvictableIdleTimeMillis(120000);        //minEvictableIdleTimeMiilis : Evict 쓰레드를 수행시, 만료여부를 체크할 시간을 정의                                             
		ds.setNumTestsPerEvictionRun(20);                //numTestsPerEvictionRun : Evict 쓰레드를 수행시 수행할 커넥션의 갯수                                                       
		ds.setRemoveAbandonedTimeout(30);                //removeAbandonedTimeout : 유효하지 안흔 커넥션의 삭제시의 타임아웃                                                           
		ds.setRemoveAbandoned(true);                     //removeAbandoned : 유효하지 않는 커넥션의 제거 여부                                                                      
		ds.setLogAbandoned(false);                       //logAbandoned : 유효하지 않는 커넥션을 생성한 코드 위치 로그생성 여부                                                             

		
		//ValidationQuery
		// 일정시간마다 DB와 연결 확인
		// mysql은 기본값으로 8시간동안
		//DB에 요청이 없으면 DB 연결을 끊어 버린다
//		ds.setMaxIdle(10);
//		ds.setTestWhileIdle(true);
//		ds.setValidationQuery("select 1");

//		ds.setValidationQueryTimeout(10000);
//		ds.setMinEvictableIdleTimeMillis(60000*3);
		
		
		
		
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
