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
 * @date 2020-06-29 14:53:57
 */
@Data
@TableName("TRANSFER_AUDIT_ITEM")
public class TransferAuditItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId(type = IdType.ID_WORKER)
	@JsonSerialize(using= ToStringSerializer.class)
	private String taskCode;
	/**
	 * $column.comments
	 */
	private String ywcode;
	/**
	 * 地市区划编码
	 */
	private String dsqhbm;
	/**
	 * 设区市
	 */
	private String sqs;
	/**
	 * 行业条线编码
	 */
	private String hytxbm;
	/**
	 * 行业条线
	 */
	private String hytx;
	/**
	 * 事项名称
	 */
	private String sxmc;
	/**
	 * 事项类型
	 */
	private String sxlx;
	/**
	 * 层级
	 */
	private String cjj;
	/**
	 * 区划名称
	 */
	private String qhmc;
	/**
	 * 实施主体
	 */
	private String sszt;
	/**
	 * $column.comments
	 */
	private String areaCode;

}
