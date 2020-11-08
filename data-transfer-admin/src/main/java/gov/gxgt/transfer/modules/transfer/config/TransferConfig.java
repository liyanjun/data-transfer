package gov.gxgt.transfer.modules.transfer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 大数据对接参数
 *
 * @author liyanjun
 */
@Data
@Component
public class TransferConfig {
    @Value("${data.transfer.url}")
    private String url;
    @Value("${data.transfer.clientId}")
    private String clientId;
    @Value("${data.transfer.clientSecret}")
    private String clientSecret;
    @Value("${data.transfer.key}")
    private String key;
    @Value("${data.transfer.secret}")
    private String secret;

}
