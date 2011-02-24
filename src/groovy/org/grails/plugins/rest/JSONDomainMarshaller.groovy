package org.grails.plugins.rest

//
// CustomDomainMarshaller.groovy by Siegfried Puchbauer
//
// http://stackoverflow.com/questions/1700668/grails-jsonp-callback-without-id-and-class-in-json-file/1701258#1701258
//

import grails.converters.JSON;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException;
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller;
import org.codehaus.groovy.grails.web.json.JSONWriter;
import org.springframework.beans.BeanUtils;

public class JSONDomainMarshaller implements ObjectMarshaller<JSON> {

    static EXCLUDED = ['metaClass','class','version']

    public boolean supports(Object object) {
        return ConverterUtil.isDomainClass(object.getClass());
    }

    public void marshalObject(Object o, JSON json) throws ConverterException {
        JSONWriter writer = json.getWriter();
        try {
            writer.object();
            def properties = BeanUtils.getPropertyDescriptors(o.getClass());
            for (property in properties) {
                String name = property.getName();
                        if(!EXCLUDED.contains(name)) {
                    def readMethod = property.getReadMethod();
                    if (readMethod != null) {
                        def value = readMethod.invoke(o, (Object[]) null);
                        writer.key(name);
                        json.convertAnother(value);
                    }
                        }
            }
            writer.endObject();
        } catch (Exception e) {
            throw new ConverterException("Exception in JSONDomainMarshaller", e);
        }
    }
}