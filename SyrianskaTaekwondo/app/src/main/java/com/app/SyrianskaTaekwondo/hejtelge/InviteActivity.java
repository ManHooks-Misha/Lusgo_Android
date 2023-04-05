package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityInviteBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Invite;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.gson.Gson;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import vk.help.MasterActivity;

import static okhttp3.OkHttpClient.Builder;

public class InviteActivity extends MasterActivity {
    String[] arr = {"Välj roll", "Sub-admin", "Teamleader"};
    private ArrayList<HashMap<String, String>> contacts = new ArrayList<>();
    private ArrayList<String> body = new ArrayList<>();
    List<String> arr_phone = new ArrayList<>();
    private String team_id = "", team_name, role = "";
    private ArrayList<ContactResult> results = new ArrayList<>();
    private static final int CONTACT_PICKER_REQUEST = 991;
    private boolean isInvite = false;
    List<String> arr_email = new ArrayList<>();
    private String status, msg, userid, password, usertype, phone, email_txt, usernametxt;
    private CommonMethods cmn = new CommonMethods();
    private String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private String NUMBER = "0123456789";
    private AlertDialog alertDialog;
    private String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private SecureRandom random = new SecureRandom();
    private String[] permissionsRequired = new String[]{Manifest.permission.READ_CONTACTS};
    private int PERMISSION_CALLBACK_CONSTANT = 100;
    ActivityInviteBinding binding;

