package us.codecraft.webmagic.utils;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Tools for annotation converting. <br>
 * 
 * 转换字段的ExtractBy注解
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class ExtractorUtils {

	/**
	 * 内容选择器, 字段提取规则
	 * 
	 * @param extractBy
	 * @return
	 */
    public static Selector getSelector(ExtractBy extractBy) {
        String value = extractBy.value();
        Selector selector;
        // ExtractBy 默认type是XPath
        switch (extractBy.type()) {
            case Css:
                selector = new CssSelector(value);  // css选择器
                break;
            case Regex:
                selector = new RegexSelector(value);  // 正则选择器
                break;
            case XPath:
                selector = getXpathSelector(value);
                break;
            case JsonPath:
                selector = new JsonPathSelector(value);  // json选择器
                break;
            default:
                selector = getXpathSelector(value);
        }
        return selector;
    }

    private static Selector getXpathSelector(String value) {
        Selector selector = new XpathSelector(value);
        return selector;
    }

    /**
     * 内容选择器, 字段提取规则集
     * 
     * @param extractBies
     * @return
     */
    public static List<Selector> getSelectors(ExtractBy[] extractBies) {
        List<Selector> selectors = new ArrayList<Selector>();
        if (extractBies == null) {
            return selectors;
        }
        for (ExtractBy extractBy : extractBies) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
