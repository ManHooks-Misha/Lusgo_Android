package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;


import com.app.SyrianskaTaekwondo.hejtelge.R;

import java.util.HashMap;
import java.util.Map;

public class MyCustomButton extends AppCompatButton {
    private static Map<String, Typeface> mTypefaces;

    public MyCustomButton(final Context context) {
        this(context, null);
    }

    public MyCustomButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCustomButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (mTypefaces == null) {
            mTypefaces = new HashMap<>();
        }
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Persian);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.Persian_customTypeface);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    mTypefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }
    }

}
