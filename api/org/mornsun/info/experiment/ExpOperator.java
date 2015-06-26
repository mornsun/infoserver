package org.mornsun.info.experiment;

import org.mornsun.info.operator.HASHOperator;
import org.mornsun.info.operator.IOperator;
import org.mornsun.info.operator.MODOperator;
import org.mornsun.info.util.IPostprocessable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class ExpOperator implements IPostprocessable
{
    private static final Logger log = LoggerFactory.getLogger(ExpOperator.class);
    public String type = null;
    public String value = null;
    public String extra = null;
    public String relation = null;
    private IOperator m_operator = null;
    private int m_numbers[] = null;

    // private String m_strings[] = null;
    /**
	 * 
	 */
    public ExpOperator()
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
     * @return the m_operator
     */
    public IOperator getOperator()
    {
        if (null == m_operator) {
            log.error("operator is null. you should invoke postprocess anteriorly");
            throw new IllegalArgumentException(
                    "operator is null. you should invoke postprocess anteriorly");
        }
        return m_operator;
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
        if ("HASH".equals(type)) {
            m_operator = HASHOperator.getInstance();
        } else if ("MOD".equals(type)) {
            m_operator = MODOperator.getInstance();
            if (null == value) {
                log.error("config may be not initialized anteriorly: value[null]");
                throw new IllegalArgumentException(
                        "config may be not initialized anteriorly: value[null]");
            }
            m_numbers = new int[1];
            m_numbers[0] = Integer.parseInt(value);
        } else {
            log.error("the operator type is not identified: " + type);
            throw new IllegalArgumentException("the operator type is not identified: " + type);
        }
    }
}
