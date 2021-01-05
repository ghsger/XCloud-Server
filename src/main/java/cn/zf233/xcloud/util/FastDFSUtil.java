package cn.zf233.xcloud.util;

import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.IOException;

/**
 * Created by zf233 on 2020/11/27
 */
public class FastDFSUtil {

    private static TrackerServer ts = null;
    private static StorageServer ss = null;

    static {
        try {
            ClientGlobal.init("fastdfs.conf");
            TrackerClient tc = new TrackerClient();
            ts = tc.getConnection();
            ss = tc.getStoreStorage(ts);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }

    public static String[] upload(byte[] buffFile, String fileExtName) throws IOException, MyException {
        StorageClient sc = getStorageClient();
        String[] result;
        result = sc.upload_file(buffFile, fileExtName, null);
        return result;
    }

    public static byte[] download(String groupName, String remoteFilename) throws IOException, MyException {
        StorageClient sc = getStorageClient();
        return sc.download_file(groupName, remoteFilename);
    }

    public static void delete(String groupName, String remoteFilename) throws IOException, MyException {
        StorageClient sc = getStorageClient();
        sc.delete_file(groupName, remoteFilename);
    }

    public static StorageClient getStorageClient() {
        return new StorageClient(ts, ss);
    }
}

