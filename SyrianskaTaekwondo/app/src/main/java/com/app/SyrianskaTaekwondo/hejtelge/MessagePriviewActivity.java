package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.SliderImageNews;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityMessagePriviewBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class MessagePriviewActivity extends MasterActivity {
    ActivityMessagePriviewBinding binding;
    InboxList data;
    String timediff, userid, id, flag;
    private CommonMethods cmn;
    private ArrayList<InboxList> arr = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        context.startActivity(new Intent(context, HomePage.class).putExtra("flag", "1"));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (userid.length() > 0) {
                context.startActivity(new Intent(context, HomePage.class).putExtra("flag", "1"));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
             //   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else {
                context.startActivity(new Intent(context, Page_withOut_Login.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userid = new CommonMethods().getPrefsData(context, "id", "");
        if (userid.length() > 0) {
            binding = ActivityMessagePriviewBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            cmn = new CommonMethods();
            setSupportActionBar(binding.toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle("Inkorg");
            if (getIntent() != null) {
                //data = (InboxList) getObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")), InboxList.class);
                id = getIntent().getStringExtra("id");
                flag = getIntent().getStringExtra("flag");

            }
            if (flag.equals("sent")) {
                binding.txt.setText("Skickat");

            } else {
                binding.txt.setText("Inkorg");

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getUserAPI(id);
            }
        } else {
            startActivity(new Intent(MessagePriviewActivity.this, Page_withOut_Login.class).setFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP)));
            finish();
        }

    }

    private void getreadAPI(String msgid) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("message_id", msgid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    runOnUiThread(() ->
                            binding.btnclickRead.setBackgroundResource(R.drawable.create_button_green));


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "read_message");
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUserAPI(String id) {
        arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("message_id", id);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    // String isReadValue=obj.getString()
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((InboxList) (getObject(obj.getString(i), InboxList.class)));

                    }
                    runOnUiThread(() -> {
                        if (arr.size() > 0)
                            data = arr.get(0);
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date past = format.parse(data.getCreated());
                            Date now = new Date();

                            timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(past);
                        } catch (Exception j) {
                            j.printStackTrace();
                        }
                        binding.time.setText(timediff);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.messageTxt.setText(Html.fromHtml(data.getMessage(), Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            binding.messageTxt.setText(Html.fromHtml(data.getMessage()));
                        }
                        binding.txtName.setText(data.getName().trim());
   /* if (userid.equals(data.getSender_id())) {
        binding.sentmsg.setText("Skickat meddelande");
        binding.sentmsg.setTextColor(context.getResources().getColor(R.color.green));
    } else {
        binding. sentmsg.setText("Inkorgmeddelande");
        binding.sentmsg.setTextColor(context.getResources().getColor(R.color.yellow));

    }*/

                        //   binding.roleUser.setText(data.getRole().trim());
                        ArrayList<HashMap<String, String>> images = data.getImages();
                        if (images.size() > 0) {
                            binding.imageSlider.setVisibility(View.VISIBLE);
                        } else {
                            binding.imageSlider.setVisibility(View.GONE);

                        }

                        final SliderImageNews adapter = new SliderImageNews(MessagePriviewActivity.this, images);
                        assert images != null;
                        if (images.size() > 0) {
                            binding.imageSlider.setSliderAdapter(new SliderImageNews(MessagePriviewActivity.this, images));
                            binding.imageSlider.setIndicatorVisibility(false);
                            binding.imageSlider.setSliderAnimationDuration(200);
                            binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                            binding.imageSlider.setIndicatorSelectedColor(Color.BLUE);
                            binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            binding.imageSlider.setAutoCycle(false);
                            binding.imageSlider.setVisibility(View.VISIBLE);
                        } else {
                            binding.imageSlider.setVisibility(View.GONE);
                        }

                        if (data.getLink()!=null) {
                            if (data.getLink().length() > 0) {
                                binding.linkTxt.setVisibility(View.VISIBLE);
                                binding.llLink.setVisibility(View.VISIBLE);
                                binding.linkTxt.setText(data.getLink());
                            } else {
                                binding.linkTxt.setVisibility(View.GONE);
                                binding.llLink.setVisibility(View.GONE);
                            }
                        }
                        //
                        if (data.getSender_id().equals(userid)) {
                            binding.totalUsers.setText("Vilka har läst");
                            //  binding.readUser.setText("Läsa: " + data.getReadCount());
                            binding.totalUsers.setVisibility(View.VISIBLE);
                            // binding.readUser.setVisibility(View.VISIBLE);

                        } else {
                            binding.totalUsers.setVisibility(View.GONE);
                            //      binding.readUser.setVisibility(View.GONE);
                        }

                        binding.profile.setOnClickListener(view -> {
                            String id1 = arr.get(0).getSender_id();
                            if (userid.equals(id1)) {
                                startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id1).putExtra("flag", "own"));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            } else {
                                startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id1).putExtra("flag", "user"));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });
                        binding.txtName.setOnClickListener(view -> {
                            String id1 = arr.get(0).getSender_id();
                            if (userid.equals(id1)) {
                                startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id1).putExtra("flag", "own"));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            } else {
                                startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id1).putExtra("flag", "user"));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }
                        });

                        binding.totalUsers.setOnClickListener(view -> {
                            if (data.getRead_users().size() > 0) {
                                context.startActivity(new Intent(context, ReadUser_Message.class).putExtra("DATA", Common.INSTANCE.getBytes(data.getRead_users())).putExtra("id", id));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }
                        });
                        if (data.getIsRead() != null)
                            if (data.getIsRead().equals("1")) {
                                binding.btnclickRead.setVisibility(View.VISIBLE);
                                binding.llRead.setVisibility(View.VISIBLE);
                                binding.btnclickRead.setBackgroundResource(R.drawable.create_button_green);
                            }
                        if (data.getDocument() != null) {
                            if (data.getDocument().length() > 0) {
                                binding.docTxt.setText("Dokument");
                                binding.docTxt.setVisibility(View.VISIBLE);
                                binding.llDoc.setVisibility(View.VISIBLE);
                            } else {
                                binding.docTxt.setVisibility(View.GONE);
                            }
                        }

                        if (data.getSender_id().equals(userid)) {
                            binding.btnclickRead.setVisibility(View.GONE);
                            binding.llRead.setVisibility(View.GONE);
                        } else {
                            binding.btnclickRead.setVisibility(View.VISIBLE);
                            binding.llRead.setVisibility(View.VISIBLE);
                        }

                        if (data.getName().trim().length() > 0) {
                            String name = String.valueOf((data.getName().trim().charAt(0)));

                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                            Glide.with(context)
                                    .load(data.getProfile_image())
                                    .placeholder(drawable)
                                    .centerCrop()
                                    .into(binding.profile);
                        }


                        binding.imageSlider.setOnClickListener(view -> {
                           /* ArrayList<String> arr_images = new ArrayList<>();
                            for (HashMap map : images) {
                                arr_images.add(Objects.requireNonNull(map.get("name")).toString());
                            }*/
                            context.startActivity(new Intent(context, ImageShowDetails.class).putExtra("image", Common.INSTANCE.getJSON(images)));
                        });
                        binding.linkTxt.setOnClickListener(view -> {
                            if (data.getLink().contains("http")) {
                                Uri uri = Uri.parse(data.getLink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                Uri uri = Uri.parse("http://" + data.getLink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                            //   context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", data.getLink()));
                        });
                        binding.docTxt.setOnClickListener(view -> context.startActivity(new Intent(context, ShowDocumentActivity.class).putExtra("Url", data.getDocument())));

                        binding.btnclickRead.setOnClickListener(view -> {
                            GradientDrawable viewColor = (GradientDrawable) binding.btnclickRead.getBackground();
                            int[] colorId = viewColor.getColors();

                            if (colorId[0] == -15108371)
                                if (cmn.isOnline(context)) {
                                    getreadAPI(data.getMessage_id());
                                } else {
                                    Common.INSTANCE.showToast(context, context.getResources().getString(R.string.internet));
                                }

                        });
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "messageInfo");
    }
}
