package sovietPosterArt.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import sovietPosterArt.ArtWorkDetailViewActivity;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private String[] urlArray;
    private Activity mParentActivity = null;

    public RecyclerAdapter(Activity parentACtivity) {
        mParentActivity = parentACtivity;

        String[] url = {
                "http://sovietart.me/img/posters/600px/0200.jpg",
                "http://sovietart.me/img/posters/600px/0196.jpg",
                "http://sovietart.me/img/posters/600px/0215.jpg",
                "http://sovietart.me/img/posters/600px/0333.jpg",
                "http://sovietart.me/img/posters/600px/0414.jpg",
                "http://sovietart.me/img/posters/600px/0412.jpg",
                "http://sovietart.me/img/posters/600px/0411.jpg",
                "http://sovietart.me/img/posters/600px/0403.jpg",
                "http://sovietart.me/img/posters/600px/0388.jpg",
                "http://sovietart.me/img/posters/600px/0323.jpg",
                "http://sovietart.me/img/posters/600px/0327.jpg",
                "http://sovietart.me/img/posters/600px/0357.jpg",
        };

        urlArray = new String[getItemCount()];
        Random rand = new Random();
        for (int i = 0; i < urlArray.length; i++) {
//            int randomIndexNumber = new Random().nextInt(url.length);

            int randomNum = rand.nextInt((414 - 96) + 1) + 96;
            String randomUrl = "http://sovietart.me/img/posters/600px/0" + randomNum + ".jpg";

            urlArray[i] = randomUrl;
//            Log.e("url=", url[randomIndexNumber]);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_item, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TextView tv = holder.artWorkDummyText;
        tv.setText("view #" + (position + 1));

        Log.e("Glide trying to load: ", urlArray[position]);
        Glide.with(mParentActivity)
                .load(urlArray[position])
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e("GLide load exception: ", e.toString() == null ? "exception is Null!" : e.toString());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.e("GLide onResourceReady: ", "");
                        // when image is loaded remove artWorkDummyText
                        holder.artWorkDummyText.setVisibility(View.GONE);
                        holder.artWorkImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.artWorkImage);

    }

    @Override
    public int getItemCount() {
        final int fixedDummyViews = 30;
        return fixedDummyViews;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.recyler_dummy_textView)
        TextView artWorkDummyText;
        @Bind(R.id.recycler_image_view)
        ImageView artWorkImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            artWorkImage.setOnClickListener(view -> {
                App.log(TAG, urlArray[getLayoutPosition()]);

                artWorkImage.setTransitionName(Constants.ART_WORK_GALLERY);
                Intent intent = new Intent(mParentActivity, ArtWorkDetailViewActivity.class);
                intent.putExtra(Constants.ART_WORK_URL, urlArray[getLayoutPosition()]);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(mParentActivity,
                                Pair.create(view, Constants.ART_WORK_GALLERY),
                                Pair.create(view, "artWorkDetail"));
                mParentActivity.startActivity(intent, options.toBundle());
            });
        }
    }
}
