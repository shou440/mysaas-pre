package com.xd.pre.common.utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonFun {

    //获取指定年月最大的天数
    //
    public static int getDaysByYearMonth(int year, int month) {

       Calendar a = Calendar.getInstance();
       a.set(Calendar.YEAR, year);
       a.set(Calendar.MONTH, month - 1);
       a.set(Calendar.DATE, 1);
       a.roll(Calendar.DATE, -1);
       int maxDate = a.get(Calendar.DATE);
       return maxDate;
    }

    //获取当前时间的字符串
    public static String getTimeStr(Date day)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String sDate = df.format(day);
        return sDate;
    }


    public static int GetTick()
    {
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        long nTick = (long)stamp.getTime()/1000;
        return (int)nTick;
    }

    public static long GetMsTick()
    {
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        long nTick = (long)stamp.getTime();
        return nTick;
    }

    //将从1970年来的秒数转换成时间戳
    public static Timestamp TickToTime(int nTick)
    {
        long lTick = nTick;
        lTick *= 1000;
        Timestamp tm = new Timestamp(lTick);

        return tm;
    }



    public static int GetGap(int nCompareTick)
    {
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        long nTick = (long)stamp.getTime()/1000;
        long lCompare = nCompareTick;

        long nDelt = 0;
        if (nTick > nCompareTick)
        {
            nDelt = nTick - lCompare;
        }
        else
        {
            nDelt = lCompare - nTick;
        }
        return (int)nDelt;
    }

    //ascII字节转Byte
    private static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    //ASCII转BCD
    private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }



    //字节转字符串
    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

}
