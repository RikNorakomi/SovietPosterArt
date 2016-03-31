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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import sovietPosterArt.ArtWorkDetailViewActivity;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.Constants;

public class ArtFeedAdapter extends RecyclerView.Adapter<ArtFeedAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private Activity mParentActivity = null;
    private ArrayList<Poster> mPosters = new ArrayList<>();

    public ArtFeedAdapter(Activity parentActivity) {
        mParentActivity = parentActivity;
    }

    public void setArtWorkCollection(ArrayList<Poster> data) {
//        App.log(TAG, "setting ArtWorkCollection with postersize = " + data.size());
        mPosters.clear();
        mPosters.addAll(data);
        notifyDataSetChanged();
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

        //todo: implement abstraction layer

        if (mPosters.isEmpty())
            return;

//        App.log(TAG, "pre glide: poster url = " + mPosters.get(position).getImageUrl() + " fileath:" + mPosters.get(position).getFilepath());

        Glide.with(mParentActivity)
                .load(mPosters.get(position).getImageUrl())
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
        return mPosters.size();
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
                artWorkImage.setTransitionName(Constants.ART_WORK_GALLERY);
                Intent intent = new Intent(mParentActivity, ArtWorkDetailViewActivity.class);
                intent.putExtra(Constants.ART_WORK_OBJECT, mPosters.get(getLayoutPosition()));
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
