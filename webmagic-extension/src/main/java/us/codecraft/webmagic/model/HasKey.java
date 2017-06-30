package us.codecraft.webmagic.model;

import us.codecraft.webmagic.utils.Experimental;

/**
 * Interface to be implemented by page mode.<br>
 * Can be used to identify a page model, or be used as name of file storing the object.<br>
 *
 * 实现页面模式,标识页面model.或者作为存储对象为文件的名称
 * 
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Experimental
public interface HasKey {

    /**
     *
     *
     * @return key
     */
    public String key();
}
