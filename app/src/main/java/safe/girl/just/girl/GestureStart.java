package safe.girl.just.girl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;


import com.baidu.speech.VoiceRecognitionService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import safe.girl.just.person.BaiduConstant;
import safe.girl.just.person.SystemConstant;

/**
 * Created by 许灡珊 on 2016/4/14.
 */
public class GestureStart extends Activity implements RecognitionListener {
    GestureOverlayView drawGesture ;
    private AppFileSet appFileSet ;       //文件设置
    private String gestureName ;
    private SpeechRecognizer speechRecognizer ;     //语音识别
    private static final int REQUEST_UI = 1;
    private static final int VOICE = 1 ;
    private static final int VOICE_SOUND = 2 ;
    private int i = 1 ;
    MediaPlayer mPlayer ;
    boolean onSound=true,onCall=true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_gesture) ;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        appFileSet = new AppFileSet(this) ;
        drawGesture = (GestureOverlayView)findViewById(R.id.drawGesture) ;
        drawGesture.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
                Log.i("手势： " ,"手势开始") ;
            }

            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {
                Log.i("手势： " ,"手势") ;

            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
                Log.i("手势： " ,"手势结束") ;

                File file = new File( Environment.getExternalStorageDirectory(), "gestures") ;
                if(file.exists()) {
                    GestureLibrary gestureLibrary =
                            GestureLibraries.fromFile(
                                    file);


                    if(gestureLibrary.load()){
                        //加载手势
                        ArrayList<Prediction> predictions = gestureLibrary.recognize(drawGesture.getGesture()) ;         //识别手势
                        ArrayList<String> result = new ArrayList<String>() ;
                        //遍历找到的所有手势
//                            result.add(predictions.get(0).name + "---" + (predictions.get(0).score));
                            for (Prediction p : predictions) {
                                //只有相似度大于2.0才输出
                                result.add(p.name + "---" + (p.score));
                                if (p.score > 2.0) {
                                    keepGesture(p.name);
                                }
                            }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GestureStart.this,android.R.layout.simple_list_item_1,result) ;
                       // new AlertDialog.Builder(GestureStart.this).setAdapter(adapter,null).setPositiveButton("确定",null).show() ;
                    }else{
                        Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
                Log.i("手势： " ,"手势取消") ;

            }
        }) ;
    }

    /**   将手势图片，名字和相关的紧急事件连接在一起*/
    public void keepGesture(String name){
        if(name.equals("报警")){
            if(onCall) {
                mPlayer = MediaPlayer.create(GestureStart.this, R.raw.alarm_bell);
                mPlayer.start();
                onCall = false ;
            }else{
                mPlayer.stop();
                onCall = true;
            }

        }else if(name.equals("语音识别")) {
            final View soundPic = getLayoutInflater().inflate(R.layout.voivce_image,null) ;
            Log.i("百度语音","开始") ;
            new AlertDialog.Builder(GestureStart.this).setMessage("现在您可以开始说话了!").setView(soundPic)
                    .setNegativeButton("我还是决定取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show()
            ;
//            new BaiduVoice(GestureStart.this) ;
            saying() ;
            Log.i("百度语音","结束") ;

        }else if(name.equals("模拟声音")){
            Random rand = new Random() ;
            int i = rand.nextInt(10) ;
            if(onSound) {
                if(i <= 2) {
                    mPlayer = MediaPlayer.create(GestureStart.this, R.raw.one);
                    mPlayer.start();
                    onSound = false;
                }else if(i>=3&&i<=6){
                    mPlayer = MediaPlayer.create(GestureStart.this, R.raw.two);
                    mPlayer.start();
                    onSound = false;
                }else if(i>=7&&i<=10){
                    mPlayer = MediaPlayer.create(GestureStart.this, R.raw.thre);
                    mPlayer.start();
                    onSound = false;
                }
            }else{
                mPlayer.stop();
                onSound = true;
            }

        }else if(name.equals("紧急求救")){
            new Emergency() ;
            sendBroadcast(new Intent(SystemConstant.EMERGENCY_ID));
        }else if(name.equals("闪光灯")){
                //直接开启
            HardHelp.light();
        }
    }

    /**百度语音插入*/
    /**语音识别*/
    private void saying(){
        startARS() ;

    }
    /**百度语音插入*/
    @Override
    protected void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(BaiduConstant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
            File file = new File(new AppFileSet(this).getAppFile() +"/voice.pcm") ;
            if(!file.exists()){
                try {
                    file.createNewFile();
                }catch (Exception e){}
            }
            intent.putExtra("outfile",file) ;
        }
        if (sp.contains(BaiduConstant.EXTRA_INFILE)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(BaiduConstant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(BaiduConstant.EXTRA_OUTFILE, false)) {
            intent.putExtra(BaiduConstant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(BaiduConstant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(BaiduConstant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(BaiduConstant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BaiduConstant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(BaiduConstant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BaiduConstant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(BaiduConstant.EXTRA_NLU)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BaiduConstant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(BaiduConstant.EXTRA_VAD)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BaiduConstant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(BaiduConstant.EXTRA_PROP)) {
            String tmp = sp.getString(BaiduConstant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BaiduConstant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(BaiduConstant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(BaiduConstant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(BaiduConstant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            // intent.putExtra(BaiduConstant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }

    }

    public void startARS() {
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        {
            String args = sp.getString("args", "");
            if (null != args) {
                print("参数集：" + args);
                intent.putExtra("args--------", args) ;
            }
        }
        boolean api = sp.getBoolean("api", false);
//        if (api) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(intent);
//        } else {
           /* intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            print("设置识别");
            startActivityForResult(intent, REQUEST_UI);*/
    }
    @Override
    public void onReadyForSpeech(Bundle params) {
        print("准备就绪，可以开始说话");
        AlertDialog.Builder  dialog = new AlertDialog.Builder(GestureStart.this).setTitle("准备开始说话") ;
        setDialog(dialog) ;
        dialog.show() ;
    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        print("检测到用户的已经开始说话");
        Toast.makeText(this,"检测到用户的已经开始说话",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        print("检测到用户的已经停止说话");
        Toast.makeText(this,"检测到用户的已经停止说话",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int error) {
        time = 0;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
    }

    @Override
    public void onResults(Bundle results) {

        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        if (nbest.size() > 0) {
            print("识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            new AlertDialog.Builder(GestureStart.this).
                    setMessage("识别结果:\n" +Arrays.toString(nbest.toArray(new String[0]))).
                    setTitle("语音识别结果显示").
                    show() ;
        }
        String re = Arrays.toString(nbest.toArray(new String[0])) ;
        decideFunction(re) ;
       /* Message message = new Message() ;
        message.what = VOICE_SOUND ;
        message.copyFrom(message);
        handle.sendMessage(message) ;*/

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
//        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
        }
    }

    long time;
    private void print(String msg) {
    }

    /**dialog框体处理，调用相应的方法*/
    private void setDialog(AlertDialog.Builder dialog){

        final View view = getLayoutInflater().inflate(R.layout.voivce_image,null) ;

        /*Message message = new Message() ;
        message.copyFrom(message) ;
        message.what = VOICE ;
        handle.sendMessage(message) ;*/
        dialog.setView(view) ;

    }
    private void decideFunction(String info){
        if(info.equals("[抢劫]") || info.equals("[张杰]") || info.equals("[强奸]")
                || info.equals("[张杰啊]") || info.equals("[抢劫啊]") || info.equals("[强奸啊]")
                ){
            MediaPlayer play =  mPlayer = MediaPlayer.create(GestureStart.this, R.raw.alarm_bell);
            mPlayer.start();
        }else if(info.equals("[救命]") || info.equals("[啊]") || info.equals("[啊啊]") || info.equals("[啊啊啊]")
                || info.equals("[救命啊]")
                ){
            new Emergency() ;
        }
    }

    /** 处理消息*/
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final View view = getLayoutInflater().inflate(R.layout.voivce_image,null) ;
            final ImageView imageView = (ImageView) view.findViewById(R.id.voice_image) ;
            if(msg.what == VOICE){
                /*if(i > 3){
                    i = 1 ;
                }
                imageView.setImageLevel(i) ;*/
            }else if(msg.what == VOICE_SOUND){
                String info = msg.getData().getString("info") ;
                decideFunction(info) ;
            }
        }
    } ;
    @Override
    public void onBackPressed() {
        Log.i("销毁进程","返回按键设置为false") ;
        super.onBackPressed();
        this.finish();
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
        this.startActivity(new Intent(this,ChooseActivity.class));
        System.exit(0);
        Log.i("手势界面","backpress") ;

    }

    @Override
    protected void onPause() {
        super.onPause();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
}


