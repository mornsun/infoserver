/**
 * 
 */
package org.mornsun.info.asserter;

import org.mornsun.info.experiment.ExpAsserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class RANGEAsserter implements IAsserter
{
    private static final Logger log = LoggerFactory.getLogger(RANGEAsserter.class);

    /**
	 * 
	 */
    protected RANGEAsserter()
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
        private static final RANGEAsserter INSTANCE = new RANGEAsserter();
    }

    /**
     * 
     * @return
     */
    public static RANGEAsserter getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mornsun.info.asserter.IAsserter#operate(org.mornsun.info.experiment.ExpAsserter, java.lang.Object)
     */
    @Override
    public boolean operate(ExpAsserter expAsserter, Object value)
    {
        // TODO Auto-generated method stub
        if (null == expAsserter || null == expAsserter.getValue()) { // value could be null
            log.error("argument illegal");
            throw new IllegalArgumentException();
        }
        int lv = -1;
        int rv = -1;
        try {
            if (null != value) {
                if (value.getClass() == Integer.class) {
                    lv = ((Integer) value).intValue();
                } else {
                    lv = Integer.parseInt(value.toString());
                }
                int nums[] = expAsserter.getNumbers();
                if (nums.length < 2) {
                    log.error("config illegal: range number[" + nums.length + "]");
                    throw new IllegalArgumentException();
                }
                if (lv >= nums[0] && lv <= nums[1])
                    return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            log.error("expect number string: lv[" + lv + "] rv[" + rv + "]");
            nfe.printStackTrace();
            return false; // return false for match nothing range after MOD
        }
    }
}
