package gov.gxgt.transfer.modules.transfer.utils;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyanjun
 */

public class MapEntryConverter implements Converter {
    @Override
    public boolean canConvert(Class clazz) {
        return AbstractMap.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        AbstractMap map = (AbstractMap) value;
        for (Object obj : map.entrySet()) {
            Map.Entry entry = (Map.Entry) obj;
            writer.startNode(entry.getKey().toString());
            if (entry.getValue() instanceof Map) {
                Map tempMap = (Map) entry.getValue();
                for (Object o : tempMap.entrySet()) {
                    Map.Entry e = (Map.Entry) o;
                }
            }
            writer.setValue(entry.getValue().toString());
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, String> map = new HashMap<String, String>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            map.put(reader.getNodeName(), reader.getValue());
            reader.moveUp();
        }
        return map;
    }
}

