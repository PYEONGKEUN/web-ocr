package com.poseungcar.webocr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.inject.Inject;

import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.poseungcar.webocr.config.RootConfig;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {RootConfig.class})
public class DBConnectionTest {

	@Inject
	private DataSource ds;


	@Test
	public void testConnection() throws Exception {

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