    @SuppressLint({"Clickab    private static final int REQ_PICK_CONTACT = 2 ;\nleViewAccessibility", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        binding = ActivityInviteBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setContentView(binding.getRoot());
        userid = cmn.getPrefsData(InviteActivity.this, "id", "");
        usertype = cmn.getPrefsData(InviteActivity.this, "usertype", "");
        usernametxt = cmn.getPrefsData(InviteActivity.this, "firstname", "");
        isInvite = true;

        //getSupportActionBar().setTitle("Skicka inbjudan");
        if (getIntent() != null) {
            team_id = getIntent().getStringExtra("Teamid");
            team_name = getIntent().getStringExtra("name");
        }

        if (usertype.equals("2")) {
            role = "0";

            binding.spinRole.setVisibility(View.VISIBLE);

            ArrayAdapter adapter = new ArrayAdapter(InviteActivity.this, R.layout.support_simple_spinner_dropdown_item, arr);
            binding.spinRole.setAdapter(adapter);
        } else {
            binding.spinRole.setVisibility(View.GONE);
        }

        /*binding.email.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.email) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });*/
        binding.spinRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    role = "5";

                } else if (i == 2) {
                    role = "3";

                } else {
                    role = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.ffinvite.setOnClickListener(view -> {
            arr_phone.clear();
            arr_email.clear();
            email_txt = Objects.requireNonNull(binding.email.getText()).toString().replace(" ", "");
            arr_email.add(email_txt);
            phone = Objects.requireNonNull(binding.mob.getText()).toString();
            arr_phone.add(phone);
            if (email_txt.length() > 0 && Objects.requireNonNull(phone.length() > 0)) {
                Toast.makeText(this, "Välj ett alternativ antingen e-post eller nummer", Toast.LENGTH_SHORT).show();
            } else if (email_txt.length() > 0) {

                if (usertype.equals("3")) {
                    if (cmn.isEmailValid(email_txt)) {
                        if (isInvite) {
                            if (cmn.isOnline(context)) {
                                role = "4";
                                getExitingUserAPI(team_id, email_txt, "1");
                            } else {
                                showToast(getResources().getString(R.string.internet));
                            }
                        } else {
                            showToast("Du har redan skickat en inbjudan");
                        }
                    } else {
                        showToast("Ange rätt e-postadress");

                    }
                } else if (usertype.equals("2")) {

                    if (role.equals("5") || role.equals("3")) {
                        if (isInvite) {
                            if (cmn.isOnline(context)) {
                                if (cmn.isEmailValid(email_txt)) {
                                    getExitingUserAPI(team_id, email_txt, "1");
                                } else {
                                    showToast("Ange rätt e-postadress");
                                }
                            } else {
                                showToast(getResources().getString(R.string.internet));
                            }
                        } else {
                            showToast("Du har redan skickat en inbjudan");
                        }
                    } else {
                        cmn.showAlert("Välj roll", context);

                    }
                    /* else {
                     */
                } else if (isInvite) {
                    if (usertype.equals(ConsURL.sub_admin)) {
                        role = "3";
                        if (cmn.isOnline(context)) {
                            if (cmn.isEmailValid(email_txt.trim())) {
                                getExitingUserAPI(team_id, email_txt, "1");
                            } else {
                                showToast("Ange rätt e-postadress");
                            }
                        } else {
                            showToast(getResources().getString(R.string.internet));
                        }
                    }

                }
            } else if (Objects.requireNonNull(binding.mob.getText()).length() > 0) {
                if (isInvite) {
                    if (usertype.equals(ConsURL.admin)) {
                        if (role.equals("5") || role.equals("3")) {
                            if (cmn.isValidMobile(binding.mob.getText().toString())) {
                                getExitingUserAPI(team_id, phone, "2");
                            } else {
                                //showToast("Ange giltigt mobilnummer");
                                cmn.showAlert("Ange giltigt mobilnummer",context);
                            }
                        } else {
                            cmn.showAlert("Välj roll", context);

                            // showToast("Välj roll");

                        }
                    } else if (usertype.equals(ConsURL.sub_admin)) {
                        role = "3";
                        if (isInvite) {
                            if (cmn.isOnline(context)) {
                                if (cmn.isValidMobile(binding.mob.getText().toString())) {
                                    getExitingUserAPI(team_id, phone, "2");
                                } else {
                                    cmn.showAlert("Ange giltigt mobilnummer",context);

                                }
                            } else {
                                showToast(getResources().getString(R.string.internet));
                            }
                        } else {
                            showToast("Du har redan skickat en inbjudan");
                        }


                    } else {
                        role = "4";
                        if (cmn.isValidMobile(binding.mob.getText().toString())) {
                            getExitingUserAPI(team_id, phone, "2");
                        } else {
                            cmn.showAlert("Ange giltigt mobilnummer",context);

                        }
                    }

                } else {
                    showToast("Du har redan skickat en inbjudan");

                }


            } else {
                cmn.showAlert("Välj roll", context);
            }

        });



        binding.contactlist.setOnClickListener(view ->

        {

            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    results.clear();
                    new MultiContactPicker.Builder(InviteActivity.this) //Activity/fragment context
                            .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                            .hideScrollbar(false) //Optional - default: false
                            .showTrack(true) //Optional - default: true
                            .searchIconColor(Color.BLACK) //Optional - default: White
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                            .handleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleTextColor(Color.WHITE) //Optional - default: White
                            .setTitleText("Välj kontakt") //Optional - only use if required
                            .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                            .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out) //Optional - default: No animation overrides
                            .showPickerForResult(CONTACT_PICKER_REQUEST);
                } else {
                    requestPermission();
                }
            } else {
                results.clear();
                new MultiContactPicker.Builder(InviteActivity.this) //Activity/fragment context
                        .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.BLACK) //Optional - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Välj kontakt") //Optional - only use if required
                        .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            }

        });

    }


    /* public String random() {
         Random generator = new Random();
         StringBuilder randomStringBuilder = new StringBuilder();
         int randomLength = generator.nextInt(6);
         char tempChar;
         for (int i = 0; i < randomLength; i++){
             tempChar = (char) (generator.nextInt(96) + 32);
             randomStringBuilder.append(tempChar);
         }
         return randomStringBuilder.toString();
     }*/
    public String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);

        }

        return sb.toString();

    }

    public boolean checkPermission() {

        int ContactPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);

        return
                ContactPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {

        int PERMISSION_REQUEST_CODE = 1;
        ActivityCompat.requestPermissions(InviteActivity.this, new String[]
                {
                        Manifest.permission.READ_CONTACTS,
                }, PERMISSION_REQUEST_CODE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* String emailtxt = Objects.requireNonNull(binding.email.getText().toString());
        if (role.length() == 0) {
            role = "4";
        }
        if (emailtxt.length()>0) {
            contacts.clear();
            InviteRequest(userid, team_id, role);
        }*/
    }


    private void InviteRequest(String userid, String teamid, String role, String flag) {
        String tset;

        ProgressDialog mprogdialog = ProgressDialog.show(InviteActivity.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        //  email_txt = Objects.requireNonNull(binding.email.getText()).toString();
        if (email_txt.length() > 0) {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.email = arr_email;
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.team_id = teamid;
            asgn.role = role;
            tset = gson.toJson(asgn);

        } else {
            Gson gson = new Gson();
            Invite asgn = new Invite();
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.phone = arr_phone;
            asgn.team_id = teamid;
            asgn.role = role;
            tset = gson.toJson(asgn);
        }


        String url = ConsURL.BASE_URL_TEST + "invite_users";

        Call call = new Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(InviteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

                mprogdialog.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    if (response.body() != null) {
                        String res = Objects.requireNonNull(response.body()).string();


                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");

                        msg = objvalue.optString("message");
                        //  JSONObject object = objvalue.getJSONObject("data");


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {
                            //  showToast("Inbjudan har skickats");
                            //  isInvite = false;
                            if (flag.equals("1")) {
                                showCustomDialog("Inbjudan har skickats. Om du valt att skicka inbjudan via e-post, be användaren titta i skräpposten.");
                            } else {
                                showCustomDialog("Inbjudan har skickats.");

                            }
                        } else {
                            cmn.showAlert(msg, InviteActivity.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }




    private void Alertshow(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            finish();

        });
        alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_PERMISSION_SETTING = 101;
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) ==
                    PackageManager.PERMISSION_GRANTED) {
                results.clear();
                new MultiContactPicker.Builder(InviteActivity.this) //Activity/fragment context
                        .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.BLACK) //Optional - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Välj kontakt") //Optional - only use if required
                        .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);

            }
        }

        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results.clear();
                results.addAll(MultiContactPicker.obtainResult(data));
                email_txt = Objects.requireNonNull(binding.email.getText()).toString();
                phone = Objects.requireNonNull(binding.mob.getText()).toString();

                if (email_txt.length() > 0 && phone.length() > 0) {
                    Toast.makeText(this, "Ange antingen e-post eller nummer", Toast.LENGTH_SHORT).show();
                } else {
                    if (results.size() > 0) {
                        body.clear();
                        for (int i = 0; i < results.size(); i++) {
                            //  password = generateRandomString(6);
                            List<PhoneNumber> num = results.get(i).getPhoneNumbers();
                            phone = num.get(0).getNumber();
                            //   arr_phone.add(phone);
//                            InviteRequest( userid,  team_id, role);

                           /* HashMap map = new HashMap();
                            map.put("username", phone);
                            map.put("password", password);
                            contacts.add(map);*/
       /*                     if (!team_id.equals("0")) {
                                String message = "Hej,\n\n" +
                                        "har nu lagt till dig i Lusgo\n" +
                                        "Du går till Lusgo och loggar in med följande uppgifter ";
                                String mess = "för att börja använda verktyget och ta del av information:\n" +
                                        "\nMobil: " + phone + "\n" +
                                        "Lösenord: " + password + "\n" + " Lagnamn: " + team_name;
                                body.add(message);
                                body.add(mess);

                                binding.mob.setText(phone);
                            } else {

                                String message = "Hej,\n\n" +
                                        "har nu lagt till dig i Lusgo\n" +
                                        "Du går till Lusgo och loggar in med följande uppgifter ";
                                String mess = "för att börja använda verktyget och ta del av information:\n" +
                                        "\nMobil: " + phone + "\n" +
                                        "Lösenord: " + password;
                                body.add(message);
                                body.add(mess);
*/

                            binding.mob.setText(phone);
                        }


                    }
                    //   InviteRequest("", userid, contacts);


                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            System.out.println("User closed the picker without selecting items.");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {

            //check if all permissions are granted
            boolean allgranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                results.clear();
                new MultiContactPicker.Builder(InviteActivity.this) //Activity/fragment context
                        .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                        .hideScrollbar(false) //Optional - default: false
                        .showTrack(true) //Optional - default: true
                        .searchIconColor(Color.BLACK) //Optional - default: White
                        .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                        .handleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleColor(ContextCompat.getColor(InviteActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                        .bubbleTextColor(Color.WHITE) //Optional - default: White
                        .setTitleText("Välj kontakt") //Optional - only use if required
                        .setSelectedContacts(results) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                        .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                        .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                        .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out) //Optional - default: No animation overrides
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(InviteActivity.this,
                        permissionsRequired[0])) {
                    //Show Information about why you need the permission and convince the user
                    AlertDialog.Builder builder = new AlertDialog.Builder(InviteActivity.this);
                    builder.setTitle("Need Read Contact Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Ja", (dialog, which) -> {
                        dialog.cancel();

                        ActivityCompat.requestPermissions(InviteActivity.this,
                                permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    });
                    builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast
                            .LENGTH_LONG).show();
                }
            }
        }
    }

