package cn.wxhyi.coverflow;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yichao on 16/2/20.
 */
public class MainActivity extends Activity implements CoverFlowView.CoverFlowItemListener{

    private static final String TAG = "MainActivity";

    private CoverFlowView coverFlowView;
    private CoverFlowAdapter coverFlowAdapter;
    private TextView text;
    private LinkedList<CardModel> cardModels;
    private String[] imageUrls;
    private ImageSwitcher mImageswitcher;
    static final List<Bitmap> mImagesWicherList = Collections.synchronizedList(new LinkedList<Bitmap>());

    private int switcherIndex = 0;
    private int currentPosition = 0;
    private Map<String, Bitmap> bigImage = new HashMap<String, Bitmap>();
    private LinkedList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSwitchView();
        initCoverFlowData();
    }


    private void initSwitchView() {
        mImageswitcher = (ImageSwitcher) findViewById(R.id.imageswitcher);
        getImgPathList();
        setSwitchView();

    }

    private void setSwitchView() {
        mImageswitcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                Log.d(TAG, "+++++++makeView++++++++++");
                ImageView imageView = new ImageView(getApplicationContext());
                if (list.size() == 0) {
                    Toast.makeText(getApplicationContext(), "NO PICTURES", Toast.LENGTH_SHORT).show();
                }
                imageView.setBackgroundColor(0xFF000000);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

                // setup init pictures
                try {
                    if (list.size() != 0) {
                        String bitmapUrl = list.get(currentPosition);
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        opts.inTempStorage = new byte[100 * 1024];
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;
                        opts.inPurgeable = true;
                        opts.inSampleSize = 4;
                        opts.inInputShareable = true;
                        opts.inJustDecodeBounds = false;
                        Bitmap bm = BitmapFactory.decodeFile(bitmapUrl, opts);
                        imageView.setImageBitmap(bm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mImagesWicherList.size() != 0) {
                    Log.i(TAG, "imageswicher.size" + mImagesWicherList.size());
                }
                return imageView;
            }
        });

        // mImageswitcher.setOnTouchListener(this);

        mImageswitcher.setOnTouchListener(new View.OnTouchListener() {

            public float lastX;
            public float downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        lastX = event.getX();
                        if (lastX > downX) {
                            if (currentPosition > 0) {
                                //mImageswitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));
                                //mImageswitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));

                                currentPosition--;
                                // mImageswitcher.setImageResource(imgIds[currentPosition % imgIds.length]);
                                Log.d(TAG, "currentPosition in switch list : " + currentPosition);
                                Bitmap bitmap = getCurrentBitmap(currentPosition);//imageswicher.get(currentPosition);
                                BitmapDrawable dra = new BitmapDrawable(getResources(), bitmap);
                                mImageswitcher.setImageDrawable(dra);
                                try {
                                    coverFlowView.scrollToCenter(currentPosition);
                                }catch (Exception ex){
                                }


                                // setup current picture in gallery
                                // =========================
                                if (coverFlowView.getCurrentPosition() == (coverFlowView.getChildCount() - 1)) {
                                    switcherIndex = coverFlowView.getChildCount() - 1;
                                } else {
                                    switcherIndex = coverFlowView.getCurrentPosition() - 1;
                                }
                                // ==========================
                            }
                        } else if (lastX < downX) {
                            if (currentPosition < list.size() - 1) {
                                //mImageswitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));
                                //mImageswitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.lift_out));
                                if(currentPosition == 0){
                                    currentPosition += coverFlowAdapter.getBorder_position()+1;
                                }else {
                                    currentPosition++;
                                }
                                Log.d(TAG, "currentPosition in switch list : " + currentPosition);
                                Bitmap bitmap = getCurrentBitmap(currentPosition);
                                BitmapDrawable dra = new BitmapDrawable(getResources(), bitmap);
                                mImageswitcher.setImageDrawable(dra);
                                try {
                                    coverFlowView.scrollToCenter(currentPosition);
                                }catch (Exception ex){
                                }

                                // setup current picture in gallery
                                // =========================
                                if (coverFlowView.getCurrentPosition() == (coverFlowView.getChildCount() - 1)) {
                                    switcherIndex = 0;
                                } else {
                                    switcherIndex = coverFlowView.getCurrentPosition() + 1;
                                }
                                // =================================
                            }
                        } else {
                            Log.d(TAG, "currentPosition : " + currentPosition + " will hold on");
                        }

                        // if(lastX-downX>100) {
                        //
                        // if(galleryView.getSelectedItemPosition()==0){
                        // switcherIndex = galleryView.getCount()-1;
                        // } else{
                        // switcherIndex=galleryView.getSelectedItemPosition()-1;
                        // }
                        // }
                        // else if(downX-lastX>100){
                        //
                        // if(galleryView.getSelectedItemPosition()==(galleryView.getCount()-1)){
                        // switcherIndex=0;
                        // } else{
                        // switcherIndex=galleryView.getSelectedItemPosition()+1;
                        // }
                        // }
                        // auto trigger ImageSwitcher:setOnItemSelectedListener
                        Log.d(TAG, "gallery switcherIndex is : " + switcherIndex);
                        //galleryView.setSelection(switcherIndex, true);
                    }
                    break;
                }
                return true;
            }
        });
    }


    private void initCoverFlowData() {
        coverFlowView = (CoverFlowView) this.findViewById(R.id.cover_flow);
        text = (TextView) this.findViewById(R.id.text);

        //vertical overlap list iew
        coverFlowView.setOrientation(CoverFlowView.HORIZONTAL);
        coverFlowAdapter = new CoverFlowAdapter(list, this);
        coverFlowView.setAdapter(coverFlowAdapter);
        coverFlowView.setCoverFlowListener(this);
        coverFlowView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "onItemClick:position:" + position);
                coverFlowView.scrollToCenter(position);

                if (list.size() > 0) {
                    //Bitmap bitmap = bigImage.get(imageUrls[position]);//imageswicher.get(position);
                    //BitmapDrawable dra = new BitmapDrawable(getResources(), bitmap);
                    Bitmap bitmap = getCurrentBitmap(position);
                    BitmapDrawable dra = new BitmapDrawable(getResources(), bitmap);
                    mImageswitcher.setImageDrawable(dra);
                    //mImageswitcher.setImageDrawable();
                }

                Log.d(TAG, "touch thumbnail: " + position);
                currentPosition = position;

            }
        }));

        coverFlowView.getLayoutManager().scrollToPosition(0);
        coverFlowAdapter.notifyDataSetChanged();
    }

    private Bitmap getCurrentBitmap(int position) {
        String bitmapUrl = list.get(position);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inTempStorage = new byte[100 * 1024];
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inSampleSize = 4;
        opts.inInputShareable = true;
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapUrl, opts);
        return bitmap;
    }

    /**
     * Scan the picture in sdcard
     *
     * @return ArrayList<String> list
     */
    private LinkedList<String> getImgPathList() {
        list = new LinkedList<String>();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "_id", "_data" }, null, null, null);
        while (cursor.moveToNext()) {
            list.add(0, cursor.getString(1));
            Log.i("cursorItemUrl", cursor.getString(1) + "");
        }
        cursor.close();
        return list;
    }
    
    @Override
    public void onItemChanged(int position) {
        //do something you want
//        Log.i(TAG, "onItemChanged" + position);
    }

    @Override
    public void onItemSelected(int position) {
        //do something you want
//        Log.i(TAG, "onItemSelected" + position);
        //text.setText(cardModels.get(position).getTitle());
    }
}
