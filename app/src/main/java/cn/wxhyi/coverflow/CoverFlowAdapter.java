package cn.wxhyi.coverflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yichao on 16/2/25.
 */
public class CoverFlowAdapter extends RecyclerView.Adapter<CoverFlowAdapter.ViewHolder> {

    private static String TAG = "CoverFlowAdapter";

    private LinkedList<CardModel> cardModels;
    private LinkedList<String> list;


    private String[] mImageUrls;
    private int border_position = 0;

    private ImageLoader loader;
    private DisplayImageOptions displayImageOptions;

    public CoverFlowAdapter(LinkedList<String> list, Context context){
        this.list = list;
        Log.d("zhangyi" , "list size is"+list.size());
        loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(context));
        displayImageOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    @Override
    public CoverFlowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.card_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.card_layout.setVisibility(View.VISIBLE);
        showPic(holder.card_image,"file://"+list.get(position));

        if (position < border_position || position > getItemCount() - border_position - 1){
            holder.card_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout card_layout;
        public ImageView card_image;
        public TextView card_title;

        public ViewHolder(View v) {
            super(v);
            card_layout = (LinearLayout) v.findViewById(R.id.card_layout);
            card_image = (ImageView) v.findViewById(R.id.card_img);
            card_title = (TextView) v.findViewById(R.id.card_title);
        }
    }

    public void setBorder_position(int border_position) {
        Log.d("zhangyi" , "border_position is:"+border_position);
        this.border_position = border_position;
        for (int i  = 0; i < border_position; i++){
                list.addFirst(list.get(i));
                list.addLast(list.get(i));
        }
        notifyDataSetChanged();
    }

    public int getBorder_position(){
        return border_position;
    }

    private void showPic(ImageView imgView, String url) {
        if (url == null) {
            imgView.setVisibility(View.GONE);
        } else {
            if (imgView != null){
                loader.displayImage(url, imgView, displayImageOptions);
            }
        }
    }
}
