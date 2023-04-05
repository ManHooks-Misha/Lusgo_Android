package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.BaseDynamicLinkAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityUpdateInfoBinding;
import com.app.SyrianskaTaekwondo.hejtelge.grid.DynamicGridView;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateAPP;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateInfoPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.documentorder;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.linkorder;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

public class Update_info extends MasterActivity {
    ActivityUpdateInfoBinding binding;
    List<HashMap<String, String>> arr_contact = new ArrayList<>();
    private String userid;
    List<String> arr_about = new ArrayList<>();

    ContactDynamicAdapter adapter_contact;
    LinkDynamicAdapter adapter;
    DocumentDynamicAdapter adapter_document;
    List<HashMap<String, String>> arr_l = new ArrayList<>();
    List<HashMap<String, String>> arr_l_final = new ArrayList<>();
    List<HashMap<String, String>> arr_document = new ArrayList<>();
    private AlertDialog alertDialog;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    AppCompatTextView upload_doc;
    CommonMethods commonMethods=new CommonMethods();
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // binding.about.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Uppdatera information");
        userid = new CommonMethods().getPrefsData(Update_info.this, "id", "");


        getUserAPI();
        if (getIntent() != null) {
            flag = getIntent().getStringExtra("Flag");
        }
        binding.addLink.setOnClickListener(v ->
                startActivity(new Intent(this, Add_newLink.class).putExtra("flag", "newActi")));
        binding.documentBtn.setOnClickListener(v -> startActivity(new Intent(this, Add_Document.class).putExtra("flag", "newActi")));
        binding.addContact.setOnClickListener(v -> {
            startActivity(new Intent(this, Add_Contact.class).putExtra("flag", "newActi"));

        });

