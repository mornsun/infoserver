package org.mornsun.info.experiment;

import java.util.Date;

import org.mornsun.info.util.IPostprocessable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class ExpPoint implements IPostprocessable
{
    private static final Logger log = LoggerFactory.getLogger(ExpPoint.class);
    public int sid = 0;
    public int priority = 0;
    public int location = 0;
    public Date startTime = null;
    public Date endTime = null;
    public ExpCondition conditions[];

    /**
	 * 
	 */
    public ExpPoint()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the sid
     */
    public int getSid()
    {
        return sid;
    }

    /**
     * @return the priority
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * @return the location
     */
    public int getLocation()
    {
        return location;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime()
    {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime()
    {
        return endTime;
    }

    /**
     * @return the conditions
     */
    public ExpCondition[] getConditions()
    {
        return conditions;
    }

    /**
     * @return boolean: success
     */
    public void postprocess() throws IllegalArgumentException
    {
        // check
        if (null == conditions) {
            log.error("condition is null: sid[" + sid + "]");
            throw new IllegalArgumentException("condition is null: sid[" + sid + "]");
        }
        if (null == startTime || null == endTime || startTime.after(endTime)) {
            log.error("starttime or endtime error: sid[" + sid + "]");
            throw new IllegalArgumentException("starttime or endtime error: sid[" + sid + "]");
        }
        // preparation
        for (ExpCondition condition : conditions) {
            condition.postprocess();
        }
    }

}
