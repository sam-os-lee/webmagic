package us.codecraft.webmagic.model;

import us.codecraft.webmagic.model.formatter.ObjectFormatter;
import us.codecraft.webmagic.selector.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Wrapper of field and extractor.
 * 
 * 字段提取包装类
 * 
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
class FieldExtractor extends Extractor {

    private final Field field;  // 字段

    private Method setterMethod;  // 方法

    private ObjectFormatter objectFormatter;

    /**
     * @param field
     * 				成员字段
     * @param selector
     * 				内容选择器
     * @param source
     * 				源内容格式
     * @param notNull
     * 				是否支持多页面
     * @param multi
     */
    public FieldExtractor(Field field, Selector selector, Source source, boolean notNull, boolean multi) {
        super(selector, source, notNull, multi);
        this.field = field;
    }

    Field getField() {
        return field;
    }

    Selector getSelector() {
        return selector;
    }

    Source getSource() {
        return source;
    }

    void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

    Method getSetterMethod() {
        return setterMethod;
    }

    boolean isNotNull() {
        return notNull;
    }

    ObjectFormatter getObjectFormatter() {
        return objectFormatter;
    }

    void setObjectFormatter(ObjectFormatter objectFormatter) {
        this.objectFormatter = objectFormatter;
    }
}
