package terminaltest.unit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author zhangyichi
 */
public class MessageTimeUnit {
    public static long bytes2Long(byte[] data){
        int year = (data[0] & 0xFF), month = (data[1] & 0xFF), day = (data[2] & 0xFF),
                hour = (data[3] & 0xFF), min = (data[4] & 0xFF), sec = (data[5] & 0xFF);
        return LocalDateTime.of(year + 2000,month,day,hour,min,sec).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static byte[] nowTime2Byte(){
        LocalDateTime time = LocalDateTime.now();
        byte[] bytetime = new byte[6];
        bytetime[0] = (byte) (time.getYear()-2000);
        bytetime[1] = (byte) time.getMonthValue();
        bytetime[2] = (byte) time.getDayOfMonth();
        bytetime[3] = (byte) time.getHour();
        bytetime[4] = (byte) time.getMinute();
        bytetime[5] = (byte) time.getSecond();
        return bytetime;
    }

    public static byte[] nowTime2BCDByte(){
        LocalDateTime time = LocalDateTime.now();
        byte[] bytetime = new byte[6];
        bytetime[0] = int2bcd(time.getYear()%1000);
        bytetime[1] = int2bcd(time.getMonthValue());
        bytetime[2] = int2bcd(time.getDayOfMonth());
        bytetime[3] = int2bcd(time.getHour());
        bytetime[4] = int2bcd(time.getMinute());
        bytetime[5] = int2bcd(time.getSecond());
        return bytetime;
    }

    public static byte int2bcd(int i){
        int high = i/10;
        int low = i%10;
        return (byte) ((high<<8)|low);
    }
}
