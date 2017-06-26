package us.codecraft.webmagic.scheduler.component;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * Remove duplicate requests.
 * 
 * 删除重复request
 * 
 * @author code4crafer@gmail.com
 * @since 0.5.1
 */
public interface DuplicateRemover {
    /**
     *
     * Check whether the request is duplicate.
     * 
     * 检查请求是否重复
     *
     * @param request request
     * @param task task
     * @return true if is duplicate
     */
    public boolean isDuplicate(Request request, Task task);

    /**
     * Reset duplicate check.
     * 
     * 重置重复检查
     * 
     * @param task task
     */
    public void resetDuplicateCheck(Task task);

    /**
     * Get TotalRequestsCount for monitor.
     * 
     * 获取监视器统计
     * 
     * @param task task
     * @return number of total request
     */
    public int getTotalRequestsCount(Task task);

}
