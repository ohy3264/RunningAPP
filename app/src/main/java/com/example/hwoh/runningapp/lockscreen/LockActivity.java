package com.example.hwoh.runningapp.lockscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.hwoh.runningapp.R;
import com.example.hwoh.runningapp.service.AppDetectService;
import com.example.hwoh.runningapp.util.ServiceUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by hwoh on 2016-03-18.
 */

public class LockActivity extends AppCompatActivity implements LockScreenView.OnTriggerListener {
    private final String TAG = "LockActivity";
    private LockScreenView mLockScrenn;
    private VerticalViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int page = 0;
    private ArrayList<String> mImageUrls;

    public static String[] eatFoodyImages = {
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_lock);
        mLockScrenn = new LockScreenView(this);
        mLockScrenn = (LockScreenView) findViewById(R.id.lockScreen);
        mLockScrenn.setOnTriggerListener(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < eatFoodyImages.length; i++) {
           // mSectionsPagerAdapter.addItem(eatFoodyImages[i]);
            mSectionsPagerAdapter.addItem(eatFoodyImages[i]);
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (VerticalViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // mViewPager.setOffscreenPageLimit(eatFoodyImages.length);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (ServiceUtil.isRunningService(this, AppDetectService.class) == false) {   //모니터링 서비스 작동안함
            Log.i(TAG, "작동중이 아니면 service on");
            startService(new Intent(this, AppDetectService.class));          //모니터링 서비스 시작
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> imgUrls;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            imgUrls = new ArrayList<String>();             //아답터 생성시 리스트 생성
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


            return PlaceholderFragment.newInstance(imgUrls, position);
        }

        @Override
        public int getCount() {
            // 리스트에 아이템 추가후 새로고침하면 뷰페이저의 갯수도 자동으로 증가~!
            return imgUrls == null ? 0 : imgUrls.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "SECTON " + position;
        }

        public void addItem(String type) {
            imgUrls.add(type);                //아이템 목록에 추가
            notifyDataSetChanged();        //아답터에 데이터 변경되었다고 알림. 알아서 새로고침
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        ImageView imgSec;
        ImageView imgBack;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(ArrayList<String> imgUrls, int sectionNumber) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putStringArrayList("imgUrls", imgUrls);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lock, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            imgSec = (ImageView) rootView.findViewById(R.id.section_image);
            imgBack = (ImageView) rootView.findViewById(R.id.background_image);
            ArrayList<String> imgUrls = getArguments().getStringArrayList("imgUrls");
            for (int i = 0; i < imgUrls.size(); i++) {
                Log.i("", imgUrls.get(i));

            }
            Glide
                    .with(getContext()) // could be an issue!
                            //  .load(imgUrls.get(getArguments().getInt(ARG_SECTION_NUMBER)))
                    .load(R.drawable.test)
                    .bitmapTransform(new BlurTransformation(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into( imgBack );

            Glide
                    .with(getContext()) // could be an issue!
                  //  .load(imgUrls.get(getArguments().getInt(ARG_SECTION_NUMBER)))
                    .load("")
                    .placeholder(R.drawable.test)
                    .fitCenter()
                    .bitmapTransform(new BlurTransformation(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into( target );


           /* Glide
                    .with( getContext() ) // could be an issue!
                    .load( eatFoodyImages[0] )
                    .asBitmap()
                    .into( target );*/

          /*  Picasso.with(getContext())
                    .load(imgUrls.get(getArguments().getInt(ARG_SECTION_NUMBER)))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
            *//* Save the bitmap or do something with it here *//*

                            //Set it in the ImageView
                            imgSec.setImageBitmap(bitmap);
                           imgBack.setImageBitmap(blur(getContext(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
*/
        /*    Picasso
                    .with(getContext())
                    .load(imgUrls.get(getArguments().getInt(ARG_SECTION_NUMBER)))
                    .into(imgSec, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {

                        }
                    });*/
            return rootView;
        }
        private SimpleTarget target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                Log.i("hwoh", "onResourceReady");
                imgSec.setImageBitmap( bitmap );
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Log.i("hwoh", "onLoadFailed");
                imgSec.setImageDrawable(getResources().getDrawable(R.drawable.test));


            }
        };


        private static final float BITMAP_SCALE = 0.4f;
        private static final float BLUR_RADIUS = 25;

        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    //블러효과
    private static final float BLUR_RADIUS = 25f;


    @Override
    public void onTrigger(View v, int target) {
        switch (target) {
            case 0:
                Toast.makeText(this, "download selected", Toast.LENGTH_SHORT).show();

                break;

            case 1:
                Toast.makeText(this, "lockOpen selected", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                // Code should never reach here.
        }
    }

}