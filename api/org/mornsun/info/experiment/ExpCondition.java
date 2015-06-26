package org.mornsun.info.experiment;

import org.mornsun.info.util.IPostprocessable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class ExpCondition implements IPostprocessable
{
    private static final Logger log = LoggerFactory.getLogger(ExpCondition.class);
    public String key = null;
    public String relation = null;
    public ExpOperator operators[] = null;
    public ExpAsserter asserters[] = null;
    private boolean m_isAnd = true;

    /**
	 * 
	 */
    public ExpCondition()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the relation
     */
    public String getRelation()
    {
        return relation;
    }

    /**
     * @return the operators
     */
    public ExpOperator[] getOperators()
    {
        return operators;
    }

    /**
     * @return the asserters
     */
    public ExpAsserter[] getAsserters()
    {
        return asserters;
    }

    /**
     * @return the m_isAnd
     */
    public boolean isAnd()
    {
        return m_isAnd;
    }

    /**
     * @return boolean: success
     */
    public void postprocess() throws IllegalArgumentException
    {
        // check
        if (null == this.key || (null == this.asserters)) {
            log.error("config may be not initialized anteriorly: key[" + this.key + "]");
            throw new IllegalArgumentException("config may be not initialized anteriorly: key["
                    + this.key + "]");
        }
        // preparation
        if (null != this.operators) {
            for (ExpOperator operator : this.operators) {
                operator.postprocess();
            }
        }
        if (null != this.asserters) {
            for (ExpAsserter asserter : this.asserters) {
                asserter.postprocess();
            }
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
