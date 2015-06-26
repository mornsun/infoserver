/**
 * 
 */
package org.mornsun.info.operator;

import org.mornsun.info.api.InfoReqData;
import org.mornsun.info.experiment.ExpOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class HASHOperator implements IOperator
{
    private static final Logger log = LoggerFactory.getLogger(HASHOperator.class);

    /**
	 * 
	 */
    protected HASHOperator()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Initializes singleton.
     *
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    protected static class SingletonHolder
    {
        private static final HASHOperator INSTANCE = new HASHOperator();
    }

    /**
     * Retrieve the singleton instance
     * NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static HASHOperator getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mornsun.info.operator.IOperator#operate(org.mornsun.info.experiment.ExpOperator, org.mornsun.info.api.InfoReqData, java.lang.Object)
     */
    @Override
    public Object operate(ExpOperator expOperator, InfoReqData reqdata, Object value)
    {
        // TODO Auto-generated method stub
        if (null == expOperator || null == value) {
            log.error("argument illegal");
            throw new IllegalArgumentException();
        }
        Integer integer = new Integer(value.hashCode());
        // System.out.println("hashcode:"+value.hashCode());
        return integer;
    }

}
