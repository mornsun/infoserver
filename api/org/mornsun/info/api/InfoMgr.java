/**
 * 
 */
package org.mornsun.info.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.PropertyConfigurator;
import org.mornsun.info.asserter.IAsserter;
import org.mornsun.info.experiment.ExpAsserter;
import org.mornsun.info.experiment.ExpCondition;
import org.mornsun.info.experiment.ExpMgr;
import org.mornsun.info.experiment.ExpOperator;
import org.mornsun.info.experiment.ExpPoint;
import org.mornsun.info.geoip.GeoIPEntry;
import org.mornsun.info.geoip.GeoIPMgr;
import org.mornsun.info.operator.IOperator;
import org.mornsun.info.server.InfoChannelHandler;
import org.mornsun.info.util.IUpdatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class InfoMgr implements IUpdatable
{
    private static final Logger log = LoggerFactory.getLogger(InfoChannelHandler.class);
    private static Timer m_timer = null;
    private static InfoMgr INSTANCE = null;

    /**
     * Retrieve the singleton instance
     * NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static InfoMgr getInstance()
    {
        if (INSTANCE == null) {
            synchronized (InfoMgr.class) {
                if (INSTANCE == null) {
                    InfoMgr instance = new InfoMgr();
                    instance.init();
                    INSTANCE = instance;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * When it is necessary, update all the configurations of this manager
     */
    public boolean update()
    {
        boolean isExpUpdated = ExpMgr.getInstance().update();
        boolean isIPUpdated = GeoIPMgr.getInstance().update();
        return isExpUpdated || isIPUpdated; // true if anyone of these updatable plugins is updated
    }

    /**
	 * 
	 */
    protected InfoMgr()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Initialize this instance
     */
    protected void init()
    {
        ExpMgr.getInstance();
        GeoIPMgr.getInstance();
    }

    /**
     * 
     * @param period
     */
    public void startAutoUpdate(int period)
    {
        stopAutoUpdate(); // in case of re-entrance of this function
        if (null == m_timer) {
            synchronized (InfoMgr.class) {
                if (m_timer == null) {
                    m_timer = new Timer();
                }
            }
        }
        m_timer.schedule(new UpdateTask(), period * 1000, period * 1000); // call task per period seconds
    }

    /**
	 * 
	 */
    public void stopAutoUpdate()
    {
        if (m_timer != null) {
            m_timer.cancel();
            m_timer.purge();
            m_timer = null;
        }
    }

    /**
     * API function: execute the handling info progression
     * 
     * @param reqdata
     * @return
     */
    public InfoResData execute(InfoReqData reqdata)
    {
        if (null == reqdata) {
            log.error("reqdata[null]");
            throw new IllegalArgumentException("reqdata[null]");
        }
        InfoLocation location = reqdata.getLocation(); // MUST be NOT null
        // IP recognition executes once ( switch is open AND IP exists AND () location need be merged)
        if (reqdata.isLocSwitch() && reqdata.getIp() != null && !"".equals(reqdata.getIp())
                && location.needMerge()) {
            GeoIPEntry entry = GeoIPMgr.getInstance().find(reqdata.getIp());
            if (null == entry) {
                log.warn("ip cannot be recognized:" + reqdata.getIp());
            } else {
                location.merge(entry);
            }
        }
        // Experiments
        Date curr_date = new Date();
        ExpPoint points[] = ExpMgr.getInstance().getExperiment().getPoints();
        // iterate all the experimental points
        ArrayList<Integer> sid_list = new ArrayList<Integer>();
        for (ExpPoint point : points) {
            boolean res = filterPoint(point, reqdata, curr_date);
            if (res) {
                sid_list.add(point.getSid());
            }
        }
        InfoResData resdata = new InfoResData(sid_list, location);
        return resdata;
    }

    /**
     * Handle all the condition filters
     * 
     * @param condition
     * @param reqdata
     * @return
     */
    protected static boolean filterCondition(ExpCondition condition, InfoReqData reqdata)
    {
        Object value = reqdata.get(condition.getKey());
        if (null != condition.getOperators()) {
            for (ExpOperator cfgOperator : condition.getOperators()) {
                IOperator hOperator = cfgOperator.getOperator();
                value = hOperator.operate(cfgOperator, reqdata, value);
            }
        }
        // when all AND asserters =true || a OR asserter =true, the function returns true
        boolean hasAnd = false;
        boolean andHasFalse = false;
        for (ExpAsserter cfgAsserter : condition.getAsserters()) {
            if (andHasFalse && cfgAsserter.isAnd()) // once an AND asserter is false, skip all AND asserters
                continue;
            IAsserter hAsserter = cfgAsserter.getAsserter();
            boolean res = hAsserter.operate(cfgAsserter, value);
            if (res) {
                if (cfgAsserter.isAnd()) {
                    hasAnd = true;
                } else {
                    andHasFalse = false;
                    hasAnd = true; // assign these for returning true after satisfying at least an OR asserter
                    break; // satisfy the asserter and the OR relation, win the experimental asserters
                }
            } else {
                if (cfgAsserter.isAnd()) {
                    andHasFalse = true;
                    hasAnd = true;
                }
            }
        }
        return hasAnd && !andHasFalse;
    }

    /**
     * Handle all the point filters
     * 
     * @param point
     * @param reqdata
     * @param currDate
     * @return
     */
    protected static boolean filterPoint(ExpPoint point, InfoReqData reqdata, Date currDate)
    {
        if (currDate.before(point.getStartTime()) || currDate.after(point.getEndTime()))
            return false; // do not satisfy the criteria of the period of the experiment
        // when all AND conditions =true || a OR condition =true, the function returns true
        boolean hasAnd = false;
        boolean andHasFalse = false;
        for (ExpCondition condition : point.getConditions()) {
            if (andHasFalse && condition.isAnd()) // once an AND condition is false, skip all AND conditions
                continue;
            boolean res = filterCondition(condition, reqdata);
            if (res) {
                if (condition.isAnd()) {
                    hasAnd = true;
                } else {
                    andHasFalse = false;
                    hasAnd = true; // assign these for returning true after satisfying at least an OR condition
                    break; // satisfy the asserter and the OR relation, win the experimental conditions
                }
            } else {
                if (condition.isAnd()) {
                    andHasFalse = true;
                    hasAnd = true;
                }
            }
        }
        return hasAnd && !andHasFalse;
    }

    /**
     * 
     * @author Chauncey
     *
     */
    protected class UpdateTask extends TimerTask
    {
        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run()
        {
            update();
        }
    }

    /**
     * Only for testing
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        String configFolder = System.getProperty("config", "./config/");
        System.setProperty("infoserver.dir", configFolder + "../");
        // log configuration
        String log4jConfig = configFolder + File.separator + "log4j.properties";
        System.out.println("log4j: " + log4jConfig);
        PropertyConfigurator.configure(log4jConfig);

        InfoMgr infoMgr = InfoMgr.getInstance();
        System.out.println("Finish loading configurations.");
        InfoReqData reqdata = new InfoReqData();

        for (int k = 0; k < 5; ++k) {
            long start_time = System.currentTimeMillis();
            for (int i = 0; i < 1000000; ++i) {
                // InfoLocation location = new InfoLocation("cmcc","中国", "湖南", "长沙");
                // InfoReqData reqdata = new InfoReqData("50001", "10.85.208.35", "baidu", adapter, location, true);
                reqdata.setBasic("50001", "202.85.213.219", "baidu");
                // reqdata.setBasic("50001", "192.168.0.1", "baidu");
                reqdata.setTerminal("Vilosnap", "1.3.1", "IOS", "8.0.1");
                reqdata.setLocation(null, null, null, null, true);
                InfoResData resdata = infoMgr.execute(reqdata);
                System.out.println("Finish the experiment: " + resdata);
            }
            System.out.println("Send time elapsed:" + (System.currentTimeMillis() - start_time));
        }

    }
}
