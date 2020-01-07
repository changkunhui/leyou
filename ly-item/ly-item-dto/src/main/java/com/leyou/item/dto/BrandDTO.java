package com.leyou.item.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 品牌的数据传输类
 */

@Data
  public class BrandDTO implements Serializable {
      private Long id;
      private String name;
      private String image;
      private String letter;
  }