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
public class MODOperator implements IOperator
{
    private static final Logger log = LoggerFactory.getLogger(MODOperator.class);

    /**
	 * 
	 */
    protected MODOperator()
    {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mornsun.infoserver.filter.ExprFilter#filter()
     */
    @Override
    public Object operate(ExpOperator expOperator, InfoReqData reqdata, Object value)
    {
        // TODO Auto-generated method stub
        if (null == expOperator || null == value || null == expOperator.getValue()) {
            log.error("argument illegal");
            throw new IllegalArgumentException();
        }
        int lv = -1;
        int rv = -1;
        try {
            if (value.getClass() == Integer.class) { // TODO: instanceof tuning
                lv = ((Integer) value).intValue();
            } else {
                lv = Integer.parseInt(value.toString());
            }
            rv = expOperator.getNumbers()[0];
            Integer integer = new Integer(lv % rv);
            return integer;
        } catch (NumberFormatException nfe) {
            log.error("expect number string: lv[" + lv + "] rv[" + rv + "]");
            nfe.printStackTrace();
            return new Integer(-1); // return -1 for match nothing range after MOD
        }
    }

    /**
     * Initializes singleton.
     *
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    protected static class SingletonHolder
    {
        private static final MODOperator INSTANCE = new MODOperator();
    }

    /**
     * Retrieve the singleton instance
     * NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static MODOperator getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

}
