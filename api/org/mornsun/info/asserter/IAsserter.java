/**
 * 
 */
package org.mornsun.info.asserter;

import org.mornsun.info.experiment.ExpAsserter;

/**
 * @author Chauncey
 *
 */
public interface IAsserter
{
    /**
     * Operate this asserter
     * NOTE: value could be null
     * 
     * @param samplOperator
     * @param value
     * @return
     */
    public boolean operate(ExpAsserter samplOperator, Object value);

}
