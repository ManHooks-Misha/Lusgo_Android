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
import androidx.recyclerview.widget.RecyclerView;


import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.ShowDocumentActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Link_Adapter extends RecyclerView.Adapter<Link_Adapter.MyViewHolder> {


    private List<String> horizontalList;
    private String flag;
    private Context context;
    public Link_Adapter(List<String> horizontalList, Context context,String flag) {
        this.horizontalList = horizontalList;
        this.flag=flag;
        this.context=context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView name, email, mobile;
        AppCompatImageView imageView;
        TextView txtview, pos;
        AppCompatImageView delete,edit;
        MyViewHolder(View view) {
            super(view);
            //   txtview = view.findViewById(R.id.group_name);
            // imageView = view.findViewById(R.id.img_edit);

            //   pos = view.findViewById(R.id.pos);
//
            name = view.findViewById(R.id.link);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);

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
        if(flag.equals("link")) {
            holder.name.setText(horizontalList.get(position));
            holder.itemView.setOnClickListener(view -> {
                if(horizontalList.get(position).contains("http")){
                    Uri uri = Uri.parse(horizontalList.get(position)); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }else {
                    Uri uri = Uri.parse("http://"+horizontalList.get(position)); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
                //  context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", horizontalList.get(position)));
            });
         //   holder.itemView.setOnClickListener(view -> context.startActivity(new Intent(context, show_Messege.class).putExtra("Url",horizontalList.get(position))));
        }else{
            File f = new File(horizontalList.get(position));

            holder.name.setText(f.getName());
            holder.itemView.setOnClickListener(view ->
                    context.startActivity(new Intent(context, ShowDocumentActivity.class).putExtra("Url",horizontalList.get(position))));
        }
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}


