package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.ShowDocumentActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class Link1_Adapter extends RecyclerView.Adapter<Link1_Adapter.MyViewHolder> {


    private ArrayList<HashMap<String,String>> horizontalList;
    private String flag;
    private Context context;

    public Link1_Adapter(ArrayList<HashMap<String,String>> horizontalList, Context context, String flag) {
        this.horizontalList = horizontalList;
        this.flag = flag;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView name, email, mobile;
        AppCompatImageView delete,edit,fwd,link1;
        TextView txtview, pos;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.link);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
            link1 = view.findViewById(R.id.linkimg);
            fwd = view.findViewById(R.id.forward);

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //holder.txtview.setText("Sweta Gupta");
        // holder.txtview.setText(horizontalList.get(position).get("name"));
//        int i=position+1;
//
//        holder.pos.setText(""+i);
        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);
        holder.fwd.setVisibility(View.VISIBLE);
        holder.link1.setVisibility(View.VISIBLE);
        if (flag.equals("link")) {
            holder.name.setText(horizontalList.get(position).get("linkName"));
            holder.itemView.setOnClickListener(view -> {
                if(horizontalList.get(position).get("linkUrl").contains("http")){
                    Uri uri = Uri.parse(horizontalList.get(position).get("linkUrl")); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }else {
                    Uri uri = Uri.parse("http://"+horizontalList.get(position).get("linkUrl")); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
              //  context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", horizontalList.get(position)));
            });
        } else {
            holder.link1.setImageResource(R.drawable.doc_menuuu);
            holder.link1.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.name.setText(horizontalList.get(position).get("documentName"));
            holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, ShowDocumentActivity.class).putExtra("Url", horizontalList.get(position).get("documentUrl"))));
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(horizontalList.get(position).get("documentUrl")));
//                    context.startActivity(browserIntent);
//                }
//            });

        }
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}


