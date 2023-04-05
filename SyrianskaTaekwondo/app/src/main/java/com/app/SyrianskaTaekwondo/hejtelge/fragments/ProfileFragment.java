/*
package com.app.hejtelge.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.app.hejtelge.R;
import com.app.hejtelge.pojo.ProfilePojo;
import com.app.hejtelge.utility.CommonMethods;
import com.app.hejtelge.utility.CompressFile;
import com.app.hejtelge.utility.ConsURL;
import com.app.hejtelge.databinding.ActivityEditProfileBinding;
import com.yalantis.ucrop.UCrop;

//import org.codepond.wizardroid.WizardStep;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;

public class ProfileFragment extends WizardStep {

    private ActivityEditProfileBinding binding;
    private CommonMethods cmn;
    private String userid, email, name, mob;
    private int flag = 0;
    private long mLastClickTime = 0;
    private String role;

    private String imagepath;
    private String banner;
    private ArrayList<HashMap<String, String>> arr_team = new ArrayList<>();

    private String status;
    private String id;
    private String msg;
    private String profileBase64 = "";
    private String backgroundBase64 = "";
    private String city, add, firstname, lastname, state, telephone, usertype, is_loggedIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEditProfileBinding.inflate(inflater, container, false);
        cmn = new CommonMethods();

        binding.toolbar.setVisibility(View.VISIBLE);
        binding.back.setOnClickListener(view -> getActivity().finish());
        userid = cmn.getPrefsData(getActivity(), "id", "");
        is_loggedIn = cmn.getPrefsData(getActivity(), "is_loggedIn", "");
        userid = cmn.getPrefsData(getActivity(), "id", "");
        role = cmn.getPrefsData(getActivity(), "role", "");
        String fname = cmn.getPrefsData(getActivity(), "firstname", "");
        String username = cmn.getPrefsData(getActivity(), "username", "");
        String lname = cmn.getPrefsData(getActivity(), "lastname", "");
        email = cmn.getPrefsData(getActivity(), "email", "");
        mob = cmn.getPrefsData(getActivity(), "telephone", "");
        imagepath = cmn.getPrefsData(getActivity(), "imagepath", "");
        banner = cmn.getPrefsData(getActivity(), "banner", "");
        binding.ffLogin.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imagepath)
                .fitCenter()
                .placeholder(R.drawable.user_profile)
                .into(binding.ppImg);
        Glide.with(this)
                .load(banner)
                .centerCrop()
                .placeholder(R.drawable.banner)
                .into(binding.pp);
        binding.txtName.setText(fname + " " + lname);
        if (mob != null && !mob.equals("null")) {
            binding.txtTel.setText(mob);
        }
        binding.txtRole.setText(role);
        binding.txtUsername.setText(username);
        binding.txtEml.setText(email);
        binding.login.setText(getResources().getString(R.string.action_next));


        binding.ppImg.setOnClickListener(view -> {
            flag = 1;
            ImagePicker.create(this).theme(R.style.AppTheme_No).single().showCamera(true).toolbarArrowColor(Color.BLACK).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();


        });
        binding.pp.setOnClickListener(view -> {
            flag = 2;
            ImagePicker.create(this).theme(R.style.AppTheme_No).single().showCamera(true).toolbarArrowColor(Color.BLACK).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Beskära").start();

        });
        binding.ffLogin.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            name = Objects.requireNonNull(binding.txtName.getText()).toString();
            email = Objects.requireNonNull(binding.txtEml.getText()).toString();
            mob = Objects.requireNonNull(binding.txtTel.getText()).toString();
            if (name.trim().length() > 0) {
                Profile(name);
            } else {
                Toast.makeText(getActivity(), "Vänligen ange namn", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();


    }

    private void Profile(String first_name) {


        ProgressDialog mprogdialog = ProgressDialog.show(getActivity(), "", "Vänta", true);
        mprogdialog.setCancelable(false);

        if (imagepath.length() > 0) {
            File file = new File(imagepath);

            int length1 = (int) file.length();
//            txt_doc.setText(file.getName());
//            ff_doc.setVisibility(View.VISIBLE);

            byte[] bytes = new byte[length1];
            try {
                //fileInputStream.read(b);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    fileInputStream.read(bytes);
                }
                profileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                // upload.setText(namefile);
            } catch (Exception ignored) {

            }
        }
        if (banner.length() > 0) {
            File file1 = new File(banner);

            int length = (int) file1.length();
//            txt_doc.setText(file.getName());
//            ff_doc.setVisibility(View.VISIBLE);

            byte[] bytes1 = new byte[length];
            try {
                //fileInputStream.read(b);
                try (FileInputStream fileInputStream = new FileInputStream(file1)) {
                    fileInputStream.read(bytes1);
                }
                backgroundBase64 = Base64.encodeToString(bytes1, Base64.DEFAULT);

                // upload.setText(namefile);
            } catch (Exception e) {
                Log.e("", Objects.requireNonNull(e.getMessage()));
            }
        }
        Gson gson = new Gson();
        ProfilePojo asgn = new ProfilePojo();
        asgn.email = email;
        asgn.access_key = ConsURL.accessKey;
        asgn.first_name = first_name;
        asgn.last_name = "";
        asgn.profile_image = profileBase64;
        asgn.banner = backgroundBase64;
        asgn.phone = mob;
        asgn.city = "";
        asgn.state = "";
        asgn.address = "";
        asgn.user_id = userid;


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "update_profile";

        Call call = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
                mprogdialog.dismiss();


            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res;
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();


                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");

                        msg = objvalue.optString("message");
                        JSONObject object = objvalue.getJSONObject("data");

                        if (status.equals("true")) {

                            id = object.getString("id");
                            email = object.getString("email");
                            firstname = object.getString("firstname");
                            imagepath = object.getString("profile_image");
                            lastname = object.getString("lastname");
                            state = object.getString("state");
                            city = object.getString("city");
                            add = object.getString("address");
                            telephone = object.getString("telephone");
                            usertype = object.getString("usertype");
                            banner = object.getString("banner_image");
                            role = object.getString("role");


                        }

                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            if (usertype.equals("3")) {
                                cmn.setPrefsData(getActivity(), "id", id);
                                cmn.setPrefsData(getActivity(), "city", city);
                                cmn.setPrefsData(getActivity(), "email", email);
                                cmn.setPrefsData(getActivity(), "firstname", firstname);
                                cmn.setPrefsData(getActivity(), "imagepath", imagepath);
                                cmn.setPrefsData(getActivity(), "lastname", lastname);
                                cmn.setPrefsData(getActivity(), "state", state);
                                cmn.setPrefsData(getActivity(), "city", city);
                                cmn.setPrefsData(getActivity(), "telephone", telephone);
                                cmn.setPrefsData(getActivity(), "usertype", usertype);
                                cmn.setPrefsData(getActivity(), "role", role);
                                cmn.setPrefsData(getActivity(), "banner", banner);

                                    Common.INSTANCE.saveString("is_profile", "true");
//                                    cmn.setPrefsData(getActivity(), "is_profile", "true");
                                    showToast("Profilen uppdaterades framgångsrikt");
                                    notifyCompleted();
                                    binding.ffLogin.setVisibility(View.GONE);
                                    //   startActivity(new Intent(getActivity(), ResetPassword.class).putExtra("flat_reset_firsttime", "1"));
                                    // finish();

                            } else {
                                cmn.setPrefsData(getActivity(), "id", id);
                                cmn.setPrefsData(getActivity(), "city", city);
                                cmn.setPrefsData(getActivity(), "email", email);
                                cmn.setPrefsData(getActivity(), "firstname", firstname);
                                cmn.setPrefsData(getActivity(), "imagepath", imagepath);
                                cmn.setPrefsData(getActivity(), "lastname", lastname);
                                cmn.setPrefsData(getActivity(), "state", state);
                                cmn.setPrefsData(getActivity(), "city", city);
                                cmn.setPrefsData(getActivity(), "telephone", telephone);
                                cmn.setPrefsData(getActivity(), "usertype", usertype);
                                cmn.setPrefsData(getActivity(), "role", role);
                                cmn.setPrefsData(getActivity(), "banner", banner);

                                Common.INSTANCE.saveString("is_profile", "true");
                                showToast("Profilen uppdaterades framgångsrikt");

                                notifyCompleted();

                                //startActivity(new Intent(getActivity(), ResetPassword.class));

                            }

                        } else {
                            mprogdialog.dismiss();
                            notifyIncomplete();
                            cmn.showAlert(msg, getActivity());
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            if (images.size() > 0) {
                UCrop.Options option = new UCrop.Options();
               option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(getActivity(), R.color.white));
                Uri destinationUri = Uri.fromFile(new File(Objects.requireNonNull(getActivity().getExternalFilesDir(null)).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(Uri.fromFile(new File(images.get(0).getPath())), destinationUri).withAspectRatio(1f,1f).withOptions(option).start(Objects.requireNonNull(getContext()), this);
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            String imagepath1 = resultUri.getPath();


            CompressFile compressFile = new CompressFile();
            assert imagepath1 != null;
            File path = compressFile.getCompressedImageFile(new File(imagepath1), getActivity());
            //Glide.with(EditProfile.this).load(path).into(profile);


            if (flag == 1) {
                imagepath = path.getPath();

                Glide.with(Objects.requireNonNull(getActivity())).load(imagepath).into(binding.ppImg);
                // imagepath = resultUri.getPath();
                flag = 0;

            }
            if (flag == 2) {
                banner = path.getPath();

                Glide.with(Objects.requireNonNull(getActivity())).load(banner).centerCrop().into(binding.pp)
                ;
                flag = 0;

            }


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            assert cropError != null;
            cropError.printStackTrace();
            //  getActivity().showErrorToast(cropError.getMessage());
        }
        super.onActivityResult(requestCode, resultCode, data);


    }


*/
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      *//*

*/
/*  if (item.getItemId() == android.R.id.home) {
            *//*
*/
/*
*//*

*/
/*if (isChangeProfile) {
                showErrorToast("Du har ändrat din bild men sparade inte din profil! Klicka på knappen \"Uppdatera profil\" för att fortsätta.");
            } else {*//*
*/
/*
*//*

*/
/*
                getActivity().finish();
          //  }
            return true;
        }*//*
*/
/*

        if (item.getItemId()==R.id.save){
            name = Objects.requireNonNull(binding.txtName.getText()).toString();
            email = Objects.requireNonNull(binding.txtEml.getText()).toString();
            mob = Objects.requireNonNull(binding.txtTel.getText()).toString();

            if (name.trim().length() > 0) {
                Profile(name);
            } else {
                Toast.makeText(getActivity(), "Vänligen ange namn", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.profile, menu);

        return true;
    }
*//*


}
*/
