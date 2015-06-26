package org.mornsun.info.experiment;

import org.mornsun.info.asserter.EQAsserter;
import org.mornsun.info.asserter.IAsserter;
import org.mornsun.info.asserter.RANGEAsserter;
import org.mornsun.info.util.IPostprocessable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class ExpAsserter implements IPostprocessable
{
    private static final Logger log = LoggerFactory.getLogger(ExpAsserter.class);
    public String type = null;
    public String value = null;
    public String extra = null;
    public String relation = null;
    private IAsserter m_asserter = null;
    private boolean m_isAnd = true;
    private int m_numbers[] = null;

    // private String m_strings[] = null;
    /**
	 * 
	 */
    public ExpAsserter()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @return the extra
     */
    public String getExtra()
    {
        return extra;
    }

    /**
     * @return the relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @return the m_assertor
     */
    public IAsserter getAsserter()
    {
        if (null == m_asserter) {
            log.error("assertor is null. you should invoke postprocess anteriorly");
            throw new IllegalArgumentException(
                    "assertor is null. you should invoke postprocess anteriorly");
        }
        return m_asserter;
    }

    /**
     * @return the m_isAnd
     */
    public boolean isAnd()
    {
        return m_isAnd;
    }

    /**
     * @return the m_numbers
     */
    public int[] getNumbers()
    {
        return m_numbers;
    }

    /**
     * @return boolean: success
     */
    public void postprocess() throws IllegalArgumentException
    {
        // check
        if (null == type) {
            log.error("config may be not initialized anteriorly: type[null]");
            throw new IllegalArgumentException(
                    "config may be not initialized anteriorly: type[null]");
        }
        // prepare the operators
        if ("EQ".equals(type)) {
            m_asserter = EQAsserter.getInstance();
        } else if ("RANGE".equals(type)) {
            m_asserter = RANGEAsserter.getInstance();
            String strs[];
            if (null == value || (strs = value.split("-")).length < 2) {
                log.error("config may be not initialized anteriorly: value[" + value + "]");
                throw new IllegalArgumentException(
                        "config may be not initialized anteriorly: value[" + value + "]");
            }
            m_numbers = new int[2];
            m_numbers[0] = Integer.parseInt(strs[0]);
            m_numbers[1] = Integer.parseInt(strs[1]);
        } else {
            log.error("the assertor type is not identified: " + type);
            throw new IllegalArgumentException("the assertor type is not identified: " + type);
        }
        // prepare the relation
        if (null == relation || "AND".equals(relation)) {
            m_isAnd = true;
        } else if ("OR".equals(relation)) {
            m_isAnd = false;
        } else {
            log.error("the relation is not available: " + relation);
            throw new IllegalArgumentException("the relation is not available: " + relation);
        }
    }

}