/*

    private void proceedAfterPermission(ArrayList<String> SMS_BODY, String phone) {

        String DIALOG_MESSAGE = "Send sms to confirm your phone";
        SmsHandler.builder(this, phone)
                //  .withCarrierNameFilter("MCI")

                .withCustomDialogForSendSms(R.layout.my_sms_dialog)
                .withCustomDialogForChoseSim(R.layout.simcard_choosing_dialog)
                .needToShowDialog(false)
                .build().sendSms(DIALOG_MESSAGE, SMS_BODY, new MySmsManager.SMSManagerCallBack() {
            @Override
            public void afterSuccessfulSMS(int smsId) {
                Log.i("sms", "afterSuccessfulSMS ");
                //  Toast.makeText(InviteActivity.this, "afterSuccessfulSMS", Toast.LENGTH_SHORT).show();

                InviteRequest(userid, team_id, role);
            }

            @Override
            public void afterDelivered(int smsId) {
                Log.i("sms", "afterDelivered ");
                //      Toast.makeText(InviteActivity.this, "afterDelivered", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterUnSuccessfulSMS(int smsId, String message) {
                Log.i("sms", "afterUnSuccessfulSMS: " + message);
                //     Toast.makeText(InviteActivity.this, "afterUnSuccessfulSMS", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCarrierNameNotMatch(int smsId, String message) {
                Log.i("sms", "onCarrierNameNotMatch: " + message);
                //  Toast.makeText(InviteActivity.this, "onCarrierNameNotMatch", Toast.LENGTH_SHORT).show();

            }
        });


    }
*/


    private void getExitingUserAPI(String teamid, String username, String flag) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("username", username);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(InviteActivity.this, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject obj = new JSONObject(result.getData());
                    String login_count = obj.getString("login_count");
                    String invite = obj.getString("exists");

                    if (flag.equals("1")) {
                        if (login_count.equals("0")) {
                            switch (invite) {
                                case "0":
                                    InviteRequest(userid, team_id, role, flag);
                                    break;
                                case "1":
                                    Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ", flag);
                                    break;
                                case "2":
                                    Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ", flag);
                                    break;
                                default:
                                    cmn.showAlert("Du kan inte skicka inbjudan på detta nummer / e-post eftersom du har överskridit gränsen", context);
                                    break;
                            }
                        } else {
                            if (usertype.equals(ConsURL.coach)) {
                                Alert("Denna användare har redan ett konto. Inbjudan kommer att skickas som en notis i applikationen.", flag);
                            } else {
                                cmn.showAlert("Den här användaren finns redan",context);
                                showToast("Den här användaren finns redan");
                            }
                           // showToast("Den här användaren finns redan");
                        }

                    } else {
                        if (login_count.equals("0")) {
                            switch (invite) {
                                case "0":
                                    InviteRequest(userid, team_id, role, flag);
                                    break;
                                case "1":
                                    Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ", flag);
                                    break;
                                case "2":
                                    Alert("Du har redan skickat inbjudan till detta nummer / e-post. Vill du skicka det igen? ", flag);
                                    break;
                                default:
                                    cmn.showAlert("Du kan inte skicka inbjudan på detta nummer / e-post eftersom du har överskridit gränsen", context);
                                    break;
                            }


                        } else {
                            if (usertype.equals(ConsURL.coach)) {
                                Alert("Denna användare är redan registrerad i applikationen. Vill du fortfarande skicka inbjudan?", flag);
                            } else {
                                cmn.showAlert("Den här användaren finns redan",context);
                               // showToast("Den här användaren finns redan");
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "checkUser_Team");
    }




    public static int isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //gets the current TelephonyManager

        int simStateMain = 0;
        try {
            if (tm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    simStateMain = tm.getSimState(0);
                    if (simStateMain != 5) {
                        simStateMain = tm.getSimState(1);
                    }
                } else {
                    simStateMain = tm.getSimState();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            simStateMain = 0;
        }
        return simStateMain;
    }

    private void Alert(String msg, String flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", (dialogInterface, i) -> {
            InviteRequest(userid, team_id, role, flag);


        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }


    private void showCustomDialog(String msg) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_alert, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView create = dialogView.findViewById(R.id.create);
        create.setText(msg);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            finish();

        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}