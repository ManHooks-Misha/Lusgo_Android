package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.SyrianskaTaekwondo.hejtelge.GroupChatActivity;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class FileUploadService extends JobIntentService {
    private static final String TAG = "FileUploadService";
    Disposable mDisposable;
    private String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Ijc5NzhjMjI3LWViMGItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTYxYzMxYmIiLCJuYmYiOjE1MDYyODQ4NzMsImV4cCI6NDY2MTk1ODQ3MywiaWF0IjoxNTA2Mjg0ODczLCJpc3MiOiJCbGVuZCIsImF1ZCI6IkJsZW5kIn0.QUh241IB7g3axLcfmKR2899Kt1xrTInwT6BBszf6aP4";
    private HubConnection connection;
    int index = 0;
    private String encodePath = "";
    private final OkHttpClient client = new OkHttpClient();
    String mimeType = "";
    String mFilePath = "", userid, groupid, chatfor;
    File file = null;
    /**
     * Unique job ID for this service.
     */
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    private static final int JOB_ID = 102;

    public static void enqueueWork(Context context, Intent intent) {

        enqueueWork(context, FileUploadService.class, JOB_ID, intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Download/Upload of file
         * The system or framework is already holding a wake lock for us at this point
         */

        // get file file here
        mFilePath = intent.getStringExtra("mFilePath");
        userid = intent.getStringExtra("userid");
        groupid = intent.getStringExtra("groupid");
        chatfor = intent.getStringExtra("chat_for");
        mimeType = intent.getStringExtra("mimeType");
        if (mimeType.toLowerCase().equals("jpg")) {
            mimeType = "image/jpg";
        } else if (mimeType.toLowerCase().equals("png")) {
            mimeType = "image/png";
        } else if (mimeType.toLowerCase().equals("jpeg")) {
            mimeType = "image/jpeg";
        }
//        } else if (mimeType.toLowerCase().equals("mp3")) {
//            mimeType = "audio/mp3";
//        } else if (mimeType.toLowerCase().equals("mp4")) {
//            mimeType = "video/mp4";
//        }
        if (mFilePath == null) {
            Log.e(TAG, "onHandleWork: Invalid file URI");
            return;
        }
       /*connection = new WebSocketHubConnectionP2("https://mapzapp.swadhasoftwares.com/uploadHub/", authHeader);
        connection.addListener(this);
        connection.subscribeToEvent("FileMessage", this);
        connection.connect();*/
        RestApiService apiService = RetrofitInstance.getApiService();
        @SuppressLint("CheckResult") Flowable<Double> fileObservable = Flowable.create(emitter -> {

            //  run(eventID, mFilePath, mimeType, userID);
            apiService.onFileUpload(createRequestBodyFromText(userid), createRequestBodyFromText(groupid), createRequestBodyFromText(""), createRequestBodyFromText(chatfor), createMultipartBody(mFilePath, mimeType, emitter)).blockingGet();
            emitter.onComplete();
        }, BackpressureStrategy.LATEST);

        mDisposable = fileObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> onProgress(progress), throwable -> onErrors(throwable), () -> onSuccess());
    }

    private void onErrors(Throwable throwable) {
        sendBroadcastMeaasge("Error in file upload " + throwable.getMessage(), "Failed", "live");
        Log.e(TAG, "onErrors: ", throwable);
    }

    private void onProgress(Double progress) {

        Log.e(TAG, "onProgress: " + progress);
    }

    private void onSuccess() {
        GroupChatActivity.index_post++;
        sendBroadcastMeaasge("File uploading successful ", "Success", "live");

        Log.e(TAG, "onSuccess: File Uploaded");
        //    sendBroadcast(new Intent(INTENT_FILTER_FOR_UPDATE_EVENT_DAIRY));

//        File fileexist = new File(mFilePath);
//        if (fileexist.exists()) {
//            fileexist.delete();
//        }
        try {
            if (GroupChatActivity.images.size() > 0) {
                if (GroupChatActivity.index_post < GroupChatActivity.images.size()) {


                    Intent mIntent = new Intent(this, FileUploadService.class);
                    mIntent.putExtra("mFilePath", GroupChatActivity.images.get(GroupChatActivity.index_post).getAbsolutePath());
                    mIntent.putExtra("groupid", groupid);
                    mIntent.putExtra("userid", userid);
                    mIntent.putExtra("chat_for", chatfor);
                    mIntent.putExtra("mimeType", "jpg");
                    FileUploadService.enqueueWork(this, mIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcastMeaasge(String message, String flag, String type) {
        Intent localIntent = new Intent("my.own.broadcast");
        localIntent.putExtra("result", message);
        localIntent.putExtra("flag", flag);
        localIntent.putExtra("type", type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private RequestBody createRequestBodyFromFile(File file, String mimeType) {
        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    private RequestBody createRequestBodyFromText(String mText) {
        return RequestBody.create(MediaType.parse("text/plain"), mText);
    }

/*
    public void sendMessage(String eventId, String userId, String mimeType, String encodePath) {


        connection.invoke("UploadEventGallery", eventId, userId, mimeType, encodePath);

    }*/


    /*
     * return multi part body in format of FlowableEmitter
     *
     * @param filePath
     * @param emitter
     * @return
     */
    private MultipartBody.Part createMultipartBody(String filePath, String mime, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return MultipartBody.Part.createFormData("img", file.getName(), createCountingRequestBody(file, mime, emitter));
    }

    private RequestBody createCountingRequestBody(File file, String mimeType, FlowableEmitter<Double> emitter) {
        RequestBody requestBody = createRequestBodyFromFile(file, mimeType);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            double progress = (1.0 * bytesWritten) / contentLength;
            emitter.onNext(progress);
        });
    }


}
