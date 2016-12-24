package safe.girl.just.girl;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;

import java.util.ArrayList;
import java.util.Arrays;

import safe.girl.just.person.BaiduConstant;
import safe.girl.just.person.SystemConstant;


/**
 * Created by 许灡珊 on 2016/9/10.
 * 百度语音
 */
public class BaiduVoice implements RecognitionListener {

    private SpeechRecognizer speechRecognizer ;     //语音识别
    private Context context ;
    public BaiduVoice(Context context){
        this.context = context ;
        saying();
    }
    /**语音识别*/
    private void saying(){
       // AlertDialog.Builder  dialog =
         //       new AlertDialog.Builder(context).setTitle("准备开始说话") ;
//        setDialog(dialog) ;
//        dialog.show() ;
        startARS() ;

    }

    /**百度语音插入*/


    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(BaiduConstant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_ERROR,
                    R.raw.bdspeech_recognition_error);
            intent.putExtra(BaiduConstant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);

        }



    }

    public void startARS() {
        print("点击了“开始”");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        {
            String args = sp.getString("args", "");
            if (null != args) {
                print("参数集：" + args);
                intent.putExtra("args--------", args) ;
            }
        }
        boolean api = sp.getBoolean("api", false);
//        if (api) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, new ComponentName(context, VoiceRecognitionService.class));
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

    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        print("检测到用户的已经开始说话");
        Toast.makeText(context,"检测到用户的已经开始说话",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(context,"检测到用户的已经停止说话",Toast.LENGTH_SHORT).show();
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
            new AlertDialog.Builder(context).
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

   /* *//**dialog框体处理，调用相应的方法*//*
    private void setDialog(AlertDialog.Builder dialog){

        final View view = context.getLayoutInflater().inflate(R.layout.voivce_image,null) ;
        dialog.setView(view) ;

    }*/
    private void decideFunction(String info){
        if(info.equals("[抢劫]") || info.equals("[张杰]") || info.equals("[强奸]")){
            HardHelp.light();
        }else if(info.equals("[救命]") || info.equals("[啊]") || info.equals("[啊啊]")){
            //发送紧急求救广播
            Intent intent = new Intent(SystemConstant.EMERGENCY_ID) ;
            context.sendBroadcast(intent) ;
        }
    }
}
