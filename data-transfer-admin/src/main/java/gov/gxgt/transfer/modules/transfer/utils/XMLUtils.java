package gov.gxgt.transfer.modules.transfer.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liyanjun
 */

public class XMLUtils {
    /**
     * (多层)map转换为xml格式字符串
     *
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    public static String multilayerMapToXml(Map map, boolean isCDATA, String parentName){
        Document doc = DocumentHelper.createDocument();
        doc.addElement(parentName);
        doc.setXMLEncoding("utf-8");
        String xml = recursionMapToXml(doc.getRootElement(), parentName, map, isCDATA);
        return xml;
    }

    /**
     * multilayerMapToXml核心方法，递归调用
     *
     * @param element 节点元素
     * @param parentName 根元素属性名
     * @param map 需要转换为xml的map
     * @param isCDATA 是否加入CDATA标识符 true:加入 false:不加入
     * @return xml字符串
     */
    @SuppressWarnings("unchecked")
    private static String recursionMapToXml(Element element, String parentName, Map<String, Object> map, boolean isCDATA) {
        Element xmlElement = element.addElement(parentName);
        map.keySet().forEach(key -> {
            Object obj = map.get(key);
            if (obj instanceof Map) {
                recursionMapToXml(xmlElement, key, (Map<String, Object>)obj, isCDATA);
            } else {
                String value = obj == null ? "" : obj.toString();
                if (isCDATA) {
                    xmlElement.addElement(key).addCDATA(value);
                } else {
                    xmlElement.addElement(key).addText(value);
                }
            }
        });
        return xmlElement.asXML();
    }

    /**
     * (多层)xml格式字符串转换为map
     *
     * @param xml xml字符串
     * @return 第一个为Root节点，Root节点之后为Root的元素，如果为多层，可以通过key获取下一层Map
     */
    public static Map<String, Object> multilayerXmlToMap(String xml) throws DocumentException {
        Document doc = null;
        doc = DocumentHelper.parseText(xml);
        Map<String, Object> map = new HashMap<>();
        if (null == doc) {
            return map;
        }
        // 获取根元素
        Element rootElement = doc.getRootElement();
        recursionXmlToMap(rootElement,map);
        return map;
    }

    /**
     * multilayerXmlToMap核心方法，递归调用
     *
     * @param element 节点元素
     * @param outmap 用于存储xml数据的map
     */
    @SuppressWarnings("unchecked")
    private static void recursionXmlToMap(Element element, Map<String, Object> outmap) {
        // 得到根元素下的子元素列表
        List<Element> list = element.elements();
        int size = list.size();
        if (size == 0) {
            // 如果没有子元素,则将其存储进map中
            outmap.put(element.getName(), element.getTextTrim());
        } else {
            // innermap用于存储子元素的属性名和属性值
            Map<String, Object> innermap = new HashMap<>();
            // 遍历子元素
            list.forEach(childElement -> recursionXmlToMap(childElement, innermap));
            outmap.put(element.getName(), innermap);
        }
    }

}
