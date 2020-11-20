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
@TableName("IN_CATALOG_NEW")
public class InCatalogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId(type = IdType.ID_WORKER)
	@JsonSerialize(using= ToStringSerializer.class)
	private String Id;
	/**
	 * $column.comments
	 */
	private String Code;
	/**
	 * $column.comments
	 */
	private String Name;
	/**
	 * $column.comments
	 */
	private String SimpleName;
	/**
	 * $column.comments
	 */
	private Integer OrderBy;
	/**
	 * $column.comments
	 */
	private Integer DaysOfLaw;
	/**
	 * $column.comments
	 */
	private Integer DaysOfPromise;
	/**
	 * $column.comments
	 */
	private String CreateProjectApi;
	/**
	 * $column.comments
	 */
	private String SupplementApi;
	/**
	 * $column.comments
	 */
	private Integer IsInnerNet;
	/**
	 * $column.comments
	 */
	private String Identity;
	/**
	 * $column.comments
	 */
	private Integer IsDeleted;
	/**
	 * $column.comments
	 */
	private Date CreatedTime;
	/**
	 * $column.comments
	 */
	private Date UpdateTime;
	/**
	 * $column.comments
	 */
	private Date DeletedTime;
	/**
	 * $column.comments
	 */
	private String DeptCode;
	/**
	 * $column.comments
	 */
	private String DeptName;
	/**
	 * $column.comments
	 */
	private String CantonCode;
	/**
	 * $column.comments
	 */
	private String Materials;
	/**
	 * $column.comments
	 */
	private Integer IsSetting;
	/**
	 * $column.comments
	 */
	private String RowGuid;
	/**
	 * $column.comments
	 */
	private String ChildCode;

}
