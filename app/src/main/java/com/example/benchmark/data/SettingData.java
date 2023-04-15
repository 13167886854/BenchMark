package com.example.benchmark.data;

/**
 * ShuoMingData
 *
 * @version 1.0
 * @since 2023/4/15 12:59
 */
public class SettingData {
    private static final SettingData settingData = new SettingData();

    private int stabilityTestNum = 5;
    private String serverIp = "175.38.1.81";
    private String serverPort = "8080";

    private SettingData(){}

    /**
     * getInstance
     *
     * @return com.example.benchmark.data.SettingData
     * @date 2023/4/15 14:47
     */
    public static SettingData getInstance(){
        return settingData;
    }

    /**
     * getStabilityTestNum
     *
     * @return int
     * @date 2023/4/15 13:02
     */
    public int getStabilityTestNum() {
        return stabilityTestNum;
    }

    /**
     * setStabilityTestNum
     *
     * @param stabilityTestNum description
     * @return void
     * @date 2023/4/15 13:02
     */
    public void setStabilityTestNum(int stabilityTestNum) {
        this.stabilityTestNum = stabilityTestNum;
    }

    /**
     * getServerIp
     *
     * @return java.lang.String
     * @date 2023/4/15 13:02
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * setServerIp
     *
     * @param serverIp description
     * @return void
     * @date 2023/4/15 13:02
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * getServerPort
     *
     * @return java.lang.String
     * @date 2023/4/15 13:02
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * setServerPort
     *
     * @param serverPort description
     * @return void
     * @date 2023/4/15 13:03
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * getServerAddress
     *
     * @return java.lang.String
     * @date 2023/4/15 13:27
     */
    public String getServerAddress(){
        return  "http://"+this.serverIp+":"+this.serverPort;
    }
}
