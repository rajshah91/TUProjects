package com.oppscience.sgevt.webapp.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.buf.StringUtils;

import com.oppscience.sgevt.webapp.model.UserList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class UserListDto implements Serializable{
	
	private static final long serialVersionUID = -6653964816834287925L;
	
	public UserListDto() {}
	
	public UserListDto(Long id, String name, String[] ids) {
		super();
		this.id = id;
		this.name = name;
		this.ids = ids;
	}
	
	@ApiModelProperty(value = "User List Id")
	private Long id;
	@ApiModelProperty(value = "User List Name")
	private String name;
	private String [] ids;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
//	@ApiModelProperty(notes="ids can have multiple values")
	public String[] getIds() {
		return ids;
	}
	public void setIds(String[] ids) {
		this.ids = ids;
	}
	
	
	
	
}
