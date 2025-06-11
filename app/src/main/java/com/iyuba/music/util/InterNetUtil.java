package com.iyuba.music.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class InterNetUtil {
	private static final String BRAND_HUAWEI = "华为";
	private static final String BRAND_XIAOMI = "小米";
	private static final String BRAND_MEIZU = "魅族";
	private static final String BRAND_VIVO = "Vivo";
	private static final String BRAND_OPPO = "Oppo";
	private static final String BRAND_SAMSUNG = "三星";
	private static final String BRAND_GIONEE = "金立";
	private static final String BRAND_360 = "360";
	private static final String BRAND_OTHER = "安卓";

	private static String brandName;

	public static String getBrandName() {
		if (TextUtils.isEmpty(brandName)) {
			brandName = setBrandName();
		}
		return brandName;
	}

	private static String setBrandName() {
		String brand = Build.BRAND.trim().toLowerCase();
		if (brand.contains("huawei") || brand.contains("honor")
				|| brand.contains("nova") || brand.contains("mate")) {
			return BRAND_HUAWEI;
		}
		if (brand.contains("xiaomi")) {
			return BRAND_XIAOMI;
		}
		if (brand.contains("vivo")) {
			return BRAND_VIVO;
		}
		if (brand.contains("oppo")) {
			return BRAND_OPPO;
		}
		if (brand.contains("samsung")) {
			return BRAND_SAMSUNG;
		}
		if (brand.contains("meizu")) {
			return BRAND_MEIZU;
		}
		// 金立
		if (brand.contains("gionee")) {
			return BRAND_GIONEE;
		}
		if (brand.contains("360") || brand.contains("qiku")
				|| brand.contains("qiho") || brand.contains("qihu")) {
			return BRAND_360;
		}
		return BRAND_OTHER;
	}
	//    爱语吧华为用户3群：705250027
//    群号：爱语吧华为用户3群(705250027) 的 key 为： X_XYfsL0_-ewHkXpIUhlwpbxvQcxEWLb
//
//    爱语吧VIVO用户2群：433075910
//    群号：爱语吧VIVO用户2群(433075910) 的 key 为： CFBROmhoDx_440-ukjYYugIf61SSujRC
//
//    爱语吧OPPO用户2群：334687859
//    群号：爱语吧OPPO用户2群(334687859) 的 key 为： Yuhyc18Q34Lmy0b6W1HeXuDG3TdferpX
//
//    爱语吧小米用户2群：499939472
//    群号：爱语吧小米用户2群(499939472) 的 key 为： 9UmuKvpLjV-ib9W-bDSgEok_KyvAZYQ-
//
//    爱语吧魅族用户2群：745011534
//    群号：爱语吧魅族用户2群(745011534) 的 key 为： zS82Y-4zaPVChkpun-HLOnNpKcf_h2_3
//
//    爱语吧360用户群：625355797
//    群号：爱语吧360用户群(625355797) 的 key 为： DO_M89dYSkdQpbO0pz0v4zlTOr50CN8P
//
//    爱语吧安卓用户2群：482516431
//    群号：爱语吧安卓用户2群(482516431) 的 key 为： DEbZdKF9fjFpsxAzdcEQ5rhzHz9WWCFW
	public static String getQQGroupKey(String brandName) {
		switch (brandName) {
			case BRAND_HUAWEI:
				return "X_XYfsL0_-ewHkXpIUhlwpbxvQcxEWLb";
			case BRAND_VIVO:
				return "CFBROmhoDx_440-ukjYYugIf61SSujRC";
			case BRAND_OPPO:
				return "Yuhyc18Q34Lmy0b6W1HeXuDG3TdferpX";
			case BRAND_XIAOMI:
				return "9UmuKvpLjV-ib9W-bDSgEok_KyvAZYQ-";
			case BRAND_SAMSUNG:
				return "4LU-47yf_P510zgmdp98miJtDx366Ty5";
			case BRAND_GIONEE:
				return "pb42xTKokAQVzo1buzX95skntd5UOLUQ";
			case BRAND_MEIZU:
				return "zS82Y-4zaPVChkpun-HLOnNpKcf_h2_3";
			case BRAND_360:
				return "0yHQOAWPGPOPacORm2BXdOblJZvlzeLw";
			default:
				return "DEbZdKF9fjFpsxAzdcEQ5rhzHz9WWCFW";
		}
	}

	public static String getQQGroupNumber(String brand) {
		switch (brand) {
			case BRAND_HUAWEI:
				return "705250027";
			case BRAND_VIVO:
				return "433075910";
			case BRAND_OPPO:
				return "334687859";
			case BRAND_XIAOMI:
				return "499939472";
			case BRAND_SAMSUNG:
				return "639727892";
			case BRAND_GIONEE:
				return "621392974";
			case BRAND_MEIZU:
				return "745011534";
			case BRAND_360:
				return "625355797";
			default:
				return "482516431";
		}
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}


	/**
	 * 判断WIFI网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}


	/**
	 * 判断MOBILE网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}


	/**
	 * 获取当前网络连接的类型信息
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}


	/**
	 * 获取当前的网络状态 ：没有网络0：WIFI网络1：3G网络2：2G网络3
	 *
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = 0;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;// wifi
		} else if (nType == ConnectivityManager.TYPE_MOBILE) {
			int nSubType = networkInfo.getSubtype();
			TelephonyManager mTelephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
					&& !mTelephony.isNetworkRoaming()) {
				netType = 2;// 3G
			} else {
				netType = 3;// 2G
			}
		}
		return netType;
	}
}
