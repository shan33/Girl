package safe.girl.just.person;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server_help {
	public Server_help(){
		
	}
	//��¼
	public boolean Login(String name,String pass){
		HttpUtil utils=new HttpUtil();
		utils.PATH="http://192.168.155.1:8080/G_hero/LoginAction";
		HttpUtil.setUrl(utils.PATH);
		Map<String,String> params=new HashMap<String,String>();								
	    params.put("username", name);
	    params.put("password", pass); 
	    String result=utils.sendPostMessage(params,"utf-8",utils.url);
	    if(result.equals("login success!")) {
			Log.i("网络----", "登录成功");
			return false;
		} else {
			Log.i("网络----","登录失败") ;
			return true;
		}
	}
	public boolean gps(String number,double lon,double lan,String address){
		HttpUtil utils=new HttpUtil();
		utils.PATH = "http://192.168.155.1:8080/G_hero/GPS";
		HttpUtil.setUrl(utils.PATH);
		Map<String, String> params = new HashMap<String, String>();
		params.put("userphon", number);
		params.put("latitude", lan + "");
		params.put("longitude", lon + "");
		params.put("location", address);
		String result=utils.sendPostMessage(params,"utf-8",utils.url);
		if(result.equals("login success!")) {
			Log.i("定位----", "更新定位成功");
			return false;
		} else {
			Log.i("定位----","更新定位失败") ;
			return true;
		}
	}

	//ע��
	public boolean Register(String name,String pass,String phon){
		HttpUtil utils=new HttpUtil();
		utils.PATH="http://192.168.155.1:8080/G_hero/RegisterAction";
		HttpUtil.setUrl(utils.PATH);
		Map<String,String> params=new HashMap<String,String>();
	    params.put ("username", name);
	    params.put("password", pass);
	    params.put("userphon", phon);
	    String result=utils.sendPostMessage(params,"utf-8",utils.url);
	    if(result.equals("register success!")) {
			Log.i("网络----", "注册成功");
			return true;
		}
	    else {
			Log.i("网络----", "注册失败");
			return false;
		}
	}
	//����GPS���յ������Ϣ
	public List<Map<String,Object>> Updategps(String name,double latitude,double longitude){
		HttpUtil utils=new HttpUtil();
		utils.PATH="http://192.168.155.1:8080/G_hero/GPS";
		HttpUtil.setUrl(utils.PATH);
		Map<String,String> params=new HashMap<String,String>();
		params.put("username", name);
		params.put("latitude",String.valueOf(latitude));
		params.put("longitude", String.valueOf(longitude));
	    String result=utils.sendPostMessage(params,"utf-8",utils.url);
	    List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	    list=JsonTest.listKeyMaps("needers",result);
	    return list;
	   /* Userclass user[]=new Userclass[1];
	    int i=0;
	  //  user[0].setUsername("qqq");
	    for (Map<String, Object> m : list){
	    	user[i].setUsername((String)m.get("username"));
	    	user[i].setUserphon((String)m.get("userphon"));
	    	user[i].setLatitude((double)m.get("latitude"));
	    	user[i].setLongitude((double)m.get("longitude"));
	    //	user[i].setDistance((double)m.get("distance"));
	    	i++;
	    }
	    return user;*/
	    }

	public boolean help(String latitude,String longitude,String location,String userphon){
		HttpUtil utils=new HttpUtil();
		Log.i("求救","收到求救") ;
		utils.PATH="http://192.168.155.1:8080/G_hero/Call";
		HttpUtil.setUrl(utils.PATH);
		Map<String,String> params=new HashMap<String,String>();
		params.put("latitude", latitude+"");
		params.put("longitude", longitude+"");
		params.put("location",location);
		params.put("userphon",userphon);

		String result=utils.sendPostMessage(params,"utf-8",utils.url);
		Log.i("求救",result+"") ;
		if(result.equals("success"))
			return true;
		else
			return false;
	}

	public List<Map<String,Object>> seek(String latitude,String longitude){
		HttpUtil utils=new HttpUtil();
		utils.PATH="http://192.168.155.1:8080/G_hero/Help";
		HttpUtil.setUrl(utils.PATH);
		List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
		Map<String,String> params=new HashMap<String,String>();
		params.put("latitude",latitude);
		params.put("longitude",longitude);
		String result=utils.sendPostMessage(params,"utf-8",utils.url);
		list=JsonTest.listKeyMaps("needers", result);
		Log.i("获取求救信息----","从服务器获取信息成功，人数：=" +list.size()) ;
		return list;

	}

	//获取周围人
	public List<Map<String,Object>> searchround(String latitude,String longitude){
		HttpUtil utils=new HttpUtil();
		utils.PATH= "http://192.168.155.1:8080/G_hero/Search" ;
		HttpUtil.setUrl(utils.PATH);
		List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
		Map<String,String> params=new HashMap<String,String>();
		params.put("latitude",latitude);
		params.put("longitude",longitude);
		String result=utils.sendPostMessage(params,"utf-8",utils.url);
		list=JsonTest.listKeyMaps("near",result);
		return list;
	}

	   
	

}
