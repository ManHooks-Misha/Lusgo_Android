package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.app.SyrianskaTaekwondo.hejtelge.HomePage;
import com.app.SyrianskaTaekwondo.hejtelge.LinkOpen;
import com.app.SyrianskaTaekwondo.hejtelge.LiveCommentActivity;
import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.ShowDocumentActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.News;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class NewsloadingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;
    String title, urls, description;
    private int overallXScroll = 0;

    Activity context;
    List<News> movies;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    private String userid, usertype;
    private AlertDialog alertDialog;


    public NewsloadingAdapter(List<News> students, String userid, Activity activity, String usertype) {
        this.context = activity;
        this.movies = students;
        this.usertype = usertype;
        this.userid = userid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_MOVIE) {
            return new MovieHolder(inflater.inflate(R.layout.news_adapter, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Toast.makeText(context, position+" item--------------- "+getItemCount()+isMoreDataAvailable+isLoading, Toast.LENGTH_SHORT).show();

        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            //    Toast.makeText(context, position+" item "+getItemCount()+isMoreDataAvailable+isLoading, Toast.LENGTH_SHORT).show();

            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_MOVIE) {
            ((MovieHolder) holder).bindData(context, movies.get(position), position);
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if ((movies.get(position)).getType().equals("News")) {
            return TYPE_MOVIE;
        } else {
            return TYPE_LOAD;
        }



         //return movies.size();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /* VIEW HOLDERS */

    class MovieHolder extends RecyclerView.ViewHolder {
        AppCompatImageView thumb, img;
        TextView txtview;
        AppCompatTextView team_name, link_txt, btnclick, txt_name, doc_text, create_comment, time, titles, link_txt_common;
        //  SliderView sliderView;
        AppCompatImageView image;
        AppCompatImageView createComment;

        View v_doc;
        AvatarView profile;
        AppCompatImageView ProfileActivity_ivLine;
        FrameLayout comments, ff_youtube;
        RecyclerView subNewsList;
        LinearLayout llleft, llright, lldoc, ll_link, ll_linkcommon;


        public MovieHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.news_txt);
            doc_text = view.findViewById(R.id.doc_txt);
            profile = view.findViewById(R.id.profile);
            ProfileActivity_ivLine = view.findViewById(R.id.ProfileActivity_ivLine);
            team_name = view.findViewById(R.id.team_name);
            link_txt = view.findViewById(R.id.link_txt);
            btnclick = view.findViewById(R.id.btnclick);
            llleft = view.findViewById(R.id.llLeft);
            llright = view.findViewById(R.id.llRight);
            lldoc = view.findViewById(R.id.ll_doc);
            //   sliderView = view.findViewById(R.id.imageSlider);
            image = view.findViewById(R.id.image);
            txt_name = view.findViewById(R.id.txt_name);
            ll_link = view.findViewById(R.id.ll_link);
            createComment = view.findViewById(R.id.comm);
            comments = view.findViewById(R.id.comments);
            create_comment = view.findViewById(R.id.create_comment);
            ff_youtube = view.findViewById(R.id.ff_youtube);
            thumb = view.findViewById(R.id.thumb);
            time = view.findViewById(R.id.time);
            titles = view.findViewById(R.id.title);
            ll_linkcommon = view.findViewById(R.id.ll_linkcommon);
            link_txt_common = view.findViewById(R.id.link_txt_common);
            v_doc = view.findViewById(R.id.v_doc);
            img = view.findViewById(R.id.img);
            subNewsList = view.findViewById(R.id.subNewsList);
        }

        void bindData(Activity activity, News menuItem, int position) {
            String desc = Objects.requireNonNull(menuItem.getDescription());
            String team_namestr = Objects.requireNonNull(menuItem.getTeam_name());
            String link = Objects.requireNonNull(menuItem.getLink());
            String name = Objects.requireNonNull(menuItem.getName());
            String doc = Objects.requireNonNull(menuItem.getDoc());
            String profile_image = Objects.requireNonNull(menuItem.getProfile_image());

            if (team_namestr.length() > 0) {
                team_name.setText(team_namestr);
                img.setVisibility(View.VISIBLE);
                team_name.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.GONE);
                team_name.setVisibility(View.GONE);
            }
            File file = new File(doc);
            long now = System.currentTimeMillis() - 1000;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(menuItem.getCreated());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date != null;
            long result = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);
            String timediff = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
            time.setText(timediff);
            //updateLabel(time, result);
            String docname = file.getName();
            if (userid.length() > 0) {
                create_comment.setVisibility(View.VISIBLE);
            } else {
                create_comment.setVisibility(View.GONE);
                createComment.setVisibility(View.GONE);
                comments.setVisibility(View.GONE);

            }

            if (userid.equals(menuItem.getInsertby())){
                createComment.setVisibility(View.VISIBLE);
            }else {
                createComment.setVisibility(View.GONE);
            }

//            if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
//                createComment.setVisibility(View.VISIBLE);
//                if (menuItem.getUsertype().equals(ConsURL.coach)){
//                    createComment.setVisibility(View.GONE);
//                }
//
//            } else {
//                if (usertype.equals(ConsURL.coach) && userid.equals(menuItem.getInsertby())) {
//                    createComment.setVisibility(View.VISIBLE);
//
//                } else {
//                    createComment.setVisibility(View.GONE);
//                }
//            }

            if (link.contains("youtube") || link.contains("youtu.be")) {
               // ff_youtube.setVisibility(View.VISIBLE);
                if(menuItem.getImages().size()>0){
                    subNewsList.setVisibility(View.VISIBLE);
                    ff_youtube.setVisibility(View.GONE);
                    titles.setVisibility(View.GONE);
                    ll_linkcommon.setVisibility(View.GONE);



                }
                else {
                    subNewsList.setVisibility(View.GONE);
                    ff_youtube.setVisibility(View.VISIBLE);
                    titles.setVisibility(View.VISIBLE);
                    ll_linkcommon.setVisibility(View.GONE);
                }
                image.setVisibility(View.GONE);

                link_txt.setText(Html.fromHtml(link));
                titles.setText(menuItem.getYouTubeTitle());


                Glide.with(context)
                        .load(menuItem.getYouTubeThubnails())
                        .placeholder(R.drawable.spin_)
                        .into(thumb);

            } else {
                ff_youtube.setVisibility(View.GONE);
                 subNewsList.setVisibility(View.VISIBLE);
                //  image.setVisibility(View.VISIBLE);
                ll_linkcommon.setVisibility(View.VISIBLE);
                ll_link.setVisibility(View.GONE);
                if (link.length() > 0) {
                    ll_linkcommon.setVisibility(View.VISIBLE);
                    link_txt_common.setText(Html.fromHtml(link));
                } else {
                    ll_link.setVisibility(View.GONE);
                    link_txt_common.setVisibility(View.GONE);
                }
            }
            doc_text.setOnClickListener(view -> activity.startActivity(new Intent(activity, ShowDocumentActivity.class).putExtra("Url", menuItem.getDoc())));

            if (docname.isEmpty()) {
                v_doc.setVisibility(View.GONE);
                lldoc.setVisibility(View.GONE);
            } else {
                v_doc.setVisibility(View.VISIBLE);

                doc_text.setText("Dokument");
                lldoc.setVisibility(View.VISIBLE);

            }

            if (menuItem.getName().length() > 0) {
                String namea = String.valueOf(menuItem.getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView)
                        .load(profile_image)
                        .placeholder(drawable)
                        .centerCrop()
                        .into(profile);
            }
         //   ProfileActivity_ivLine.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_360_rotation));

            txt_name.setText(name.trim());

            txt_name.setOnClickListener(view -> {
                String id = menuItem.getInsertby();
                if (userid.equals(id)) {
                    activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", id).putExtra("flag", "own"));
                    //activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                } else {
                    activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                    //activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
            profile.setOnClickListener(view -> {
                String id = menuItem.getInsertby();
                if (userid.equals(id)) {
                    activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", id).putExtra("flag", "own"));
                   // activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    activity.startActivity(new Intent(activity, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                   // activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
            Object img = (menuItem.getImages());
            ArrayList<HashMap<String, String>> arr = (ArrayList<HashMap<String, String>>) img;
            desc = desc.replaceFirst("<p dir=\"ltr\">", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            desc = desc.replaceAll("</p>", "");
            desc = desc.replaceAll("</span>", "");
            desc = desc.replaceAll("<span style=\"background-color:#669DF6;\">", "");
            txtview.setText(Html.fromHtml(desc));
            //txtview.initViews();
            if (txtview.getText().toString().length() >400) {
                makeTextViewResizable(txtview, 10, "Läs mer", true);
            }
            ll_link.setOnClickListener(v -> {

                if (menuItem.getLink().contains("http")) {
                    Uri uri = Uri.parse(menuItem.getLink()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } else {
                    Uri uri = Uri.parse("http://" + menuItem.getLink()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }

            });


            ll_linkcommon.setOnClickListener(v -> {
                if (menuItem.getLink().contains("http")) {
                    context.startActivity(new Intent(context, LinkOpen.class).putExtra("url", menuItem.getLink()).putExtra("link", link));


                } else {
                    context.startActivity(new Intent(context, LinkOpen.class).putExtra("url", menuItem.getLink()).putExtra("link", link));

                }

            });
            thumb.setOnClickListener(v -> {
                if (menuItem.getLink().contains("http")) {
                    Uri uri = Uri.parse(menuItem.getLink()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } else {
                    Uri uri = Uri.parse("http://" + menuItem.getLink()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }

            });

            String NoComment = menuItem.getComments();
            if (NoComment.equals("0")) {
                create_comment.setText("Kommentera");
            } else {
                create_comment.setText("Kommentera" + " (" + NoComment + " Kommentarer)");
            }

            //  final SliderImageNews adapter = new SliderImageNews(activity, arr);
            assert arr != null;
            if (arr != null) {
                if (arr.size() > 0) {
                   /* sliderView.setVisibility(View.VISIBLE);

                    sliderView.setSliderAdapter(new SliderImageNews(activity, arr));
                    sliderView.setIndicatorVisibility(false);
                    sliderView.setSliderAnimationDuration(200);
                    sliderView.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                    sliderView.setIndicatorSelectedColor(Color.BLUE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setAutoCycle(false);
                    sliderView.setVisibility(View.GONE);*/

                    // image.setVisibility(View.VISIBLE);
                    subNewsList.setVisibility(View.VISIBLE);
                    for (int i = 0; i < menuItem.getImages().size(); i++) {
                        String url = menuItem.getImages().get(i).get("location");

                    }

                    SubNewsListAdapter adapterAds = new SubNewsListAdapter(menuItem.getImages(), context,menuItem.getImages_thumbnail());
                    LinearLayoutManager laymanager = new LinearLayoutManager(context);
                    laymanager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    subNewsList.setLayoutManager(laymanager);
                    subNewsList.setAdapter(adapterAds);

                    //get position
                    subNewsList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            overallXScroll = overallXScroll + dx;
                            Log.i("check", "overall X  = " + overallXScroll);
                        }
                    });

                    //signle item at time.
                    try {
                        SnapHelper snapHelper = new PagerSnapHelper();
                        subNewsList.setLayoutManager(laymanager);
                        snapHelper.attachToRecyclerView(subNewsList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    //   sliderView.setVisibility(View.GONE);
                    // image.setVisibility(View.GONE);
                }
            } else {
                //  sliderView.setVisibility(View.GONE);
                // image.setVisibility(View.GONE);
            }
            createComment.setOnClickListener(v -> {
                alert(menuItem.getId(), position);
//                    Intent intent = new Intent(mContext, CommentActivity.class);
//                    intent.putExtra("news_id", menuItem.getId());
//                    mContext.startActivity(intent);

            });

            comments.setOnClickListener(v -> {
                Intent intent = new Intent(activity, LiveCommentActivity.class);
                intent.putExtra("news_id", menuItem.getId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });

        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                tv.setText(text);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE);
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                      //  makeTextViewResizable(tv, -1, "Se mindre", false);
                    } else {
                        makeTextViewResizable(tv, 10, "Läs mer", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }

    private void alert(String newsid, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill ta bort inlägget.");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                getDeleteAPI(newsid, pos);
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

    private void getDeleteAPI(String newsid, int pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("news_id", newsid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
//                        arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
//                    }
                    //  AddCampaign.img.clear();
                    context.startActivity(new Intent(context, HomePage.class));
                    context.finish();
//                    mRecyclerViewItems.remove(pos);
//                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST + "delete_news");

    }


    private void getLinkURLAPI(String link) {
        //  arr.clear();
        String requestData;

        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");


            object.put("url", link);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    String object = result.getData();

                    context.startActivity(new Intent(context, LinkOpen.class).putExtra("url", object).putExtra("link", link));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST1 + "geturl");

    }



    public String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }


    class OnLoadMoreListener1 {
    }
}
