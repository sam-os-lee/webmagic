package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Define the extractor for field or class.<br>
 * 
 * 定义预将提取的field或者class
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExtractBy {

    /**
     * Extractor expression, support XPath, CSS Selector and regex.
     * 
     * 提取表达式,XPath,Css和正则
     *
     * @return extractor expression
     */
    String value();

    /**
     * types of extractor expressions
     * 
     * 提取类型
     */
    public static enum Type {XPath, Regex, Css, JsonPath}

    /**
     * Extractor type, support XPath, CSS Selector and regex.
     *
     * @return extractor type
     */
    Type type() default Type.XPath;

    /**
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
     * 
     * 定义filed是否可为null,如果为true,则当提取不到结果,class将会被丢弃
     *
     * @return whether the field can be null
     */
    boolean notNull() default false;

    /**
     * types of source for extracting.
     * 
     * 提取源类型
     */
    public static enum Source {
        /**
         * extract from the content extracted by class extractor
         */
        SelectedHtml,
        /**
         * extract from the raw html
         */
        RawHtml,
        RawText
    }

    /**
     * The source for extracting. <br>
     * It works only if you already added 'ExtractBy' to Class. <br>
     *
     * @return the source for extracting
     */
    Source source() default Source.SelectedHtml;

    /**
     * Define whether the extractor return more than one result.
     * When set to 'true', the extractor return a list of string (so you should define the field as List). <br>
     * 
     * 定义是否提取返回多结果,true则返回string集合
     *
     * Deprecated since 0.4.2. This option is determined automatically by the class of field.
     * @deprecated since 0.4.2
     * @return whether the extractor return more than one result
     */
    boolean multi() default false;

}
