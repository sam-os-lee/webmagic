package us.codecraft.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;

/**
 * Interface to be implemented to customize a crawler.<br>
 * <br>
 * In PageProcessor, you can customize:
 * <br>
 * start urls and other settings in {@link Site}<br>
 * how the urls to fetch are detected               <br>
 * how the data are extracted and stored             <br>
 * 
 * url如何获取
 * 数据如何提取保存
 *
 * @author code4crafter@gmail.com <br>
 * @see Site
 * @see Page
 * @since 0.1.0
 */
public interface PageProcessor {

    /**
     * process the page, extract urls to fetch, extract the data and store
     * 
     * 处理页面,提取urls,提取保存数据
     *
     * @param page page
     */
    public void process(Page page);

    /**
     * get the site settings
     * 
     * 获取页面配置
     *
     * @return site
     * @see Site
     */
    public Site getSite();
}
