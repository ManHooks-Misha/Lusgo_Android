package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityOtpVerifyBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import vk.help.Common;
import vk.help.MasterActivity;

public class OtpVerify extends MasterActivity {
    ActivityOtpVerifyBinding binding;
    private String email, otp;
    private AlertDialog alertDialog;
    private CommonMethods cmn = new CommonMethods();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verifiera koden");
        binding.one.addTextChangedListener(new GenericTextWatcher(binding.one));
        binding.close.setVisibility(View.GONE);
        binding.two.addTextChangedListener(new GenericTextWatcher(binding.two));
        binding.three.addTextChangedListener(new GenericTextWatcher(binding.three));
        binding.four.addTextChangedListener(new GenericTextWatcher(binding.four));
        binding.verify.setOnClickListener(view -> {
            String one = Objects.requireNonNull(binding.one.getText()).toString();
            String two = Objects.requireNonNull(binding.two.getText()).toString();
            String three = Objects.requireNonNull(binding.three.getText()).toString();
            String four = Objects.requireNonNull(binding.four.getText()).toString();
            otp = one + two + three + four;
            getforgotAPI(otp);
        });
        binding.resend.setOnClickListener(view -> getforgotAPIOtp(email));
    }

    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.one:
                    if (text.length() == 1)
                        binding.two.requestFocus();
                    break;
                case R.id.two:
                    if (text.length() == 1)
                        binding.three.requestFocus();
                    else if (text.length() == 0)
                        binding.one.requestFocus();
                    break;
                case R.id.three:
                    if (text.length() == 1)
                        binding.four.requestFocus();
                    else if (text.length() == 0)
                        binding.two.requestFocus();
                    break;
                case R.id.four:
                    if (text.length() == 0)
                        binding.three.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }


    private void getforgotAPIOtp(String email) {
        //arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("email", email);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(OtpVerify.this, result -> {
            try {
                if (result.isStatus()) {

                    // alertDialog.dismiss();
                    //    startActivity(new Intent(OtpVerify.this, OtpVerify.class).putExtra("email", email));

                } else {
                    Common.INSTANCE.showToast(OtpVerify.this, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "forgot_request");
    }

    private void getforgotAPI(String otp) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("username", email);
            object.put("otp", otp);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog();
                    //    startActivity(new Intent(OtpVerify.this,OtpVerify.class));

                } else {
                    cmn.showAlert("Ogiltig kod",this);
                  //  Common.INSTANCE.showToast(OtpVerify.this, "Ogiltig kod");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "verify_otp");
    }

    private void changepass(String newpass) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("username", email);
            object.put("new_password", newpass);
            object.put("status", "1");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    startActivity(new Intent(OtpVerify.this, LoginActivity.class));
                    finish();
                } else {
                    Common.INSTANCE.showToast(OtpVerify.this, result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_password");
    }

    private void showCustomDialog() {
         AppCompatImageView eyeshow, eyeshow1, eyehide, eyehide1;

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.new_password, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatButton ok = dialogView.findViewById(R.id.resetPassword);
        AppCompatEditText txt_newpass = dialogView.findViewById(R.id.txt_newpass);
        AppCompatTextView strenthconfirm = dialogView.findViewById(R.id.strenthconfirm);
        AppCompatTextView strenth = dialogView.findViewById(R.id.strenth);
        eyeshow =  dialogView.findViewById(R.id.eye);
        eyeshow1 =  dialogView.findViewById(R.id.eye1);
        eyehide =  dialogView.findViewById(R.id.eyehide);
        eyehide1 = dialogView. findViewById(R.id.eye1hide);
        AppCompatEditText txt_confirmpass = dialogView.findViewById(R.id.txt_confirmpass);
        eyehide.setOnClickListener(view -> {
            if (txt_newpass.getText().length() > 0) {
                txt_newpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeshow.setVisibility(View.VISIBLE);
                eyehide.setVisibility(View.GONE);
                txt_newpass.setSelection(txt_newpass.length());
            }

        });
        eyeshow.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_newpass.getText()).length() > 0) {
                txt_newpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeshow.setVisibility(View.GONE);
                eyehide.setVisibility(View.VISIBLE);
                txt_newpass.setSelection(txt_newpass.length());
            }
        });

        eyehide1.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_confirmpass.getText()).length() > 0) {
                txt_confirmpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeshow1.setVisibility(View.VISIBLE);
                eyehide1.setVisibility(View.GONE);
                txt_confirmpass.setSelection(txt_confirmpass.length());
            }

        });
        eyeshow1.setOnClickListener(view -> {
            if (Objects.requireNonNull(txt_confirmpass.getText()).length() > 0) {
                txt_confirmpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeshow1.setVisibility(View.GONE);
                eyehide1.setVisibility(View.VISIBLE);
                txt_confirmpass.setSelection(txt_confirmpass.length());
            }
        });
        ok.setOnClickListener(view -> {
            if (cmn.isOnline(context)) {
                String email_text = Objects.requireNonNull(txt_newpass.getText()).toString();
                if (txt_newpass.getText().toString().length() > 0) {
                    if (Objects.requireNonNull(txt_confirmpass.getText()).toString().length() > 0) {
                        if (txt_newpass.getText().toString().equals(txt_confirmpass.getText().toString())) {
                            changepass(email_text);
                        } else {
                            cmn.showAlert("Lösenorden matchar inte",this);
                        }
                    } else {
                        cmn.showAlert("Vänligen bekräfta lösenord",this);
                    }
             /*   if (cmn.isEmailValid(email_text)) {

                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }*/
                } else {

                    cmn.showAlert("Skriv in nytt lösenord",this);

                }
            }

        });
        txt_newpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Calculate password strength
                calculateStrength(editable.toString(), strenth);
            }
        });
        txt_confirmpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Calculate password strength
                calculateStrength(editable.toString(), strenthconfirm);
            }
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void calculateStrength(String passwordText, AppCompatTextView strenth) {
        int upperChars = 0, lowerChars = 0, numbers = 0,
                specialChars = 0, otherChars = 0, strengthPoints = 0;
        char c;

        int passwordLength = passwordText.length();

        if (passwordLength == 0) {
            strenth.setText("Fel lösenord");
            strenth.setTextColor(Color.RED);
            return;
        }

        //If password length is <= 5 set strengthPoints=1
        if (passwordLength <= 5) {
            strengthPoints = 1;
        }
        //If password length is >5 and <= 10 set strengthPoints=2
        else if (passwordLength <= 10) {
            strengthPoints = 2;
        }
        //If password length is >10 set strengthPoints=3
        else
            strengthPoints = 3;
        // Loop through the characters of the password
        for (int i = 0; i < passwordLength; i++) {
            c = passwordText.charAt(i);
            // If password contains lowercase letters
            // then increase strengthPoints by 1
            if (c >= 'a' && c <= 'z') {
                if (lowerChars == 0) strengthPoints++;
                lowerChars = 1;
            }
            // If password contains uppercase letters
            // then increase strengthPoints by 1
            else if (c >= 'A' && c <= 'Z') {
                if (upperChars == 0) strengthPoints++;
                upperChars = 1;
            }
            // If password contains numbers
            // then increase strengthPoints by 1
            else if (c >= '0' && c <= '9') {
                if (numbers == 0) strengthPoints++;
                numbers = 1;
            }
            // If password contains _ or @
            // then increase strengthPoints by 1
            else if (c == '_' || c == '@') {
                if (specialChars == 0) strengthPoints += 1;
                specialChars = 1;
            }
            // If password contains any other special chars
            // then increase strengthPoints by 1
            else {
                if (otherChars == 0) strengthPoints += 2;
                otherChars = 1;
            }
        }

        if (strengthPoints <= 3) {
            strenth.setText("Låg");
            strenth.setTextColor(Color.RED);
        } else if (strengthPoints <= 6) {
            strenth.setText("Medium");
            strenth.setTextColor(Color.parseColor("#CFBD1F"));
        } else if (strengthPoints <= 9) {
            strenth.setText("Hög");
            strenth.setTextColor(Color.GREEN);
        }
    }
}
