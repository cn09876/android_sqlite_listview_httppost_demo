package com.sww.ldpicex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Appl extends Application
{
	public final float version=(float) 6.01;
	private static final String TAG = Appl.class.getSimpleName();

	@Override
	public void onCreate() 
	{
		super.onCreate();
	}

	public void l(String s)
	{
		Log.e(TAG,s);
	}

	public String sendHttpPost(String url,List<BasicNameValuePair> lst) 
	{
		try {
			HttpPost request = new HttpPost(url); // 创建Http请求
			request.setEntity(new UrlEncodedFormEntity(lst, "GBK")); // 设置参数的编码
			HttpResponse httpResponse = new DefaultHttpClient().execute(request); // 发送请求并获取反馈
			// 解析返回的内容
			if (httpResponse.getStatusLine().getStatusCode() != 404) 
			{
				String result = EntityUtils.toString(httpResponse.getEntity());
				l(result);
				return result;
			}
		}
		catch (Exception e) 
		{
			l("http.post.error= "+e.getMessage());
			return "";
		}
		return "";
	}

}
