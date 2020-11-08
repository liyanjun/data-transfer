package gov.gxgt.transfer.modules.transfer.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gxgt.transfer.common.exception.RRException;
import gov.gxgt.transfer.modules.transfer.config.TransferConfig;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 维护、缓存和获取访问需要的token
 *
 * @author liyanjun
 */
@Component
public class TokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    @Autowired
    private TransferConfig transferConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 过期时间
     */
    private Date accessTokenExpire;
    /**
     * 过期时间
     */
    private Date tokenExpire;

    /**
     * 访问令牌
     */
    private String token;
    /**
     * 访问令牌
     */
    private String accessToken;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取访问令牌
     *
     * @return
     */
    public String getToken() throws JsonProcessingException, DocumentException {
        // 有Token未超期
        if (StringUtils.isNotBlank(token) && tokenExpire != null && tokenExpire.after(new Date())) {
            return token;
        }
        MultiValueMap map = new LinkedMultiValueMap(16);
        map.put("access_token", Collections.singletonList(getAccessToken()));
        map.put("key", Collections.singletonList(transferConfig.getKey()));
        map.put("secret", Collections.singletonList(transferConfig.getSecret()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/core/token/getXmlToken", request, String.class);
            String result = responseEntity.getBody();
            Map resultMap = (Map) XMLUtils.multilayerXmlToMap(result).get("RESULT");
            if (!"200".equals(resultMap.get("STATUS"))) {
                logger.error("获取/刷新Token异常，返回信息：" + objectMapper.writeValueAsString(result));
                throw new RRException("获取/刷新Token业务异常");
            }
            token = (String) resultMap.get("TOKEN");
            // 过期时间30分钟
            tokenExpire = new Date(System.currentTimeMillis() + 15 * 1000 * 60);
            return token;
        } catch (Exception e) {
            logger.error("获取/刷新获取AccessToken异常", e);
            throw e;
        }
    }

    /**
     * 获取AccessToken
     *
     * @return
     */
    public String getAccessToken() throws JsonProcessingException {
        // 有AccessToken未超期
        if (StringUtils.isNotBlank(accessToken) && accessTokenExpire != null && accessTokenExpire.after(new Date())) {
            return accessToken;
        }
        MultiValueMap map = new LinkedMultiValueMap(16);
        map.put("client_id", Collections.singletonList(transferConfig.getClientId()));
        map.put("client_secret", Collections.singletonList(transferConfig.getClientSecret()));
        map.put("grant_type", Collections.singletonList("client_credentials"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "token", request, Map.class);
            Map result = responseEntity.getBody();
            Map status = (Map) result.get("status");
            if (!"1".equals(status.get("code"))) {
                logger.error("获取/刷新AccessToken异常，返回信息：" + objectMapper.writeValueAsString(result));
                throw new RRException("获取/刷新AccessToken业务异常");
            }
            Map custom = (Map) result.get("custom");
            accessToken = (String) custom.get("access_token");
            // 过期时间30分钟
            accessTokenExpire = new Date(System.currentTimeMillis() + 15 * 1000 * 60);
            return accessToken;
        } catch (Exception e) {
            logger.error("获取/刷新获取AccessToken异常", e);
            throw e;
        }
    }

}
