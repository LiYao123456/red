package com.android_red.intenter;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.os.AsyncTask;

//异步进行HTTP网络通信
/*
 * AsyncTask比Handler更轻量级一些，适用于简单的异步处理。
 * AsyncTask就是一个封装过的后台任务类，用于异步任务
 * AsyncTask定义了三种泛型类型 Params，Progress和Result。
Params 启动任务执行的输入参数，比如HTTP请求的URL。
Progress 后台任务执行的百分比。
Result 后台执行任务最终返回的结果，比如String。
 */
public class AsyncTaskSoap extends AsyncTask<String, Void, JSONObject> {
	
	public interface OnDataRecvListener {
		void onDataRecv(JSONObject jsonObj, int iCode);
	}

	private OnDataRecvListener oLsner = null;
	private int iRequestCode = 0;

	// login list add preAdd
	// method + "?" + param = url
	public void startAsyncTask(String method, String param,
			OnDataRecvListener lsner, int iCode) {
		oLsner = lsner;
		iRequestCode = iCode;
		// ...代表可变参数：可以有多个参数
		execute(method, param);
	}

	public void cancelAsyncTask(boolean b) {
		oLsner = null;
		super.cancel(b);
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject json = null;
		String method = params[0];
		String param = params[1];
		// 延迟加载
		try {
		   Thread.sleep(0);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);// 连接时间
		HttpPost request = new HttpPost(Const.WEB_PATH + method + "?" + param);

		try {
			// 发送请求，得到响应
			HttpResponse response = client.execute(request);

			// 请求成功
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String receive = EntityUtils.toString(response.getEntity());
				json = new JSONObject(receive);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	private Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (oLsner != null) {
			oLsner.onDataRecv(result, iRequestCode);
			oLsner = null;
		}
	}

}
