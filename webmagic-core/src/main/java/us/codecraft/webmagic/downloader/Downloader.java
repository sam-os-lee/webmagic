package us.codecraft.webmagic.downloader;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * Downloader is the part that downloads web pages and store in Page object. <br>
 * Downloader has {@link #setThread(int)} method because downloader is always the bottleneck of a crawler,
 * there are always some mechanisms such as pooling in downloader, and pool size is related to thread numbers.
 * 
 * downloader是下载web页面和保存page对象的一部分.
 * downloader的是爬虫的瓶颈,downloader一般使用线程池机制
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public interface Downloader {

    /**
     * Downloads web pages and store in Page object.
     * 
     * 下载web页面并保存为page对象
     *
     * @param request request
     * @param task task
     * @return page
     */
    public Page download(Request request, Task task);

    /**
     * Tell the downloader how many threads the spider used.
     * 
     * 通知downloader有多少线程spider使用
     * 
     * @param threadNum number of threads
     */
    public void setThread(int threadNum);
}
