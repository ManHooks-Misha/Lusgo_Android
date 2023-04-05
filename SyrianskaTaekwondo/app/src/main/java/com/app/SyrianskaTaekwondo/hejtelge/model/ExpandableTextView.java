package com.app.SyrianskaTaekwondo.hejtelge.model;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.app.SyrianskaTaekwondo.hejtelge.R;

public class ExpandableTextView extends AppCompatTextView {

    private  Context context;
    private TextView textView;
    private int maxLine = 10;
    private boolean isViewMore = true;

    public ExpandableTextView(Context context) {
        super(context);
        this.context = context;
        textView = this;
        initViews();
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        textView = this;
        initViews();
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        textView = this;
        initViews();
    }

    public void initViews() {
        if (textView.getText().toString().isEmpty()) {
            return;
        }
        String txt=textView.getText().toString();
      //  Log.e("LLLLLLLLLL",txt);
        //if (textView.getTag() == null) {
            textView.setTag(textView.getText());
    //    }
  //      textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "GothamBook.ttf"));
        ViewTreeObserver vto = textView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text, expandText = "Läs mer";
                int lineEndIndex;
                ViewTreeObserver obs = textView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                int lineCount = textView.getLayout().getLineCount();

               // expandText += isViewMore ? "" : " Se mindre";
                if (lineCount <= maxLine) {
                    lineEndIndex = textView.getLayout().getLineEnd(textView.getLayout().getLineCount() - 1);
                    text = textView.getText().subSequence(0, lineEndIndex).toString();
                } else if (isViewMore && maxLine > 0 && textView.getLineCount() >= maxLine) {
                  //  expandText += " Se mindre";

                    lineEndIndex = textView.getLayout().getLineEnd(maxLine - 1);
                    text = textView.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    expandText = "Se mindre";

                    lineEndIndex = textView.getLayout().getLineEnd(textView.getLayout().getLineCount() - 1);
                    text = textView.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                textView.setText(text);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                if (lineCount > maxLine)
                    textView.setText(addClickablePartTextViewResizable(expandText),
                            BufferType.SPANNABLE);
                textView.setSelected(true);
            }
        });
    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final String expandText) {
        String string = textView.getText().toString();
        SpannableStringBuilder expandedStringBuilder = new SpannableStringBuilder(string);
        if (string.contains(expandText)) {
            expandedStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    textView.setLayoutParams(textView.getLayoutParams());
                    textView.setText(textView.getTag().toString(), BufferType.SPANNABLE);
                    textView.invalidate();
                    maxLine = isViewMore ? -1 : 10;
                    isViewMore = !isViewMore;
                    initViews();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(true);
                    ds.setColor(context.getResources().getColor(R.color.bluedark));
//                    ds.setTypeface(Typeface.createFromAsset(context.getAssets(), "GothamMedium.ttf"));
                }
            }, string.indexOf(expandText), string.length(), 0);
        }
        return expandedStringBuilder;
    }
}