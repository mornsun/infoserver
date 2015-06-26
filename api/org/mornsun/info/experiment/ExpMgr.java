package org.mornsun.info.experiment;

import java.io.File;
import java.io.FileNotFoundException;

import org.ho.yaml.Yaml;
import org.mornsun.info.util.IUpdatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chauncey
 *
 */
public class ExpMgr implements IUpdatable
{
    private static final Logger log = LoggerFactory.getLogger(ExpMgr.class);
    private static ExpMgr INSTANCE = null;
    private static long m_lastUpdateTime = -1;
    private static final Object updateLock = new Object();

    /**
     * Atomic: if you read or write it, you must assure that it is an atomic operation
     */
    private Experiment m_experiment = null;

    /**
     * Retrieve the singleton instance
     * NOTE: It is strongly recommended to invoke this at the period of loading configurations or initialization.
     * 
     * @return
     */
    public static ExpMgr getInstance()
    {
        if (INSTANCE == null) {
            synchronized (ExpMgr.class) {
                if (INSTANCE == null) {
                    ExpMgr instance = new ExpMgr();
                    instance.init();
                    INSTANCE = instance;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * When it is necessary, update the configuration structure with the mechanism like double buffers
     * 
     * @return true: updated
     */
    public boolean update()
    {
        // sample configuration
        String cfgFolder = System.getProperty("config", "./config/");
        String cfgFn = cfgFolder + File.separator + "exp.yaml";
        File cfgFile = new File(cfgFn);
        long time = cfgFile.lastModified();
        try {
            if (time != 0 && time != m_lastUpdateTime) { // the configuration file exists and has been modified
                boolean bNeedUpdate = false;
                synchronized (updateLock) {
                    if (time != 0 && time != m_lastUpdateTime) { // the configuration file exists and has been modified
                        m_lastUpdateTime = time; // If set m_lastUpdateTime after successfully reconfigure, a incorrect configuration will be loaded repeatedly
                        bNeedUpdate = true;
                    }
                }
                if (bNeedUpdate) {
                    // update the configuration structure with the mechanism like double buffers
                    // lock free to update the configuration
                    Experiment cfg = Yaml.loadType(cfgFile, Experiment.class);
                    cfg.postprocess();
                    m_experiment = cfg; // it is atomic
                    m_lastUpdateTime = time;
                    log.warn("updated " + cfgFile);
                    return true;
                }
            }
            if (0 == time) {
                log.error("the configuration file does not be found: " + cfgFile);
            } else {
                log.debug("the configuration file does not modified: " + cfgFile);
            }
        } catch (FileNotFoundException e) { // will not enter this block in normal case because of the anterior assertion of time
            log.error("init Exception: config file error: file not found" + cfgFile);
        } catch (Exception e) {
            log.error("init Exception: config file error: " + cfgFile);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Private constructor. Prevents instantiation from other classes.
     */
    protected ExpMgr()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the m_experiment
     */
    public Experiment getExperiment()
    {
        return m_experiment;
    }

    /**
     * Initialize this instance
     */
    protected void init()
    {
        boolean res = this.update();
        if (false == res) { // at the 1st time, it must be successful
            throw new RuntimeException("fail to init the configuration");
        }
    }

}
