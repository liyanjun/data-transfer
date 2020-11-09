package gov.gxgt.transfer.modules.yth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 一体化不动产推送信息
 *
 * @author liyanjun
 * @email
 * @date 2020-06-24 15:20:05
 */
@Data
@TableName("APIINFO.YTH_BDC")
public class YthBdcEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    /**
     * 推送类型，accept：受理信息，finish：办结信息
     */
    private String type;
    /**
     * 申报推送信息
     */
    private String dataSb;
    /**
     * 受理推送信息
     */
    private String dataSl;
    /**
     * 审批推送信息
     */
    private String dataSp;
    /**
     * 办结推送信息
     */
    private String dataBj;
    /**
     * 入库时间
     */
    private Date createTime;
    /**
     * 状态，0：未申报，1：未受理，2：未审批，3：未办结，-1：申报推送业务异常，-2：受理推送业务异常，-3：审批推送业务异常，-4：办结推送业务异常
     */
    private Integer state;
    /**
     * 区县代码
     */
    private String areaCode;
    /**
     * 区县代码
     */
    private String spsx;
    /**
     * 异常信息
     */
    private String exception;

//    private String name;

}
