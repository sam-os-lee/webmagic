package us.codecraft.webmagic.selector;

import java.util.List;

/**
 * Selector(extractor) for text.<br>
 * 
 * 文本选择器
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Selector {

    /**
     * Extract single result in text.<br>
     * If there are more than one result, only the first will be chosen.
     * 
     * 单个结果
     *
     * @param text text
     * @return result
     */
    public String select(String text);

    /**
     * Extract all results in text.<br>
     * 
     * 所有结果
     *
     * @param text text
     * @return results
     */
    public List<String> selectList(String text);

}
