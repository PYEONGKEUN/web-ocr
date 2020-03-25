package com.poseungcar.webocr.DAO;

import java.util.List;

import com.poseungcar.webocr.DTO.USER;

public interface UserDao {
	public List<USER> select(USER user);
	public int update(USER user);
	public int insert(USER user);
}
