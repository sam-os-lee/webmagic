package us.codecraft.webmagic.model;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.annotation.*;
import us.codecraft.webmagic.model.formatter.BasicTypeFormatter;
import us.codecraft.webmagic.model.formatter.ObjectFormatter;
import us.codecraft.webmagic.model.formatter.ObjectFormatters;
import us.codecraft.webmagic.selector.*;
import us.codecraft.webmagic.utils.ClassUtils;
import us.codecraft.webmagic.utils.ExtractorUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The main internal logic of page model extractor.
 * 
 * 主要内部pagemodel提取类
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
class PageModelExtractor {

    private List<Pattern> targetUrlPatterns = new ArrayList<Pattern>();  // 目标url集合

    private Selector targetUrlRegionSelector;  // xpath region目标url提取

    private List<Pattern> helpUrlPatterns = new ArrayList<Pattern>();  // 帮助url集合

    private Selector helpUrlRegionSelector;  // 帮助url提取

    private Class clazz;

    private List<FieldExtractor> fieldExtractors;  // 字段提取类

    private Extractor objectExtractor;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 
     * @param clazz
     *  		 具体的PageMode类
     * @return
     */
    public static PageModelExtractor create(Class clazz) {
        PageModelExtractor pageModelExtractor = new PageModelExtractor();
        pageModelExtractor.init(clazz);
        return pageModelExtractor;
    }

    /**
     * @param clazz PageMode类
     * @return
     */
    private void init(Class clazz) {
    	
        this.clazz = clazz;
        
        // 初始化class的定义注解
        initClassExtractors();
        
        fieldExtractors = new ArrayList<FieldExtractor>();
        
        // 迭代获取类所有声明的字段,获取字段的注解
        for (Field field : ClassUtils.getFieldsIncludeSuperClass(clazz)) {
        	
            field.setAccessible(true);
            // 获取每个字段的ExtractBy注解
            FieldExtractor fieldExtractor = getAnnotationExtractBy(clazz, field);
            
            // 获取每个字段的ComboExtract注解
            FieldExtractor fieldExtractorTmp = getAnnotationExtractCombo(clazz, field);
            
            // ExtractBy和ComboExtract和ExtractByUrl不能同时注解到同一个字段
            if (fieldExtractor != null && fieldExtractorTmp != null) {
                throw new IllegalStateException("Only one of 'ExtractBy ComboExtract ExtractByUrl' can be added to a field!");
            } else if (fieldExtractor == null && fieldExtractorTmp != null) {
                fieldExtractor = fieldExtractorTmp;
            }
            
            // 获取每个字段的ExtractByUrl注解
            fieldExtractorTmp = getAnnotationExtractByUrl(clazz, field);
            // ExtractBy和ComboExtract和ExtractByUrl不能同时注解到同一个字段
            if (fieldExtractor != null && fieldExtractorTmp != null) {
                throw new IllegalStateException("Only one of 'ExtractBy ComboExtract ExtractByUrl' can be added to a field!");
            } else if (fieldExtractor == null && fieldExtractorTmp != null) {
                fieldExtractor = fieldExtractorTmp;
            }
            
            if (fieldExtractor != null) {
                checkFormat(field, fieldExtractor);
                fieldExtractors.add(fieldExtractor);
            }
        }
    }

