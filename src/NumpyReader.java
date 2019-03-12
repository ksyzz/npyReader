import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ksyzz
 * @since <pre>2019/03/11</pre>
 */
public class NumpyReader {
    private static int MAGIC_LEN = 8;
    private static final Map<String, String> VERSION2HTYPE = new HashMap<String, String>(){
        {
            /**
             * short型
             */
            put("10", "<H");
            /**
             * int型
             */
            put("20", "<I");
        }
    };


    /**
     * 获取npy文件的前缀版本信息
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readVersion(InputStream inputStream) throws Exception {
        byte[] magicStr = new byte[MAGIC_LEN];
        inputStream.read(magicStr);
        return magicStr[MAGIC_LEN-2] + "" + magicStr[MAGIC_LEN-1];
    }

    /**
     * 获取npy文件头信息，包括数组纬度和数组每个元素的字节数
     * @param inputStream
     * @param version
     * @return
     * @throws Exception
     */
    public static String readArrayHeader(InputStream inputStream, String version) throws Exception {
        String headerLengthType = VERSION2HTYPE.get(version);
        if (headerLengthType == null) {
            throw new Exception("Invalid version " + version);
        }
        byte[] headerLengthInfo = new byte[headerLengthType.getBytes().length];
        inputStream.read(headerLengthInfo);
        int headerLength = getHeaderLength(headerLengthInfo, headerLengthType);
        byte[] headerBytes = new byte[headerLength];
        inputStream.read(headerBytes);
        String headers = new String(headerBytes);
        return headers;
    }

    /**
     * 获取npy文件头长度
     * @param headerLengthInfo
     * @param headerLengthType
     * @return
     * @throws Exception
     */
    public static int getHeaderLength(byte[] headerLengthInfo, String headerLengthType) throws Exception {
        int hlen = 0;
        if ("<H".equals(headerLengthType)) {
            // short存储文件头的长度
            hlen = 8;
        } else if ("<I".equals(headerLengthType)){
            // int 存储文件头长度
            hlen = 16;
        } else {
            throw new Exception("暂不支持的类型");
        }
        return bytes2Int(Arrays.copyOf(headerLengthInfo, hlen));
    }

    /**
     * 根据头信息获取元素的字节长度
     * @param header
     * @return
     */
    public static int itemLength(String header) throws Exception {
        //目前仅支持int和float类型
        String[] headerArray = header.split(",");
        String dataType = headerArray[0];
        if (dataType.contains("'<f")) {
            int index = dataType.indexOf("<f");
            return Integer.valueOf(dataType.substring(index+2, index+3));
        } else if (dataType.contains("'<i")) {
            int index = dataType.indexOf("<i");
            return Integer.valueOf(dataType.substring(index+2, index+3));
        }
        throw new Exception("暂不支持的类型");
    }

    /**
     * 获取数组纬度，例如 2,3  1,1,2
     * @param header
     * @return
     */
    public static String arrayShape(String header) {
        String[] headerArray = header.split(",", 3);
        String shape = headerArray[2];
        return shape.substring(shape.indexOf("(")+1, shape.indexOf(")")).replace(" ", "");
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static int bytes2Int(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16)
                | ((src[3] & 0xFF)<<24));
        return value;
    }

}
