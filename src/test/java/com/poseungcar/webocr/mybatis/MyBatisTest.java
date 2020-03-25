package com.poseungcar.webocr.mybatis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.inject.Inject;

import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.poseungcar.webocr.config.RootConfig;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RootConfig.class})
public class MyBatisTest {

	private static final Logger logger = LoggerFactory.getLogger(MyBatisTest.class);
	
	@Inject
	private DataSource ds;


	// sql 동작은 모두 로그에 기록되기때문에 하지 않아도 됨   
	// DB 서버 연결부터 Ibatis 매핑 테스트
	@Test
	public void test() throws Exception {

		try (Connection con = ds.getConnection()) {
			System.out.println(con);
			Statement stmt = con.createStatement();
			
            String sql;
            sql = "SELECT 1";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
            	
            	System.out.println(rs.toString()); 
            }
            rs.close();
            stmt.close();
            con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
