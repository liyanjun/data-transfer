package gov.gxgt.transfer.modules.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ${comments}
 * 
 * @author liyanjun
 * @email 
 * @date 2020-11-08 22:05:29
 */
@Data
@TableName("IN_CATALOG")
public class InCatalogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId(type = IdType.ID_WORKER)
@JsonSerialize(using= ToStringSerializer.class)
	private String id;
	/**
	 * $column.comments
	 */
	private String code;
	/**
	 * $column.comments
	 */
	private String name;
	/**
	 * $column.comments
	 */
	private String simplename;
	/**
	 * $column.comments
	 */
	private Integer orderby;
	/**
	 * $column.comments
	 */
	private Integer daysoflaw;
	/**
	 * $column.comments
	 */
	private Integer daysofpromise;
	/**
	 * $column.comments
	 */
	private String createprojectapi;
	/**
	 * $column.comments
	 */
	private String supplementapi;
	/**
	 * $column.comments
	 */
	private Integer isinnernet;
	/**
	 * $column.comments
	 */
	private String identity;
	/**
	 * $column.comments
	 */
	private Integer isdeleted;
	/**
	 * $column.comments
	 */
	private Date createdtime;
	/**
	 * $column.comments
	 */
	private Date updatetime;
	/**
	 * $column.comments
	 */
	private Date deletedtime;
	/**
	 * $column.comments
	 */
	private String deptcode;
	/**
	 * $column.comments
	 */
	private String deptname;
	/**
	 * $column.comments
	 */
	private String cantoncode;
	/**
	 * $column.comments
	 */
	private String materials;
	/**
	 * $column.comments
	 */
	private Integer issetting;
	/**
	 * $column.comments
	 */
	private String rowguid;
	/**
	 * $column.comments
	 */
	private String childcode;

}
