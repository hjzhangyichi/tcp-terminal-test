package terminaltest.protocol.gb17691;

import java.util.Arrays;

/**
 * @author zhangyichi
 */
public class Gb17691Message {
    private byte cmd;
    private byte[] vin;
    private byte softwareVersion;
    private byte encryptType;
    private byte[] data;


    public Gb17691Message(byte cmd , byte[] vin , byte softwareVersion , byte encryptType , byte[] data) {
        this.cmd = cmd;
        this.vin = vin;
        this.softwareVersion = softwareVersion;
        this.encryptType = encryptType;
        this.data = data;
    }




    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte[] getVin() {
        return vin;
    }

    public void setVin(byte[] vin) {
        this.vin = vin;
    }

    public byte getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(byte softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public byte getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(byte encryptType) {
        this.encryptType = encryptType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public byte cks(){
        byte cks = 0;
        cks ^= cmd;
        for (byte b : vin) {
            cks ^= b;
        }
        cks ^= softwareVersion;
        cks ^= encryptType;
        byte h = (byte)((data.length & 0xFF00) >> 8);
        byte l = (byte)(data.length & 0xFF);
        cks ^= h;
        cks ^= l;
        for (byte b : data) {
           cks ^= b;
        }
        return cks;
    }
    public String toHex(){
        StringBuilder hex = new StringBuilder("2323");
        String cmdStr = Integer.toHexString(cmd & 0xFF);
        hex.append(cmdStr.length() < 2 ? "0" + cmdStr : cmdStr);
        for (byte b : vin) {

            String vinItemStr = Integer.toHexString(b & 0xFF);

            hex.append(vinItemStr.length() < 2 ? "0" + vinItemStr : vinItemStr);
        }
        String softwareVerStr = Integer.toHexString(softwareVersion & 0xFF);
        hex.append(softwareVerStr.length() < 2 ? "0" + softwareVerStr : softwareVerStr);
        String encryptStr = Integer.toHexString(encryptType & 0xFF);
        hex.append(encryptStr.length() < 2 ? "0" + encryptStr : encryptType);
        String lenHStr = Integer.toHexString(((getData().length & 0xFF00) >> 8));
        lenHStr = lenHStr.length() < 2 ? "0" + lenHStr : lenHStr;

        String lenLStr = Integer.toHexString((getData().length & 0xFF));
        lenLStr = lenLStr.length() < 2 ? "0" + lenLStr : lenLStr;
        hex.append(lenHStr);
        hex.append(lenLStr);
        for (byte datum : getData()) {
            String byteStr = Integer.toHexString(datum & 0xFF);
            byteStr = byteStr.length() < 2 ? "0" + byteStr : byteStr;
            hex.append(byteStr);
        }
        String cksStr = Integer.toHexString(cks()&0xFF);
        cksStr = cksStr.length() < 2 ? "0" + cksStr : cksStr;
        hex.append(cksStr);
        return hex.toString();
    }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "cmd=" + cmd +
                ", vin=" + Arrays.toString(vin) +
                ", softwareVersion=" + softwareVersion +
                ", encryptType=" + encryptType +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
