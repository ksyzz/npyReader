import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author ksyzz
 * @since <pre>2019/03/12</pre>
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        String path = "./x.npy";
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(path));
        int[][] arr = readIntArray(dataInputStream);

        System.out.println();
    }

    /**
     * 读取整数型数组
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static int[][] readIntArray(InputStream inputStream) throws Exception {
        String version = NumpyReader.readVersion(inputStream);
        String header = NumpyReader.readArrayHeader(inputStream, version);
        int itemLength = NumpyReader.itemLength(header);
        String shape = NumpyReader.arrayShape(header);
        int row = Integer.valueOf(shape.split(",")[0]);
        int col = Integer.valueOf(shape.split(",")[1]);
        int[][] array = new int[row][col];
        byte[] item = new byte[itemLength];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                inputStream.read(item);
                array[i][j] = NumpyReader.bytes2Int(item);
            }
        }
        return array;
    }

    /**
     * 读取double数组
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static double[][] readDoubleArray(InputStream inputStream) throws Exception {
        String version = NumpyReader.readVersion(inputStream);
        String header = NumpyReader.readArrayHeader(inputStream, version);
        int itemLength = NumpyReader.itemLength(header);
        String shape = NumpyReader.arrayShape(header);
        int row = Integer.valueOf(shape.split(",")[0]);
        int col = Integer.valueOf(shape.split(",")[1]);
        double[][] array = new double[row][col];
        byte[] item = new byte[itemLength];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                inputStream.read(item);
                array[i][j] = NumpyReader.bytes2Double(item);
            }
        }
        return array;
    }
}
