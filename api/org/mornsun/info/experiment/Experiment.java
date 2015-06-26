package org.mornsun.info.experiment;

import org.mornsun.info.util.IPostprocessable;

/**
 * @author Chauncey
 *
 */
public class Experiment implements IPostprocessable
{
    // private static final Logger log = LoggerFactory.getLogger(ExpConfig.class);

    public ExpPoint points[];

    /**
     * Private constructor. Prevents instantiation from other classes.
     */
    public Experiment()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the points
     */
    public ExpPoint[] getPoints()
    {
        return points;
    }

    /**
	 * 
	 */
    public void postprocess() throws IllegalArgumentException
    {
        // prepare
        for (ExpPoint point : points) {
            point.postprocess();
        }
    }
}
