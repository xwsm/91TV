/*
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.owen.player.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class EPLibraryUtil {
    
    public static abstract class InitLibraryCallback {
        public abstract void onInitStart();
        public abstract void onInitFinish(boolean isSucceed);
    }
    
    private static final String[] LIBS_NAMES = {"libijkffmpeg.so", "libijksdl.so", "libijkplayer.so"};
    private static final String[] LIBS_OLD_NAMES = {
            "libvvo.7.so", "libvvo.8.so", "libffmpeg.so", "libOMX.9.so", 
            "libOMX.11.so", "libOMX.14.so", "libOMX.18.so", "libvao.0.so",
            "libvvo.0.so", "libvvo.9.so", "libvvo.j.so", "libvplayer.so",
            "libvscanner.so", "libstlport_shared.so", ".lock"
    };
    private static final String ABI_ARM64_V8A = "arm64-v8a";
    private static final String LIBS_LOCK = ".libslock";
    private static String vitamioLibraryPath;

    public static void initialize(final Context ctx, final InitLibraryCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if(null != callback)
                    callback.onInitStart();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
//                if(!isInitialized(ctx)) {
//                    if (Build.VERSION.SDK_INT >= 21) {
//                        String abis = Arrays.toString(Build.SUPPORTED_ABIS);
//                        if(!TextUtils.isEmpty(abis) && abis.contains(ABI_ARM64_V8A)) {
//                            return extractLibs(ctx, R.raw.arm64_v8a);
//                        }
//                    }
//                    return extractLibs(ctx, R.raw.armeabi_v7a);
//                }
                return true;
            }

            @Override
            protected void onPostExecute(final Boolean aBoolean) {
                if(aBoolean) {
                    IjkMediaPlayer.loadLibrariesOnce(new IjkLibLoader() {
                        @Override
                        public void loadLibrary(String libName) {
                            boolean b = false;
                            try {
                                System.loadLibrary("lexue"+libName);
//                                libName = getLibraryPath() + "lib" + libName + ".so";
//                                EPLog.i("loadLibrary...libName = " + libName + " , exists = " + new File(libName).exists());
//                                System.load(libName);
                                b = true;
                            }
                            catch (Throwable e) {
                                e.printStackTrace();
                                b = false;
                            }
                            finally {
                                if(null != callback && !b) {
                                    callback.onInitFinish(b);
                                }
                            }
                        }
                    });
                }
                if(null != callback)
                    callback.onInitFinish(aBoolean);
            }
        }.execute();
    }

    private static boolean isInitialized(Context ctx) {

        if (Build.VERSION.SDK_INT >= 21) {
            EPLog.i("isInitialized, SUPPORTED ABIS " + Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            EPLog.i("isInitialized, SUPPORTED ABIS " + Build.CPU_ABI + " " + Build.CPU_ABI2);
        }
//        EPLog.i("isInitialized, CPU Feature [ %s ]", CPU.getFeatureString());

//        vitamioLibraryPath = EPUtils.getDataDir(ctx) + "lib/";
        vitamioLibraryPath = EPUtils.getDataDir(ctx, "lib") + File.separator;
        EPLog.i("isInitialized , vitamioLibraryPath = "+vitamioLibraryPath);
        File dir = new File(getLibraryPath());
        if (dir.exists() && dir.isDirectory()) {
            EPUtils.chmodPath("777", getLibraryPath());
            String[] libs = dir.list();
            if (libs != null) {
                Arrays.sort(libs);
                EPLog.d("isInitialized, Native libs " + Arrays.toString(libs));
                for (String L : getRequiredLibs()) {
                    if (Arrays.binarySearch(libs, L) < 0) {
                        EPLog.d("isInitialized, Native libs %s not exists!", L);
                        return false;
                    }
                }
                File lock = new File(getLibraryPath() + LIBS_LOCK);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader(new FileReader(lock));
                    int appVersion = EPUtils.getVersionCode(ctx);
                    int libVersion = Integer.valueOf(buffer.readLine());
                    EPLog.i("isInitialized, APP VERSION: %d, Native Library version: %d", appVersion, libVersion);
                    if (libVersion == appVersion)
                        return true;
                } catch (IOException e) {
                    EPLog.e("isInitialized", e);
                } finally {
                    EPUtils.closeSilently(buffer);
                }
            }
        }
        return false;
    }

    public static final String getLibraryPath() {
        return vitamioLibraryPath;
    }

    private static final List<String> getRequiredLibs() {
        List<String> libs = Arrays.asList(LIBS_NAMES);
        libs = new ArrayList<>(libs);
        libs.add(LIBS_LOCK);
        EPLog.i("getRequiredLibs... " + libs.toString());
        return libs;
    }

    private static boolean extractLibs(Context ctx, int rawID) {
        long begin = System.currentTimeMillis();
        final int version = EPUtils.getVersionCode(ctx);
        EPLog.d("extractLibs, loadLibs start " + version);
        File lock = new File(getLibraryPath() + LIBS_LOCK);
        if (lock.exists())
            lock.delete();
        String libZipPath = copyCompressedLib(ctx, rawID, "playerlibs.zip");
        EPLog.d("extractLibs, copyCompressedLib time : " + (System.currentTimeMillis() - begin) / 1000.0);
        boolean inited = initializeLibs(libZipPath, getLibraryPath());
        new File(libZipPath).delete();
        FileWriter fw = null;
        try {
            lock.createNewFile();
            fw = new FileWriter(lock);
            fw.write(String.valueOf(version));
            return true;
        } catch (IOException e) {
            EPLog.e("Error creating lock file", e);
        } finally {
            EPLog.d("extractLibs, initializeNativeLibs result : " + inited);
            EPLog.d("extractLibs, initializeNativeLibs time : " + (System.currentTimeMillis() - begin) / 1000.0);
            EPUtils.closeSilently(fw);
        }
        return false;
    }

    private static String copyCompressedLib(Context ctx, int rawID, String destName) {
        byte[] buffer = new byte[1024];
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        String destPath = null;

        try {
            try {
                String destDir = getLibraryPath();
                destPath = destDir + destName;
                File f = new File(destDir);
                if (f.exists() && !f.isDirectory())
                    f.delete();
                if (!f.exists())
                    f.mkdirs();
                f = new File(destPath);
                if (f.exists() && !f.isFile())
                    f.delete();
                if (!f.exists())
                    f.createNewFile();
            } catch (Exception fe) {
                EPLog.e("loadLib", fe);
            }

            is = ctx.getResources().openRawResource(rawID);
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(destPath);
            while (bis.read(buffer) != -1) {
                fos.write(buffer);
            }
        } catch (Exception e) {
            EPLog.e("loadLib", e);
            return null;
        } finally {
            EPUtils.closeSilently(fos);
            EPUtils.closeSilently(bis);
            EPUtils.closeSilently(is);
        }

        return destPath;
    }

    private static boolean initializeLibs(String libZipPath, String destDir) {
        File file;
        for(String l : LIBS_NAMES) {
            file = new File(getLibraryPath() + l);
            file.delete();
        }
        for(String l : LIBS_OLD_NAMES) {
            file = new File(getLibraryPath() + l);
            file.delete();
        }
        List list = EPZipUtil.unZipFolder(libZipPath, destDir);
        return null != list && !list.isEmpty();
    }
}
