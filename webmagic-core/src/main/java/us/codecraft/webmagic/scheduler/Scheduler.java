package us.codecraft.webmagic.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 * 
 * scheduler是url管理部分
 * 它的功能主要有:
 * 管理url的获取
 * 删除重复url
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Scheduler {

    /**
     * add a url to fetch
     * 
     * 添加url
     *
     * @param request request
     * @param task task
     */
    public void push(Request request, Task task);

    /**
     * get an url to crawl
     * 
     * 获取url
     *
     * @param task the task of spider
     * @return the url to crawl
     */
    public Request poll(Task task);

}
