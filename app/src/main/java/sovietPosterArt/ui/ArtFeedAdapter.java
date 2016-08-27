package sovietPosterArt.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import sovietPosterArt.ArtWorkDetailViewActivity;
import sovietPosterArt.MainActivity;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.JavaUtils;

public class ArtFeedAdapter extends RecyclerView.Adapter<ArtFeedAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private boolean fullArtWorkCollectionShown = true;

    private Activity mParentActivity = null;
    private ArrayList<Poster> mPosters = new ArrayList<>();
    private ArrayList<Poster> mFullArtWorkCollection = new ArrayList<>();

    public ArtFeedAdapter(Activity parentActivity) {
        mParentActivity = parentActivity;
    }

    public void setArtWorkCollection(ArrayList<Poster> artWorkCollection) {
        // randomizing collection to not always have same art work at start of overview when app starts
        JavaUtils.randomizeArrayList(artWorkCollection);

        this.mFullArtWorkCollection.addAll(artWorkCollection);
        mPosters.clear();
        mPosters.addAll(artWorkCollection);
        fullArtWorkCollectionShown = true;
        notifyDataSetChanged();
    }

    public void setQueryResult (List<Poster> artWorkCollection){
        mPosters.clear();
        mPosters.addAll(artWorkCollection);
        fullArtWorkCollectionShown = false;
        notifyDataSetChanged();
    }

    public void resetBackToFullCollection (){
        mPosters.clear();
        mPosters.addAll(mFullArtWorkCollection);
        fullArtWorkCollectionShown = true;
        notifyDataSetChanged();
    }

    public void clearCollection(){
        mPosters.clear();
        notifyDataSetChanged();
    }

    public boolean isFullArtWorkCollectionShown(){
        return fullArtWorkCollectionShown;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //todo: implement abstraction layer

        if (mPosters.isEmpty())
            return;

        App.log(TAG, "pre glide: poster url = " + mPosters.get(position).getImageUrl() + " fileath:" + mPosters.get(position).getFilepath());

        Glide.with(mParentActivity)
                .load(mPosters.get(position).getImageUrl())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide load exception: ");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.e("GLide onResourceReady: ", "");
                        // when image is loaded remove artWorkDummyText
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

    public Poster getRandomPoster() {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(mFullArtWorkCollection.size());
        Poster poster  = mFullArtWorkCollection.get(index);
        return poster;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.recycler_image_view)
        ImageView artWorkImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            artWorkImage.setOnClickListener(view -> {
                if (((MainActivity) mParentActivity).isFabMenuOpened()) {
                    App.log(TAG, "fabMenu opened. not selecting art item");
                    return;
                }

                Intent intent = new Intent(mParentActivity, ArtWorkDetailViewActivity.class);
                intent.putExtra(Constants.ART_WORK_OBJECT, mPosters.get(getLayoutPosition()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (Build.VERSION.SDK_INT >= 21) {
                    artWorkImage.setTransitionName(Constants.ART_WORK_GALLERY);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mParentActivity,
                            Pair.create(view, Constants.ART_WORK_GALLERY),
                            Pair.create(view, "artWorkDetail"));
                    mParentActivity.startActivity(intent, options.toBundle());
                } else {
                    mParentActivity.startActivity(intent);
                }

            });
        }
    }
}
