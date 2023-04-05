package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityViewPagerBinding;
import com.app.SyrianskaTaekwondo.hejtelge.widget.ElasticDragDismissCallback;
import com.bumptech.glide.Glide;
import com.misha.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Objects;

public class ImageShowDetails extends AppCompatActivity {
    ActivityViewPagerBinding binding;
    ArrayList<String> arr_img = new ArrayList<>();
    String flag = "";
    String pos;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        arr_img.clear();
        if (getIntent() != null) {
            arr_img = Objects.requireNonNull(getIntent().getExtras()).getStringArrayList("image");
            Bundle bundle = getIntent().getExtras();
            pos = bundle.getString("position");
            flag = bundle.getString("flag");
        }
       // setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new SamplePagerAdapter());
        binding.viewPager.setCurrentItem(Integer.parseInt(pos));
        binding.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.dragToClose.addListener(new ElasticDragDismissCallback() {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {

            }

            @Override
            public void onDragDismissed() {
                // if we drag dismiss downward then the default reversal of the enter
                // transition would slide content upward which looks weird. So reverse it.

                if (Build.VERSION.SDK_INT >= 21) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

        if(arr_img.size()>1) {
            addDot(Integer.valueOf(pos));
        }
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                addDot(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
       /* ImageDetailsAdapter adapter = new ImageDetailsAdapter(context, arr_img);
        adapter.setCount(arr_img.size());
        binding.imageSlider.setIndicatorVisibility(false);

        binding.imageSlider.setSliderAdapter(adapter);
        binding.imageSlider.setSliderAnimationDuration(200);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(Color.BLUE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setAutoCycle(false);
        if(pos!=null) {
            binding.imageSlider.setCurrentPagePosition(Integer.parseInt(pos));
        }
*/
    }
    public void addDot(int page_position) {

        LinearLayout layout_dot = findViewById(R.id.layout_dot);

        TextView[] dot = new TextView[arr_img.size()];
        layout_dot.removeAllViews();

        for (int i = 0; i < dot.length; i++) {
            ;
            dot[i] = new TextView(this);
            dot[i].setText(Html.fromHtml("."));
            dot[i].setTextSize(70);
            //set default dot color
            dot[i].setTextColor(getResources().getColor(R.color.darker_gray));
            layout_dot.addView(dot[i]);
        }
        //set active dot color
        dot[page_position].setTextColor(getResources().getColor(R.color.bl));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    class SamplePagerAdapter extends PagerAdapter {

//        private static final int[] sDrawables = {R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper,
//                R.drawable.wallpaper, R.drawable.wallpaper, R.drawable.wallpaper};

        @Override
        public int getCount() {
            return arr_img.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Glide.with(ImageShowDetails.this)
                    .load(arr_img.get(position))
                    .fitCenter()
                    .thumbnail(Glide.with(ImageShowDetails.this).load(R.drawable.spin_))
                    .into(photoView);

            //photoView.setImageResource(arr_img.get(position));
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
