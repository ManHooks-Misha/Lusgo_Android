package com.app.SyrianskaTaekwondo.hejtelge.utility;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by root on 6/16/17.
 */

public class RequestBuilder {

    //Login request body
    public static RequestBody LoginBody(String username, String password, String token) {
        return new FormBody.Builder()
                .add("action", "login")
                .add("format", "json")
                .add("username", username)
                .add("password", password)
                .add("logintoken", token)
                .build();
    }

    public static MultipartBody uploadRequestBody(String userid, String groupid, String chat_for, ArrayList<File> arr_file) {
        MultipartBody.Builder mRequestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        mRequestBody.addFormDataPart("userid", userid);
        mRequestBody.addFormDataPart("groupid", groupid);
        mRequestBody.addFormDataPart("msg", "");
        mRequestBody.addFormDataPart("chat_for", chat_for);
        if (arr_file.size() > 0) {
           // String content_type = getMimeType(arr_file.get(0).getPath());
            String content_type = "image/jpeg";
       //     if (content_type != null) {
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(0));
                //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
                mRequestBody.addFormDataPart("img", arr_file.get(0).getName(), file_body);
           // }
        }

        if (arr_file.size() > 1) {
           // String content_type = getMimeType(arr_file.get(1).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(1));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(1).getName(), file_body);
        }

        if (arr_file.size() > 2) {
           // String content_type = getMimeType(arr_file.get(2).getPath());
            String content_type ="image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(2));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(2).getName(), file_body);
        }

        if (arr_file.size() > 3) {
           // String content_type = getMimeType(arr_file.get(3).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(3));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(3).getName(), file_body);
        }
        if (arr_file.size() > 4) {
            //String content_type = getMimeType(arr_file.get(4).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(4));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(4).getName(), file_body);
        }
        if (arr_file.size() > 5) {
          //  String content_type = getMimeType(arr_file.get(5).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(5));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(5).getName(), file_body);
        }
        if (arr_file.size() > 6) {
         //   String content_type = getMimeType(arr_file.get(6).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(6));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(6).getName(), file_body);
        }
        if (arr_file.size() > 7) {
           // String content_type = getMimeType(arr_file.get(7).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(7));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(7).getName(), file_body);
        }
        if (arr_file.size() > 8) {
          //  String content_type = getMimeType(arr_file.get(8).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(8));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(8).getName(), file_body);
        }
        if (arr_file.size() > 9) {
         //   String content_type = getMimeType(arr_file.get(9).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(9));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(9).getName(), file_body);
        }
        if (arr_file.size() > 10) {
         //   String content_type = getMimeType(arr_file.get(10).getPath());
            String content_type = "image/jpeg";
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), arr_file.get(10));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("img", arr_file.get(10).getName(), file_body);
        }
        return mRequestBody.build();
    }


    public static MultipartBody uploadRequestBody1(String user_id, String team_id, String description, String access_key, String doc, String link, String is_public, ArrayList<File> arr_file) {
        MultipartBody.Builder mRequestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        mRequestBody.addFormDataPart("description", description);
        mRequestBody.addFormDataPart("access_key", access_key);
        mRequestBody.addFormDataPart("is_public", is_public);
        mRequestBody.addFormDataPart("doc", doc);
        mRequestBody.addFormDataPart("link", link);
        mRequestBody.addFormDataPart("user_id", user_id);
        mRequestBody.addFormDataPart("team_id", team_id);
        if (arr_file.size() > 0) {
            String content_type = getMimeType(arr_file.get(0).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(0));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(0).getName(), file_body);
        }

        if (arr_file.size() > 1) {
            String content_type = getMimeType(arr_file.get(1).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(1));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(1).getName(), file_body);
        }

        if (arr_file.size() > 2) {
            String content_type = getMimeType(arr_file.get(2).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(2));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(2).getName(), file_body);
        }

        if (arr_file.size() > 3) {
            String content_type = getMimeType(arr_file.get(3).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(3));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(3).getName(), file_body);
        }
        if (arr_file.size() > 4) {
            String content_type = getMimeType(arr_file.get(4).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(4));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(4).getName(), file_body);
        }
        if (arr_file.size() > 5) {
            String content_type = getMimeType(arr_file.get(5).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(5));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(5).getName(), file_body);
        }
        if (arr_file.size() > 6) {
            String content_type = getMimeType(arr_file.get(6).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(6));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(6).getName(), file_body);
        }
        if (arr_file.size() > 7) {
            String content_type = getMimeType(arr_file.get(7).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(7));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(7).getName(), file_body);
        }
        if (arr_file.size() > 8) {
            String content_type = getMimeType(arr_file.get(8).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(8));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(8).getName(), file_body);
        }
        if (arr_file.size() > 9) {
            String content_type = getMimeType(arr_file.get(9).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(9));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(9).getName(), file_body);
        }
        if (arr_file.size() > 10) {
            String content_type = getMimeType(arr_file.get(10).getPath());
            RequestBody file_body = RequestBody.create(MediaType.parse("image/jpeg"), arr_file.get(10));
            //       MEDIA_TYPE = arr_file.get(0).getAbsolutePath().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            mRequestBody.addFormDataPart("post_images", arr_file.get(10).getName(), file_body);
        }
        return mRequestBody.build();
    }

    private static String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

    }
}
