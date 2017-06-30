package us.codecraft.webmagic.model;

import us.codecraft.webmagic.selector.Selector;

/**
 * The object contains 'ExtractBy' information.
 * 
 * 包含注解ExtractBy信息的类
 * 
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
class Extractor {

    protected Selector selector;  // 选择器

    protected final Source source;  // 源页面类型

    protected final boolean notNull;

    protected final boolean multi;

    static enum Source {Html, Url, RawHtml, RawText}

    /**
     * @param selector
     * 				内容选择器
     * @param source
     * 				源内容格式
     * @param notNull
     * @param multi
     * 				是否支持多页面
     */			
    public Extractor(Selector selector, Source source, boolean notNull, boolean multi) {
        this.selector = selector;
        this.source = source;
        this.notNull = notNull;
        this.multi = multi;
    }

    Selector getSelector() {
        return selector;
    }

    Source getSource() {
        return source;
    }

    boolean isNotNull() {
        return notNull;
    }

    boolean isMulti() {
        return multi;
    }

    void setSelector(Selector selector) {
        this.selector = selector;
    }
}
