/**
 * 
 */
package org.mornsun.info.api;

/**
 * Terminal relevant information
 * 
 * @author Chauncey
 * 
 */
public class InfoTerminal
{
    private String m_app; // The name of terminal application
    private String m_app_ver; // The version of terminal application
    private String m_os; // The name of terminal OS, such as IPhone, Android
    private String m_os_ver; // The version of terminal OS

    /**
     * 
     */
    public InfoTerminal()
    {
        init(null, null, null, null);
    }

    /**
     * @param m_app
     * @param m_app_ver
     * @param m_os
     * @param m_os_ver
     */
    public InfoTerminal(String app, String app_ver, String os, String os_ver)
    {
        init(app, app_ver, os, os_ver);
    }

    /**
     * 
     * @param app
     * @param app_ver
     * @param os
     * @param os_ver
     */
    public void init(String app, String app_ver, String os, String os_ver)
    {
        this.m_app = app;
        this.m_app_ver = app_ver;
        this.m_os = os;
        this.m_os_ver = os_ver;
    }

    /**
     * @return the app
     */
    public String getApp()
    {
        return m_app;
    }

    /**
     * @return the app_ver
     */
    public String getAppVer()
    {
        return m_app_ver;
    }

    /**
     * @return the os
     */
    public String getOs()
    {
        return m_os;
    }

    /**
     * @return the os_ver
     */
    public String getOsVer()
    {
        return m_os_ver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "InfoAdapter [m_app=" + m_app + ", m_app_ver=" + m_app_ver + ", m_os=" + m_os
                + ", m_os_ver=" + m_os_ver + "]";
    }

}
