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
public class EQAsserter implements IAsserter
{
    private static final Logger log = LoggerFactory.getLogger(EQAsserter.class);

    /**
	 * 
	 */
    protected EQAsserter()
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
        private static final EQAsserter INSTANCE = new EQAsserter();
    }

    /**
     * 
     * @return
     */
    public static EQAsserter getInstance()
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
            log.error("argument illegal: expAsserter[" + expAsserter + "]");
            throw new IllegalArgumentException();
        }
        // System.out.println(expAsserter.getValue() + "==" + value.toString());
        if (null != value && expAsserter.getValue().equals(value.toString())) {
            return true;
        }
        return false;
    }
}
