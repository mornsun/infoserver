/**
 * 
 */
package org.mornsun.info.operator;

import org.mornsun.info.api.InfoReqData;
import org.mornsun.info.experiment.ExpOperator;

/**
 * @author Chauncey
 *
 */
public interface IOperator
{
    /**
     * @param expOperator
     * @param value
     * @return
     */
    public Object operate(ExpOperator expOperator, InfoReqData reqdata, Object value);

}