        binding.link.setOnClickListener(view -> {
            binding.link.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.link.setTextColor(Color.WHITE);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);
            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);
            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.VISIBLE);
        });
        binding.info.setOnClickListener(view -> {
            binding.info.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.info.setTextColor(Color.WHITE);

            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);

            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.VISIBLE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.GONE);
        });
        binding.document.setOnClickListener(view -> {
            binding.document.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.document.setTextColor(Color.WHITE);
            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);

            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);
            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.VISIBLE);
            binding.llLink1.setVisibility(View.GONE);
        });
        binding.contact.setOnClickListener(view -> {
            binding.contact.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.contact.setTextColor(Color.WHITE);

            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);
            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);

            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.VISIBLE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.GONE);
        });
        binding.llLink.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {


            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                binding.llLink.stopEditMode();

            }


        });
        binding.llLink.setOnItemLongClickListener((parent, view, position, id) -> {
            binding.llLink.startEditMode(position);
            return true;
        });
        binding.fileList.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {


            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                binding.fileList.stopEditMode();

            }


        });
        binding.fileList.setOnItemLongClickListener((parent, view, position, id) -> {
            binding.fileList.startEditMode(position);
            return true;
        });
        binding.llContact.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {

            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
                binding.llContact.stopEditMode();


            }


        });
        binding.llContact.setOnItemLongClickListener((parent, view, position, id) -> {
            binding.llContact.startEditMode(position);
            return true;
        });
    }


    private void getUserAPI() {
        arr_contact.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());
                    JSONArray document = object.getJSONArray("document");
                    JSONArray links = object.getJSONArray("links");
                    JSONArray about_us = object.getJSONArray("about_us");
                    JSONArray contactlist = object.getJSONArray("contactList");
                    for (int i = 0; i < contactlist.length(); i++) {
                        JSONObject obj = contactlist.getJSONObject(i);
                        HashMap map = new HashMap();
                        String id = obj.getString("id");
                        String email = obj.getString("email");
                        String mobile_no = obj.getString("mobile_no");
                        String name = obj.getString("name");
                        String role = obj.getString("website");
                        String contactOrder = obj.getString("contactOrder");
                        String image = obj.getString("image");
                        map.put("email", email);
                        map.put("mobile_no", mobile_no);
                        map.put("name", name);
                        map.put("role", role);
                        map.put("contactOrder", contactOrder);
                        map.put("image", image);
                        map.put("id", id);
                        arr_contact.add(map);
                    }

                    runOnUiThread(() -> {
                        for (int i = 0; i < about_us.length(); i++) {
                            try {
                                //binding.about.setText(Html.fromHtml(about_us.get(0).toString()));
                                binding.about.setText(about_us.get(0).toString());
                                binding.llInfo.setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        for (int i = 0; i < links.length(); i++) {
                            HashMap map = new HashMap();
                            try {
                                JSONObject obj_link = links.getJSONObject(i);
                                String link = obj_link.getString("linkName");
                                String linkUrl = obj_link.getString("linkUrl");
                                String linkOrder = obj_link.getString("linkOrder");
                                String id = obj_link.getString("id");
                                map.put("linkName", link);
                                map.put("linkUrl", linkUrl);
                                map.put("linkOrder", linkOrder);
                                map.put("l_id", id);
                                arr_l.add(map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < document.length(); i++) {
                            HashMap map = new HashMap();

                            try {
                                JSONObject object1 = document.getJSONObject(i);

                                map.put("id", object1.getString("id"));
                                map.put("documentUrl", object1.getString("documentUrl"));
                                map.put("documentName", object1.getString("documentName"));
                                map.put("documentoder", object1.getString("documentOrder"));
                                arr_document.add(map);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        adapter_contact = new ContactDynamicAdapter(Update_info.this,
                                arr_contact, 1);
                        //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                        binding.llContact.setAdapter(adapter_contact);
                        adapter_contact.notifyDataSetChanged();
                        adapter = new LinkDynamicAdapter(Update_info.this,
                                arr_l, 1);
                        //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                        binding.llLink.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        adapter_document = new DocumentDynamicAdapter(Update_info.this,
                                arr_document, 1);
                        //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                        binding.fileList.setAdapter(adapter_document);
                        adapter_document.notifyDataSetChanged();
                        if(flag.equals("about")){
                            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.link.setTextColor(Color.BLACK);
                            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.document.setTextColor(Color.BLACK);
                            binding.info.setBackgroundColor(getResources().getColor(R.color.bluedark));
                            binding.info.setTextColor(Color.WHITE);
                            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.contact.setTextColor(Color.BLACK);
                            binding.llInfo.setVisibility(View.VISIBLE);
                            binding.llContact1.setVisibility(View.GONE);
                            binding.llDocument.setVisibility(View.GONE);
                            binding.llLink1.setVisibility(View.GONE);
                        } else if(flag.equals("doc")){
                            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.link.setTextColor(Color.BLACK);
                            binding.document.setBackgroundColor(getResources().getColor(R.color.bluedark));
                            binding.document.setTextColor(Color.WHITE);
                            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.info.setTextColor(Color.BLACK);
                            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.contact.setTextColor(Color.BLACK);
                            binding.llInfo.setVisibility(View.GONE);
                            binding.llContact1.setVisibility(View.GONE);
                            binding.llDocument.setVisibility(View.VISIBLE);
                            binding.llLink1.setVisibility(View.GONE);
                        }else if(flag.equals("link")){
                            binding.link.setBackgroundColor(getResources().getColor(R.color.bluedark));
                            binding.link.setTextColor(Color.WHITE);
                            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.document.setTextColor(Color.BLACK);
                            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.info.setTextColor(Color.BLACK);
                            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.contact.setTextColor(Color.BLACK);
                            binding.llInfo.setVisibility(View.GONE);
                            binding.llContact1.setVisibility(View.GONE);
                            binding.llDocument.setVisibility(View.GONE);
                            binding.llLink1.setVisibility(View.VISIBLE);
                        }else if(flag.equals("contact")){
                            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.link.setTextColor(Color.BLACK);
                            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.document.setTextColor(Color.BLACK);
                            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
                            binding.info.setTextColor(Color.BLACK);
                            binding.contact.setBackgroundColor(getResources().getColor(R.color.bluedark));
                            binding.contact.setTextColor(Color.WHITE);
                            binding.llInfo.setVisibility(View.GONE);
                            binding.llContact1.setVisibility(View.VISIBLE);
                            binding.llDocument.setVisibility(View.GONE);
                            binding.llLink1.setVisibility(View.GONE);
                        }
                      /*  if (arr_l.size() > 0) {
                            Collections.reverse(arr_l);
                        }
                        for (int i = 0; i < arr_l.size(); i++) {

                        }*/


                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "data_informations");
    }

    public class LinkDynamicAdapter extends BaseDynamicLinkAdapter {

        public LinkDynamicAdapter(Update_info context, List<HashMap<String, String>> items, int columnCount) {
            super(context, items, columnCount, "link");

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.link_adapter, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(getItems().get(pos), pos);
            return convertView;
        }

        private class CheeseViewHolder {
            private AppCompatTextView link;
            private AppCompatImageView edit, delete;

            private CheeseViewHolder(View view) {
                //titleText = (TextView) view.findViewById(R.id.item_title);
                link = view.findViewById(R.id.link);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
            }

            void build(HashMap title, int pos) {
                // titleText.setText(title);
                String links = Objects.requireNonNull(title.get("linkName")).toString();
                link.setText(links);

                edit.setOnClickListener(v -> {
                    String links1 = Objects.requireNonNull(title.get("linkName")).toString();
                    String id = Objects.requireNonNull(title.get("l_id")).toString();
                    String linkUrl = Objects.requireNonNull(title.get("linkUrl")).toString();
                    String linkOrder = Objects.requireNonNull(title.get("linkOrder")).toString();
                    startActivity(new Intent(Update_info.this, Add_newLink.class).putExtra("name", links1).putExtra("id", id).putExtra("linkurl", linkUrl).putExtra("order", linkOrder).putExtra("flag", "edit"));

                });
                delete.setOnClickListener(v -> alert(title.get("l_id").toString(), pos));
              /*  image.setOnClickListener(view -> {
                    if(title.get("link").toString().contains("http")){
                        Uri uri = Uri.parse(title.get("link").toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }else {
                        Uri uri = Uri.parse("http://"+title.get("link")); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }

                });*/


            }
        }
    }

    public class DocumentDynamicAdapter extends BaseDynamicLinkAdapter {

        public DocumentDynamicAdapter(Update_info context, List<HashMap<String, String>> items, int columnCount) {
            super(context, items, columnCount, "document");

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.link_adapter, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(getItems().get(pos), pos);
            return convertView;
        }

        private class CheeseViewHolder {
            private AppCompatTextView link;
            private AppCompatImageView edit, delete;

            private CheeseViewHolder(View view) {
                //titleText = (TextView) view.findViewById(R.id.item_title);
                link = view.findViewById(R.id.link);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
            }

            void build(HashMap title, int pos) {
                // titleText.setText(title);
                String links = Objects.requireNonNull(title.get("documentName")).toString();
                link.setText(links);

                edit.setOnClickListener(v -> {
                    String links1 = Objects.requireNonNull(title.get("documentName")).toString();
                    String id = Objects.requireNonNull(title.get("id")).toString();
                    String linkUrl = Objects.requireNonNull(title.get("documentUrl")).toString();
                    String linkOrder = Objects.requireNonNull(title.get("documentoder")).toString();
                    startActivity(new Intent(Update_info.this, Add_Document.class).putExtra("name", links1).putExtra("id", id).putExtra("linkurl", linkUrl).putExtra("order", linkOrder).putExtra("flag", "edit"));

                });
                delete.setOnClickListener(v -> alertDoc(title.get("id").toString(), pos, title.get("documentName").toString()));

              /*  image.setOnClickListener(view -> {
                    if(title.get("link").toString().contains("http")){
                        Uri uri = Uri.parse(title.get("link").toString()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }else {
                        Uri uri = Uri.parse("http://"+title.get("link")); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }

                });*/


            }
        }
    }

    public class ContactDynamicAdapter extends BaseDynamicLinkAdapter {

        public ContactDynamicAdapter(Update_info context, List<HashMap<String, String>> items, int columnCount) {
            super(context, items, columnCount, "contact");

        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            CheeseViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.newslist_adapter, null);
                holder = new CheeseViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (CheeseViewHolder) convertView.getTag();
            }
            holder.build(getItems().get(pos), pos);
            return convertView;
        }

        private class CheeseViewHolder {
            private AppCompatTextView name, email, mobile, role;
            private LinearLayout llname, llmobile, llemail;
            private AvatarView user_profile;
            private AppCompatImageView delete, edit;

            private CheeseViewHolder(View view) {
                //titleText = (TextView) view.findViewById(R.id.item_title);
                name = view.findViewById(R.id.name);
                mobile = view.findViewById(R.id.mobile);
                email = view.findViewById(R.id.email);
                llemail = view.findViewById(R.id.llemail);
                llmobile = view.findViewById(R.id.llmobile);
                llname = view.findViewById(R.id.llname);
                user_profile = view.findViewById(R.id.user_profile);
                role = view.findViewById(R.id.role);
                edit = view.findViewById(R.id.edit);
                delete = view.findViewById(R.id.delete);
            }

            void build(HashMap title, int pos) {
                if (title.get("name").toString().trim().length() > 0) {
                    String name_txt = String.valueOf(title.get("name").toString().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name_txt.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(Update_info.this)
                            .load(title.get("image"))
                            .placeholder(drawable)
                            .into(user_profile);

                }

                edit.setOnClickListener(v -> {
                    String name = Objects.requireNonNull(title.get("name")).toString();
                    String id = Objects.requireNonNull(title.get("id")).toString();
                    String role = Objects.requireNonNull(title.get("role")).toString();
                    String email = Objects.requireNonNull(title.get("email")).toString();
                    String contactOrder = Objects.requireNonNull(title.get("contactOrder")).toString();
                    String mobile_no = Objects.requireNonNull(title.get("mobile_no")).toString();
                    String image = Objects.requireNonNull(title.get("image")).toString();
                    startActivity(new Intent(Update_info.this, Add_Contact.class).putExtra("name", name).putExtra("id", id).putExtra("email", email).putExtra("order", contactOrder).putExtra("flag", "edit").putExtra("image", image).putExtra("role", role).putExtra("mobile", mobile_no));
                });
                delete.setOnClickListener(v -> alertContact(title.get("id").toString(), pos));

                if (!title.get("mobile_no").toString().equals("null") && !title.get("mobile_no").toString().equals("")) {
                    mobile.setText(title.get("mobile_no").toString());
                    llmobile.setVisibility(View.VISIBLE);

                } else {
                    llmobile.setVisibility(View.GONE);
                }
                if (!title.get("name").toString().equals("null") && !title.get("name").toString().equals("")) {
                    name.setText(title.get("name").toString());
                    name.setVisibility(View.VISIBLE);

                } else {
                    role.setTextSize(18);
                    role.setTypeface(role.getTypeface(), Typeface.BOLD);
                    name.setVisibility(View.GONE);
                }
                if (!title.get("email").toString().equals("") && !title.get("email").toString().equals("null") ) {
                    email.setText(title.get("email").toString());
                    llemail.setVisibility(View.VISIBLE);

                } else {
                    llemail.setVisibility(View.GONE);
                }
                if (!title.get("role").toString().equals("") && !title.get("role").toString().equals("null")) {
                    role.setText(title.get("role").toString());
                    role.setVisibility(View.VISIBLE);

                } else {
                    role.setVisibility(View.GONE);
                }
                email.setText(title.get("email").toString());


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        showErrorToast("Permission Required");
                    }
                }
            } else {
                showErrorToast("Permission Required");
            }
        }
    }

    private void alert(String linkid, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill ta bort länken?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                getDeleteAPI(linkid, pos);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void alertContact(String linkid, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill ta bort kontakten?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {

                getDeleteContactAPI(linkid, pos);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getDeleteAPI(String linkid, int pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
//            object.put("user_id", userid);
            object.put("Id", linkid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    //  context.startActivity(new Intent(context, HomePage.class));
                    //   finish();
                    arr_l.remove(pos);
                    adapter = new LinkDynamicAdapter(Update_info.this,
                            arr_l, 1);
                    //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                    binding.llLink.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST2 + "delete_link");

    }


    private void getDeleteContactAPI(String linkid, int pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
//            object.put("user_id", userid);
            object.put("Id", linkid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    //  context.startActivity(new Intent(context, HomePage.class));
                    //   finish();
                    arr_contact.remove(pos);
                    adapter_contact = new ContactDynamicAdapter(Update_info.this,
                            arr_contact, 1);
                    //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                    binding.llContact.setAdapter(adapter_contact);
                    adapter_contact.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST2 + "delete_contact");

    }

    private void alertDoc(String linkid, int pos, String file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill ta bort dokument?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                getDeleteDocumentAPI(linkid, pos, file);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getDeleteDocumentAPI(String linkid, int pos, String filename) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
//            object.put("user_id", userid);
            object.put("Id", linkid);
            object.put("Filename", filename);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    //  context.startActivity(new Intent(context, HomePage.class));
                    //   finish();
                    arr_document.remove(pos);
                    adapter_document = new DocumentDynamicAdapter(Update_info.this,
                            arr_document, 1);
                    //list.setLayoutManager(new GridLayoutManager(SponserListActivity.this, 1));
                    binding.fileList.setAdapter(adapter_document);
                    adapter_document.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST2 + "deletedocument");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {

                finish();
                return true;
            }
            if (item.getItemId() == R.id.save) {
                arr_about.clear();
               // binding.about.setPaintFlags( binding.about.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                //hideKeybaord();
                String lusgo_about = Html.toHtml(binding.about.getText());
                arr_about.add(binding.about.getText().toString());
                alertAPP(arr_about);
            }
        return super.onOptionsItemSelected(item);
    }


    private void alertAPP(List<String> about) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Update_info.this);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att uppdatera denna information?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(Update_info.this)) {
                getUpdateAPI(about);
            } else {
                Toast.makeText(Update_info.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    private void getUpdateAPI(List<String> arr_about) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        Gson gson = new Gson();
        UpdateAPP pojo = new UpdateAPP();
        pojo.access_key = ConsURL.accessKey;
        pojo.about_us = arr_about;
        pojo.user_id = userid;
        requestData = gson.toJson(pojo);


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    //  context.startActivity(new Intent(context, HomePage.class));
                    //   finish();
                    showCustomDialog();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST2 + "update_Appinformation");

    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_alert, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        AppCompatTextView create = dialogView.findViewById(R.id.create);
        create.setText("Informationen har uppdaterats");
        ok.setOnClickListener(view -> {

            startActivity(new Intent(Update_info.this, HomePage.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void getAddAPI(ArrayList<HashMap<String, String>> id) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        Gson gson = new Gson();

        linkorder pojo = new linkorder();
        pojo.access_key = ConsURL.accessKey;
        pojo.link = id;
        pojo.user_id = userid;
        requestData = gson.toJson(pojo);
    //    Log.d("checkData",requestData);

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
//                    Toast.makeText(context, "Sponsor Radera framgångsrikt", Toast.LENGTH_SHORT).show();
//                    arr.remove(position);
//                    libraryHotAdapter.notifyDataSetChanged();
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
                    //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    //   }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "update_Linkinformation");
    }

    public void getAddDocumtAPI(ArrayList<HashMap<String, String>> id) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        Gson gson = new Gson();

        documentorder pojo = new documentorder();
        pojo.access_key = ConsURL.accessKey;
        pojo.document = id;
        pojo.user_id = userid;
        requestData = gson.toJson(pojo);
      //  Log.d("checkData1",requestData);

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
//                    Toast.makeText(context, "Sponsor Radera framgångsrikt", Toast.LENGTH_SHORT).show();
//                    arr.remove(position);
//                    libraryHotAdapter.notifyDataSetChanged();
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
                    //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    //   }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "update_Documentinformation");
    }

    public void getAddContactAPI(ArrayList<HashMap<String, String>> id) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        Gson gson = new Gson();

        UpdateInfoPojo pojo = new UpdateInfoPojo();
        pojo.access_key = ConsURL.accessKey;
        pojo.contactlist = id;
        pojo.user_id = userid;
        requestData = gson.toJson(pojo);
      //  Log.d("checkData2",requestData);
        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                  //  Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "update_Contactinformation");
    }

}