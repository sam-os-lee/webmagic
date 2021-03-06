package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Combo 'ExtractBy' extractor with and/or operator.
 * 
 * 提取时
 * a)条件必须是and全部符合
 * b)条件只要符合一个or
 *  
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ComboExtract {

    /**
     * The extractors to be combined.
     * 
     * 联合提取
     *
     * @return the extractors to be combined
     */
    ExtractBy[] value();

    public static enum Op {
        /**
         * All extractors will be arranged as a pipeline. <br>
         * The next extractor uses the result of the previous as source.
         * 
         * 所有提取将会安排给pipeline, 下一个提取使用上一次结果
         */
        And,
        /**
         * All extractors will do extracting separately, <br>
         * and the results of extractors will combined as the final result.
         * 
         * 所有提取结果分开
         */
        Or;
    }

    /**
     * Combining operation of extractors.<br>
     * 
     * 联合提取操作
     *
     * @return combining operation of extractors
     */
    Op op() default Op.And;

    /**
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
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
        RawHtml
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
     * Deprecated since 0.4.2. This option is determined automatically by the class of field.
     * @deprecated since 0.4.2
     * @return whether the extractor return more than one result
     */
    boolean multi() default false;

}
