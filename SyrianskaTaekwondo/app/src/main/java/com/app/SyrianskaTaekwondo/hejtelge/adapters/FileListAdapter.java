/*
 *  Copyright (c) 2018, Jaisel Rahman <jaiselrahman@gmail.com>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.app.SyrianskaTaekwondo.hejtelge.adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.model.MediaFile;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private ArrayList<MediaFile> mediaFiles;
    private Context context;
    AlertDialog alertDialog;
    public FileListAdapter(Context context, ArrayList<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaFile mediaFile = mediaFiles.get(position);
        Context context = holder.itemView.getContext();
        int i = position + 1;
        if (mediaFile.getName().equals("http")) {
            holder.fileBucketName.setText(mediaFile.getBucketName());

        } else {
            holder.fileBucketName.setText(mediaFile.getName());
        }
//        holder.fileBucketName.setText("Dokument " + i);
        if (mediaFile.getMediaType() == MediaFile.TYPE_IMAGE
                || mediaFile.getMediaType() == MediaFile.TYPE_VIDEO) {
            Glide.with(context)
                    .load(mediaFile.getUri())
                    .into(holder.fileThumbnail);
        } else if (mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {
            Glide.with(context)
                    .load(mediaFile.getThumbnail())
                    .placeholder(R.drawable.ic_audio)
                    .into(holder.fileThumbnail);
        } else {
            holder.fileThumbnail.setImageResource(R.drawable.document);
        }
        holder.del.setOnClickListener(view -> {
            if (mediaFile.getName() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Är du säker på att du vill ta bort det här dokument?");
                builder.setPositiveButton("Ja", (dialogInterface, j) -> {
                    getDeleteAPI(position);

//                    alertDialog.dismiss();

                });
                builder.setNegativeButton("Avbryt", (dialogInterface, j) -> alertDialog.dismiss());
                alertDialog = builder.create();
                alertDialog.show();



            }
            notifyDataSetChanged();
        });

    }

    public ArrayList<MediaFile> getlist() {

        return mediaFiles;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView fileThumbnail, del;
        private TextView filePath, fileSize, fileMime, fileBucketName;

        ViewHolder(View view) {
            super(view);
            fileThumbnail = view.findViewById(R.id.file_thumbnail);

            fileBucketName = view.findViewById(R.id.file_bucketname);
            del = view.findViewById(R.id.dele);
        }
    }


    private void getDeleteAPI(int pos) {
        ProgressDialog mprogdialog = ProgressDialog.show(context, "", "Vänta", true);
        mprogdialog.setCancelable(false);
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("document", mediaFiles.get(pos).getMimeType());
            object.put("user_id", new CommonMethods().getPrefsData(context, "id", ""));

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {

                if (result.isStatus()) {
                    mprogdialog.dismiss();
                    mediaFiles.remove(pos);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_document");
    }
}

