package terminaltest.protocol.gb17691;

import com.alibaba.excel.annotation.ExcelProperty;

public class Gb17691ExcelRow {
    @ExcelProperty(value = "VIN", index = 0)
    private String vin;
    @ExcelProperty(value = "SIM卡号", index = 1)
    private String sim;
    @ExcelProperty(value = "软件标定识别号", index = 2)
    private String scid;
    @ExcelProperty(value = "CVN", index = 3)
    private String cvn;
    @ExcelProperty(value = "IUPR", index = 4)
    private String iupr;
    @ExcelProperty(value = "经度", index = 5)
    private String jd;
    @ExcelProperty(value = "纬度", index = 6)
    private String wd;
    @ExcelProperty(value = "总里程", index = 7)
    private String zxslc;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getScid() {
        return scid;
    }

    public void setScid(String scid) {
        this.scid = scid;
    }

    public String getCvn() {
        return cvn;
    }

    public void setCvn(String cvn) {
        this.cvn = cvn;
    }

    public String getIupr() {
        return iupr;
    }

    public void setIupr(String iupr) {
        this.iupr = iupr;
    }

    public String getJd() {
        return jd;
    }

    public void setJd(String jd) {
        this.jd = jd;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public String getZxslc() {
        return zxslc;
    }

    public void setZxslc(String zxslc) {
        this.zxslc = zxslc;
    }
}
