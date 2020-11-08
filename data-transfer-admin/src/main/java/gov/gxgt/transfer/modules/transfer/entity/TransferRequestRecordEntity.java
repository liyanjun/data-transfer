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
 * 数据同步通讯记录
 * 
 * @author liyanjun
 * @email 
 * @date 2020-06-24 10:31:26
 */
@Data
@TableName("TRANSFER_REQUEST_RECORD")
public class TransferRequestRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.ID_WORKER)
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	/**
	 * 请求参数
	 */
	private String request;
	/**
	 * 响应参数
	 */
	private String response;
	/**
	 * 异常信息
	 */
	private String exception;
	/**
	 * 请求来源ID
	 */
	private String sourceId;
	/**
	 * 请求时间
	 */
	private Date createTime;
	/**
	 * 响应时间
	 */
	private Date responseTime;
	/**
	 * 来源类型1：受理信息，2：办结信息
	 */
	private Integer type;

}