    /**
     * 
     * 
     * @param field
     * @param fieldExtractor
     */
    private void checkFormat(Field field, FieldExtractor fieldExtractor) {
        //check custom formatter
    	// 检查是否自定义字段转换
        Formatter formatter = field.getAnnotation(Formatter.class);
        if (formatter != null && !formatter.formatter().equals(ObjectFormatter.class)) {
            if (formatter != null) {
            	
            	// 实例化自定义转换类
                if (!formatter.formatter().equals(ObjectFormatter.class)) {
                    ObjectFormatter objectFormatter = initFormatter(formatter.formatter());
                    objectFormatter.initParam(formatter.value());
                    fieldExtractor.setObjectFormatter(objectFormatter);
                    return;
                }
            }
        }
        
        // 根据字段类型,实例化字段转换类
        if (!fieldExtractor.isMulti() && !String.class.isAssignableFrom(field.getType())) {
        	
            Class<?> fieldClazz = BasicTypeFormatter.detectBasicClass(field.getType());
            ObjectFormatter objectFormatter = getObjectFormatter(field, fieldClazz, formatter);
            if (objectFormatter == null) {
                throw new IllegalStateException("Can't find formatter for field " + field.getName() + " of type " + fieldClazz);
            } else {
                fieldExtractor.setObjectFormatter(objectFormatter);
            }
        } else if (fieldExtractor.isMulti()) {
            if (!List.class.isAssignableFrom(field.getType())) {
                throw new IllegalStateException("Field " + field.getName() + " must be list");
            }
            if (formatter != null) {
                if (!formatter.subClazz().equals(Void.class)) {
                    ObjectFormatter objectFormatter = getObjectFormatter(field, formatter.subClazz(), formatter);
                    if (objectFormatter == null) {
                        throw new IllegalStateException("Can't find formatter for field " + field.getName() + " of type " + formatter.subClazz());
                    } else {
                        fieldExtractor.setObjectFormatter(objectFormatter);
                    }
                }
            }
        }
    }

    private ObjectFormatter getObjectFormatter(Field field, Class<?> fieldClazz, Formatter formatter) {
        return initFormatter(ObjectFormatters.get(fieldClazz));
    }

    private ObjectFormatter initFormatter(Class<? extends ObjectFormatter> formatterClazz) {
        try {
            return formatterClazz.newInstance();
        } catch (InstantiationException e) {
            logger.error("init ObjectFormatter fail", e);
        } catch (IllegalAccessException e) {
            logger.error("init ObjectFormatter fail", e);
        }
        return null;
    }

    /**
     * 获取每个字段的ExtractByUrl注解
     * 
     * @param clazz
     * @param field
     * @return
     */
    private FieldExtractor getAnnotationExtractByUrl(Class clazz, Field field) {
        FieldExtractor fieldExtractor = null;
        ExtractByUrl extractByUrl = field.getAnnotation(ExtractByUrl.class);
        if (extractByUrl != null) {
        	// 获取正则提取模式
            String regexPattern = extractByUrl.value();
            if (regexPattern.trim().equals("")) {
                regexPattern = ".*";
            }
            fieldExtractor = new FieldExtractor(field,
                    new RegexSelector(regexPattern), FieldExtractor.Source.Url, extractByUrl.notNull(),
                    extractByUrl.multi() || List.class.isAssignableFrom(field.getType()));
            Method setterMethod = getSetterMethod(clazz, field);
            if (setterMethod != null) {
                fieldExtractor.setSetterMethod(setterMethod);
            }
        }
        return fieldExtractor;
    }

    /**
     * 获取每个字段的ComboExtract注解
     * 
     * @param clazz
     * @param field
     * @return
     */
    private FieldExtractor getAnnotationExtractCombo(Class clazz, Field field) {
        FieldExtractor fieldExtractor = null;
        ComboExtract comboExtract = field.getAnnotation(ComboExtract.class);
        if (comboExtract != null) {
            
        	// comboExtract.value值是ExtractBy注解数组
        	ExtractBy[] extractBies = comboExtract.value();
            Selector selector;
            
            // 提取操作
            switch (comboExtract.op()) {
                case And:
                    selector = new AndSelector(ExtractorUtils.getSelectors(extractBies));
                    break;
                case Or:
                    selector = new OrSelector(ExtractorUtils.getSelectors(extractBies));
                    break;
                default:
                    selector = new AndSelector(ExtractorUtils.getSelectors(extractBies));
            }
            fieldExtractor = new FieldExtractor(field, selector, comboExtract.source() == ComboExtract.Source.RawHtml ? FieldExtractor.Source.RawHtml : FieldExtractor.Source.Html,
                    comboExtract.notNull(), comboExtract.multi() || List.class.isAssignableFrom(field.getType()));
            Method setterMethod = getSetterMethod(clazz, field);
            if (setterMethod != null) {
                fieldExtractor.setSetterMethod(setterMethod);
            }
        }
        return fieldExtractor;
    }

