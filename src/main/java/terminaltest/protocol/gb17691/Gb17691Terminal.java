package terminaltest.protocol.gb17691;

public class Gb17691Terminal {
    private byte[] vin;
    private byte[] sim;
    private byte[] scid;
    private byte[] cvn;
    private byte[] iupr;
    private int jd;
    private int wd;
    private int zxslc;

    public Gb17691Terminal() {
    }

    public Gb17691Terminal(byte[] vin, byte[] sim, byte[] scid, byte[] cvn, byte[] iupr, int jd, int wd, int zxslc) {
        this.vin = vin;
        this.sim = sim;
        this.scid = scid;
        this.cvn = cvn;
        this.iupr = iupr;
        this.jd = jd;
        this.wd = wd;
        this.zxslc = zxslc;
    }

    public byte[] getScid() {
        return scid;
    }

    public void setScid(byte[] scid) {
        this.scid = scid;
    }

    public byte[] getCvn() {
        return cvn;
    }

    public void setCvn(byte[] cvn) {
        this.cvn = cvn;
    }

    public byte[] getIupr() {
        return iupr;
    }

    public void setIupr(byte[] iupr) {
        this.iupr = iupr;
    }

    public byte[] getSim() {
        return sim;
    }

    public void setSim(byte[] sim) {
        this.sim = sim;
    }

    public byte[] getVin() {
        return vin;
    }

    public void setVin(byte[] vin) {
        this.vin = vin;
    }

    public int getJd() {
        return jd;
    }

    public void setJd(int jd) {
        this.jd = jd;
    }

    public int getWd() {
        return wd;
    }

    public void setWd(int wd) {
        this.wd = wd;
    }

    public int getZxslc() {
        return zxslc;
    }

    public void setZxslc(int zxslc) {
        this.zxslc = zxslc;
    }
}
