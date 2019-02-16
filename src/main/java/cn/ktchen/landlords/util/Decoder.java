package cn.ktchen.landlords.util;

import java.io.IOException;
import java.io.InputStream;

public class Decoder {
    public static String inputStream2Str(InputStream in) {
        return inputStream2Str(in, "utf-8");
    }
    public static String inputStream2Str(InputStream in, String encode)
    {
        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;
        try
        {
            while ((len = in.read(b)) != -1)
            {
                sb.append(new String(b, 0, len, encode));
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";

    }

}
