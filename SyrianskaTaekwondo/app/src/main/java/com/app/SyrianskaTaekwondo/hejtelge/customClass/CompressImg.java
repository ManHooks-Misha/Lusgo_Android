package com.app.SyrianskaTaekwondo.hejtelge.customClass;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created on : June 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class CompressImg {
    private static volatile CompressImg INSTANCE;
    private Context context;
    //max width and height values of the compressed image is taken as 612x816
    private float maxWidth = 1424.0f;
    private float maxHeight = 2160.0f;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    private int quality = 100;
    private String destinationDirectoryPath;

    private CompressImg(Context context) {
        this.context = context;
        destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FileUtil.FILES_PATH;
    }

    public static CompressImg getDefault(Context context) {
        if (INSTANCE == null) {
            synchronized (CompressImg.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CompressImg(context);
                }
            }
        }
        return INSTANCE;
    }

    public File compressToFile(File file) {
        return ImageUtil.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight, compressFormat, bitmapConfig, 50, destinationDirectoryPath);
    }

    public Bitmap compressToBitmap(File file) {
        return ImageUtil.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }

    public Observable<File> compressToFileAsObservable(final File file) {
        return Observable.defer(new Func0<Observable<File>>() {
            @Override
            public Observable<File> call() {
                return Observable.just(compressToFile(file));
            }
        });
    }

    public Observable<Bitmap> compressToBitmapAsObservable(final File file) {
        return Observable.defer(new Func0<Observable<Bitmap>>() {
            @Override
            public Observable<Bitmap> call() {
                return Observable.just(compressToBitmap(file));
            }
        });
    }

    public static class Builder {
        private CompressImg compressor;

        public Builder(Context context) {
            compressor = new CompressImg(context);
        }

        public CompressImg.Builder setMaxWidth(float maxWidth) {
            compressor.maxWidth = maxWidth;
            return this;
        }

        public CompressImg.Builder setMaxHeight(float maxHeight) {
            compressor.maxHeight = maxHeight;
            return this;
        }

        public CompressImg.Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            compressor.compressFormat = compressFormat;
            return this;
        }

        public CompressImg.Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            compressor.bitmapConfig = bitmapConfig;
            return this;
        }

        public CompressImg.Builder setQuality(int quality) {
            compressor.quality = quality;
            return this;
        }

        public CompressImg.Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            compressor.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        public CompressImg build() {
            return compressor;
        }
    }
}
