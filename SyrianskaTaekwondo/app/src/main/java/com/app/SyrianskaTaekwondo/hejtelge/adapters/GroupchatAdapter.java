package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ImageShowDetails;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupChatMessage;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;

public class GroupchatAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;

    private List<GroupChatMessage> horizontalList;
    private List<GroupChatMessage> filtedlist;
    private AlertDialog alertDialog;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private long mLastClickTime = 0;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public Context activity;

    private String userid;

    public GroupchatAdapter(List<GroupChatMessage> students, RecyclerView recyclerView, Context activity) {
        horizontalList = students;
        this.filtedlist = students;
        this.activity = activity;

        userid = new CommonMethods().getPrefsData(activity, "id", "");

/*
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
*/
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            ((StudentViewHolder) holder).setData(horizontalList.get(position),position);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, username, sender_time, reciever_time, value_image, value_image_rec;
        LinearLayout ll_recieve, ll_sender, ll_single, ll_multiple, ll_extra, ll_extra_rec, ll_multiple_rec, ll_single_rec;

        public AvatarView image_group,receiverProfileImage;
        public ImageView layout, messageSenderPicture, messageReceiverPicture, single_img, img1, img2, img3, img4, single_img_rec, img1_rec, img2_rec, img3_rec, img4_rec;
        public CardView crd_images, crd_images_rec;

        StudentViewHolder(View view) {
            super(view);
            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            sender_time = itemView.findViewById(R.id.sender_time);
            reciever_time = itemView.findViewById(R.id.time);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            image_group = itemView.findViewById(R.id.image_group);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            layout = itemView.findViewById(R.id.message_sender_image_view);
            username = itemView.findViewById(R.id.username);
            ll_recieve = itemView.findViewById(R.id.ll_recieve);
            ll_single = itemView.findViewById(R.id.ll_single);
            ll_multiple = itemView.findViewById(R.id.ll_multiple);
            crd_images = itemView.findViewById(R.id.crd_images);
            ll_sender = itemView.findViewById(R.id.ll_sender);
            single_img = itemView.findViewById(R.id.single_img);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            ll_extra = itemView.findViewById(R.id.txt);
            value_image = itemView.findViewById(R.id.value_count);


            single_img_rec = itemView.findViewById(R.id.single_img_recieve);
            img1_rec = itemView.findViewById(R.id.img_rec1);
            img2_rec = itemView.findViewById(R.id.img_rec2);
            img3_rec = itemView.findViewById(R.id.img_rec3);
            img4_rec = itemView.findViewById(R.id.img_rec4);
            ll_extra_rec = itemView.findViewById(R.id.txt_recieve);
            value_image_rec = itemView.findViewById(R.id.value_count_recieve);
            ll_single_rec = itemView.findViewById(R.id.ll_single_recieve);
            ll_multiple_rec = itemView.findViewById(R.id.ll_multiple_recieve);
            crd_images_rec = itemView.findViewById(R.id.crd_images_recieve);
        }

        void setData(GroupChatMessage data,int position) {
            String fromUserID = data.getSenderId();
            String name = data.getSenderName();

            String desc = data.getMessage();
            if (desc != null) {
                desc = desc.replaceFirst("<p dir=\"ltr\">", "");
                desc = desc.replaceAll("<p dir=\"ltr\">", "<br>");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                desc = desc.replaceAll("</p>", "");
            }

            receiverMessageText.setVisibility(View.GONE);
            receiverProfileImage.setVisibility(View.GONE);
            senderMessageText.setVisibility(View.GONE);
            messageSenderPicture.setVisibility(View.GONE);
            messageReceiverPicture.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
            ll_recieve.setVisibility(View.GONE);
            ll_sender.setVisibility(View.GONE);
            itemView.setOnClickListener(v -> {
                if (data.getPost_images().size() > 0) {
                    ArrayList<String> arrimg = new ArrayList<>();
                    for (HashMap map : data.getPost_images()) {
                        arrimg.add(Objects.requireNonNull(map.get("image")).toString());
                    }
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    activity.startActivity(new Intent(activity, ImageShowDetails.class).putExtra("image", arrimg).putExtra("position", "" + "0"));

                }
            });
            if (horizontalList.get(position).getSenderName().length() > 0) {
                String namea = String.valueOf(horizontalList.get(position).getSenderName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(activity)
                        .load(horizontalList.get(position).getSenderImage())
                        .placeholder(drawable)
                        .centerCrop()
                        .into(image_group);
            }



            if (userid.equals(fromUserID)) {
                if (data.getPost_images().size() > 0) {
                    if (data.getPost_images().size() == 1) {
                        Glide.with(activity)
                                .load(data.getPost_images().get(0).get("image"))
                                .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                .centerCrop()
                                .into(single_img);
                        ll_multiple.setVisibility(View.GONE);
                        single_img.setVisibility(View.VISIBLE);
                        ll_single.setVisibility(View.VISIBLE);
                    } else {
                        for (int i = 0; i < data.getPost_images().size(); i++) {
                            if (i == 0) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img1);
                                img1.setVisibility(View.VISIBLE);
                                img2.setVisibility(View.GONE);
                                img3.setVisibility(View.GONE);
                                img4.setVisibility(View.GONE);
                            }
                            if (i == 1) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img2);

                                img1.setVisibility(View.VISIBLE);
                                img2.setVisibility(View.VISIBLE);
                                img3.setVisibility(View.GONE);
                                img4.setVisibility(View.GONE);
                                ll_extra.setVisibility(View.GONE);

                            }
                            if (i == 2) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img3);

                                img1.setVisibility(View.VISIBLE);
                                img2.setVisibility(View.VISIBLE);
                                img3.setVisibility(View.VISIBLE);
                                img4.setVisibility(View.GONE);
                                ll_extra.setVisibility(View.GONE);

                            }
                            if (i == 3) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img4);

                                img1.setVisibility(View.VISIBLE);
                                img2.setVisibility(View.VISIBLE);
                                img3.setVisibility(View.VISIBLE);
                                img4.setVisibility(View.VISIBLE);
                                ll_extra.setVisibility(View.GONE);

                            }
                            if (i == 4) {
                                ll_extra.setVisibility(View.VISIBLE);
                                int size = data.getPost_images().size() - 4;
                                value_image.setText("+" + size);
                            }
                        }

                        ll_multiple.setVisibility(View.VISIBLE);
                        ll_single.setVisibility(View.GONE);
                    }
                    crd_images.setVisibility(View.VISIBLE);

                } else {
                    crd_images.setVisibility(View.GONE);

                }

                ll_sender.setVisibility(View.VISIBLE);

                if (desc.length() > 0) {
                    senderMessageText.setVisibility(View.VISIBLE);
                    senderMessageText.setText(Html.fromHtml(desc));

                } else {
                    senderMessageText.setVisibility(View.GONE);

                }
                sender_time.setText(data.getTime());
                username.setText(name.trim());
            }

            else {
                if (data.getPost_images().size() > 0) {
                    if (data.getPost_images().size() == 1) {
                        Glide.with(activity)
                                .load(data.getPost_images().get(0).get("image"))
                                .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                //.centerCrop()
                                .into(single_img_rec);
                        ll_multiple_rec.setVisibility(View.GONE);
                        single_img_rec.setVisibility(View.VISIBLE);
                        ll_single_rec.setVisibility(View.VISIBLE);
                    } else {
                        for (int i = 0; i < data.getPost_images().size(); i++) {
                            if (i == 0) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img1_rec);
                                img1_rec.setVisibility(View.VISIBLE);
                                img2_rec.setVisibility(View.GONE);
                                img3_rec.setVisibility(View.GONE);
                                img4_rec.setVisibility(View.GONE);
                            }
                            if (i == 1) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img2_rec);

                                img1_rec.setVisibility(View.VISIBLE);
                                img2_rec.setVisibility(View.VISIBLE);
                                img3_rec.setVisibility(View.GONE);
                                img4_rec.setVisibility(View.GONE);
                                ll_extra_rec.setVisibility(View.GONE);

                            }
                            if (i == 2) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img3_rec);

                                img1_rec.setVisibility(View.VISIBLE);
                                img2_rec.setVisibility(View.VISIBLE);
                                img3_rec.setVisibility(View.VISIBLE);
                                img4_rec.setVisibility(View.GONE);
                                ll_extra_rec.setVisibility(View.GONE);

                            }
                            if (i == 3) {
                                Glide.with(activity)
                                        .load(data.getPost_images().get(i).get("image"))
                                        .thumbnail(Glide.with(activity).load(R.drawable.spin))
                                        .centerCrop()
                                        .into(img4_rec);

                                img1_rec.setVisibility(View.VISIBLE);
                                img2_rec.setVisibility(View.VISIBLE);
                                img3_rec.setVisibility(View.VISIBLE);
                                img4_rec.setVisibility(View.VISIBLE);
                                ll_extra_rec.setVisibility(View.GONE);

                            }
                            if (i == 4) {
                                ll_extra_rec.setVisibility(View.VISIBLE);
                                int size = data.getPost_images().size() - 4;
                                value_image_rec.setText("+" + size);
                            }
                        }

                        ll_multiple_rec.setVisibility(View.VISIBLE);
                        ll_single_rec.setVisibility(View.GONE);
                    }
                    crd_images_rec.setVisibility(View.VISIBLE);

                } else {
                    crd_images_rec.setVisibility(View.GONE);

                }
                        //Log.d("dasjflkdsf",data.getGroupId());
                        ll_recieve.setVisibility(View.VISIBLE);
                        reciever_time.setText(data.getTime());
                        username.setText(name.trim());
                        receiverProfileImage.setVisibility(View.VISIBLE);
                        receiverMessageText.setVisibility(View.VISIBLE);
                        username.setVisibility(View.VISIBLE);

                if (horizontalList.get(position).getSenderName().length() > 0) {
                    String namea1 = String.valueOf(horizontalList.get(position).getSenderName().trim().charAt(0));
                    TextDrawable drawable1 = TextDrawable.builder()
                            .buildRect(namea1.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(activity)
                            .load(horizontalList.get(position).getSenderImage())
                            .placeholder(drawable1)
                            .centerCrop()
                            .into(receiverProfileImage);
                }


                        if (desc.length() > 0) {
                            receiverMessageText.setVisibility(View.VISIBLE);
                            receiverMessageText.setText(Html.fromHtml(desc.trim()));

                        } else {
                            receiverMessageText.setVisibility(View.GONE);

                        }
            }
        }

    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


}
