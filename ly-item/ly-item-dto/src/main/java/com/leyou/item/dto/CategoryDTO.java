package com.leyou.item.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryDTO implements Serializable {
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent;
	private Integer sort;
}