    /**
     * 获取每个字段的ExtractBy注解
     * 
     * @param clazz
     * @param field
     * @return
     */
    private FieldExtractor getAnnotationExtractBy(Class clazz, Field field) {
        FieldExtractor fieldExtractor = null;
        ExtractBy extractBy = field.getAnnotation(ExtractBy.class);
        if (extractBy != null) {
        	
        	// 获取内容选择器(XPath, Regex, Css, JsonPath)
            Selector selector = ExtractorUtils.getSelector(extractBy);

            // 字段源类型
            FieldExtractor.Source source = null;
            switch (extractBy.source()){
                case  RawText:
                    source = FieldExtractor.Source.RawText;
                    break;
                case RawHtml:
                    source = FieldExtractor.Source.RawHtml;
                    break;
                case SelectedHtml:
                    source =FieldExtractor.Source.Html;
                    break;
                default:
                    source =FieldExtractor.Source.Html;

            }

            // 字段提取类
            fieldExtractor = new FieldExtractor(field, selector, source,
                    extractBy.notNull(), extractBy.multi() || List.class.isAssignableFrom(field.getType()));
            
            Method setterMethod = getSetterMethod(clazz, field);
            if (setterMethod != null) {
                fieldExtractor.setSetterMethod(setterMethod);
            }
        }
        return fieldExtractor;
    }

