package com.iyuba.music.dubbing.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/2 0002.
 */

public class CloseUtil {
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
