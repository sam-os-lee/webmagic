package us.codecraft.webmagic.utils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public abstract class ClassUtils {

	/**
	 * 迭代获取类所有声明的字段
	 * 
	 * @param clazz
	 * @return
	 */
    public static Set<Field> getFieldsIncludeSuperClass(Class clazz) {
        Set<Field> fields = new LinkedHashSet<Field>();
        Class current = clazz;
        // 迭代获取类所有声明的字段,包括父类+
        while (current != null) {
            Field[] currentFields = current.getDeclaredFields();
            for (Field currentField : currentFields) {
                fields.add(currentField);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

}