    /**
     * 获取字段设置方法
     * 
     * @param clazz
     *       		类
     * @param field
     * 				字段
     * @return
     */
    public static Method getSetterMethod(Class clazz, Field field) {
        String name = "set" + StringUtils.capitalize(field.getName());
        try {
            Method declaredMethod = clazz.getDeclaredMethod(name, field.getType());
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 获取pagemode类注解配置
     * annotation:
     * 		TargetUrl
     * 		HelpUrl
     * 		ExtractBy
     */
    private void initClassExtractors() {
    	// 定义爬取提取的Target
        Annotation annotation = clazz.getAnnotation(TargetUrl.class);
        if (annotation == null) {
            targetUrlPatterns.add(Pattern.compile("(.*)"));
        } else {
            TargetUrl targetUrl = (TargetUrl) annotation;
            String[] value = targetUrl.value();
            
            // 迭代预爬取目标链接,并转换关键字
            for (String s : value) {
                targetUrlPatterns.add(Pattern.compile("(" + s.replace(".", "\\.").replace("*", "[^\"'#]*") + ")"));
            }
            
            // 如果定义了xpath格式语法
            if (!targetUrl.sourceRegion().equals("")) {
                targetUrlRegionSelector = new XpathSelector(targetUrl.sourceRegion());
            }
        }
        
        // 定义爬取提取的HelpUrl
        annotation = clazz.getAnnotation(HelpUrl.class);
        if (annotation != null) {
            HelpUrl helpUrl = (HelpUrl) annotation;
            String[] value = helpUrl.value();
            
            // 迭代预爬取帮助链接,并转换关键字
            for (String s : value) {
                helpUrlPatterns.add(Pattern.compile("(" + s.replace(".", "\\.").replace("*", "[^\"'#]*") + ")"));
            }
            
            // 如果定义了xpath格式语法
            if (!helpUrl.sourceRegion().equals("")) {
                helpUrlRegionSelector = new XpathSelector(helpUrl.sourceRegion());
            }
        }
        
        // 定义爬取提取的ExtractBy
        annotation = clazz.getAnnotation(ExtractBy.class);
        if (annotation != null) {
            ExtractBy extractBy = (ExtractBy) annotation;
            objectExtractor = new Extractor(new XpathSelector(extractBy.value()), Extractor.Source.Html, extractBy.notNull(), extractBy.multi());
        }
    }

    public Object process(Page page) {
        boolean matched = false;
        for (Pattern targetPattern : targetUrlPatterns) {
            if (targetPattern.matcher(page.getUrl().toString()).matches()) {
                matched = true;
            }
        }
        if (!matched) {
            return null;
        }
        if (objectExtractor == null) {
            return processSingle(page, null, true);
        } else {
            if (objectExtractor.multi) {
                List<Object> os = new ArrayList<Object>();
                List<String> list = objectExtractor.getSelector().selectList(page.getRawText());
                for (String s : list) {
                    Object o = processSingle(page, s, false);
                    if (o != null) {
                        os.add(o);
                    }
                }
                return os;
            } else {
                String select = objectExtractor.getSelector().select(page.getRawText());
                Object o = processSingle(page, select, false);
                return o;
            }
        }
    }

    private Object processSingle(Page page, String html, boolean isRaw) {
        Object o = null;
        try {
            o = clazz.newInstance();
            for (FieldExtractor fieldExtractor : fieldExtractors) {
                if (fieldExtractor.isMulti()) {
                    List<String> value;
                    switch (fieldExtractor.getSource()) {
                        case RawHtml:
                            value = page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
                            break;
                        case Html:
                            if (isRaw) {
                                value = page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
                            } else {
                                value = fieldExtractor.getSelector().selectList(html);
                            }
                            break;
                        case Url:
                            value = fieldExtractor.getSelector().selectList(page.getUrl().toString());
                            break;
                        case RawText:
                            value = fieldExtractor.getSelector().selectList(page.getRawText());
                            break;
                        default:
                            value = fieldExtractor.getSelector().selectList(html);
                    }
                    if ((value == null || value.size() == 0) && fieldExtractor.isNotNull()) {
                        return null;
                    }
                    if (fieldExtractor.getObjectFormatter() != null) {
                        List<Object> converted = convert(value, fieldExtractor.getObjectFormatter());
                        setField(o, fieldExtractor, converted);
                    } else {
                        setField(o, fieldExtractor, value);
                    }
                } else {
                    String value;
                    switch (fieldExtractor.getSource()) {
                        case RawHtml:
                            value = page.getHtml().selectDocument(fieldExtractor.getSelector());
                            break;
                        case Html:
                            if (isRaw) {
                                value = page.getHtml().selectDocument(fieldExtractor.getSelector());
                            } else {
                                value = fieldExtractor.getSelector().select(html);
                            }
                            break;
                        case Url:
                            value = fieldExtractor.getSelector().select(page.getUrl().toString());
                            break;
                        case RawText:
                            value = fieldExtractor.getSelector().select(page.getRawText());
                            break;
                        default:
                            value = fieldExtractor.getSelector().select(html);
                    }
                    if (value == null && fieldExtractor.isNotNull()) {
                        return null;
                    }
                    if (fieldExtractor.getObjectFormatter() != null) {
                        Object converted = convert(value, fieldExtractor.getObjectFormatter());
                        if (converted == null && fieldExtractor.isNotNull()) {
                            return null;
                        }
                        setField(o, fieldExtractor, converted);
                    } else {
                        setField(o, fieldExtractor, value);
                    }
                }
            }
            if (AfterExtractor.class.isAssignableFrom(clazz)) {
                ((AfterExtractor) o).afterProcess(page);
            }
        } catch (InstantiationException e) {
            logger.error("extract fail", e);
        } catch (IllegalAccessException e) {
            logger.error("extract fail", e);
        } catch (InvocationTargetException e) {
            logger.error("extract fail", e);
        }
        return o;
    }

    private Object convert(String value, ObjectFormatter objectFormatter) {
        try {
            Object format = objectFormatter.format(value);
            logger.debug("String {} is converted to {}", value, format);
            return format;
        } catch (Exception e) {
            logger.error("convert " + value + " to " + objectFormatter.clazz() + " error!", e);
        }
        return null;
    }

    private List<Object> convert(List<String> values, ObjectFormatter objectFormatter) {
        List<Object> objects = new ArrayList<Object>();
        for (String value : values) {
            Object converted = convert(value, objectFormatter);
            if (converted != null) {
                objects.add(converted);
            }
        }
        return objects;
    }

    private void setField(Object o, FieldExtractor fieldExtractor, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        if (fieldExtractor.getSetterMethod() != null) {
            fieldExtractor.getSetterMethod().invoke(o, value);
        }
        fieldExtractor.getField().set(o, value);
    }

    Class getClazz() {
        return clazz;
    }

    List<Pattern> getTargetUrlPatterns() {
        return targetUrlPatterns;
    }

    List<Pattern> getHelpUrlPatterns() {
        return helpUrlPatterns;
    }

    Selector getTargetUrlRegionSelector() {
        return targetUrlRegionSelector;
    }

    Selector getHelpUrlRegionSelector() {
        return helpUrlRegionSelector;
    }
}
