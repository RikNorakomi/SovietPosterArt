package sovietPosterArt;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.sharing_artwork.ShareArtworkTask;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.AppPreferences;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.ScreenUtils;
import sovietPosterArt.utils.ScrimUtil;
import sovietPosterArt.utils.TypefaceUtil;

/**
 * Created by Rik van Velzen on 27-1-2016.
 * This class makes use of Dave Morrissey's subsampling scale image view.
 * More info on this custom view's usage can be found at: https://github.com/davemorrissey/subsampling-scale-image-view
 */
public class ArtWorkDetailViewActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private PopupMenu mOverflowMenu;
    private boolean mShowUI;
    private Bitmap mArtWorkBitmap;
    private String mImageUrl;
    private String mHighResImageUrl;
    private Poster mArtWork;

    FrameLayout mContainerView;
    ImageButton mBackButton;
    ImageButton mOverflowButton;
    SubsamplingScaleImageView imageZoomView;
    LinearLayout mArtWorkInfoContainer;
    View mStatusBarScrimView;
    ProgressBar mLoadingSpinner;

    private float scale = 1;
    private boolean shouldDownloadHRImages;
    private boolean highResImageSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.art_work_detail);

        shouldDownloadHRImages = AppPreferences.sharedInstance().shouldDownloadHighResImages();
        App.logError(TAG, "shouldDownloadHRImages = " + shouldDownloadHRImages);

        mContainerView = (FrameLayout) findViewById(R.id.container);
        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mOverflowButton = (ImageButton) findViewById(R.id.overflow_button);
        imageZoomView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
        mArtWorkInfoContainer = (LinearLayout) findViewById(R.id.art_work_info_container);
        mStatusBarScrimView = findViewById(R.id.status_bar_scrim);
        mLoadingSpinner = (ProgressBar) findViewById(R.id.progressBar);

        mBackButton.setOnClickListener(v -> v.postDelayed(this::onBackPressed, 200)); // adds a 200ms sec delay to show ripple effect on clicking back button
        setOverflowMenu();

        mShowUI = true;
        mArtWork = (Poster) getIntent().getSerializableExtra(Constants.ART_WORK_OBJECT);
        mImageUrl = mArtWork.getImageUrl();
        mHighResImageUrl = mArtWork.getHighResImageUrl();


        mContainerView.setOnClickListener(v -> {
            mShowUI = !mShowUI;
            showHideUiOverlay(mShowUI);
            App.log(TAG, "setting mShowUI to: " + mShowUI);
        });

        mLoadingSpinner.setVisibility(View.VISIBLE);
        loadImageIntoView(mImageUrl);

        /** Touch event handling:
         *  A GestureDetector(GD) is created to handle more advanced touch event detection
         *  A TouchListener is then set to the ImageView which calls the GD's onTouchEvent() */
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageZoomView.isReady()) {
                    mShowUI = !mShowUI;
                    showHideUiOverlay(mShowUI);
                    App.log(TAG, "setting mShowUI to: " + mShowUI);
                }
                return true;
            }
        });

        imageZoomView.setOnTouchListener((view, motionEvent) -> gestureDetector.onTouchEvent(motionEvent));

        // Set font and Info Text
        String titleFont = "AlegreyaSans-Black.ttf";
        String bylineFont = "AlegreyaSans-Medium.ttf";

        titleFont = "Alegreya-BlackItalic.ttf";
        bylineFont = "Alegreya-Italic.ttf";
        TextView mTitleView = (TextView) findViewById(R.id.title);
        TextView mBylineView = (TextView) findViewById(R.id.artist);

        mTitleView.setTypeface(TypefaceUtil.getAndCache(titleFont));
        mTitleView.setText(mArtWork.getTitle());

        mBylineView.setTypeface(TypefaceUtil.getAndCache(bylineFont));

        String author = mArtWork.getAuthor();
        String year = mArtWork.getYear();
        String artistInfo = author + ", " + year;
        mBylineView.setText(artistInfo);

        // setup status and navigation bar scrim:
        mArtWorkInfoContainer.setBackground(ScrimUtil.makeCubicGradientScrimDrawable(
                0x99000000, 8, Gravity.BOTTOM)); // original value = 0xaa000000

        mStatusBarScrimView.setBackground(ScrimUtil.makeCubicGradientScrimDrawable(
                0x99000000, 8, Gravity.TOP));

        final float metadataSlideDistance = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());


        mContainerView.setOnSystemUiVisibilityChangeListener(
                vis -> {
                    mArtWorkInfoContainer.setVisibility(View.VISIBLE);
                    mBackButton.setVisibility(View.VISIBLE);

                    final int animationDuration = 400;

                    if (mShowUI) {
                        // move ui overlay into vision
                        mArtWorkInfoContainer.animate()
                                .alpha(1f)
                                .translationY(mShowUI ? 1 : -metadataSlideDistance)
                                .setDuration(animationDuration)
                                .withEndAction(() -> App.log(TAG, "no EndACtion in animation"));
                        mBackButton.animate()
                                .alpha(1f)
                                .translationY(mShowUI ? 1 : metadataSlideDistance)
                                .setDuration(animationDuration)
                                .withEndAction(() -> {
                                    App.log(TAG, "finish() EndACtion in animation");

                                });
                        mStatusBarScrimView.animate()
                                .alpha(1f)
                                .setDuration(animationDuration);

                    } else {
                        // move ui overlay out of vision
                        mArtWorkInfoContainer.animate()
                                .alpha(0f)
                                .translationY(metadataSlideDistance)
                                .setDuration(animationDuration)
                                .withEndAction(() -> {
                                    App.log(TAG, "setting end action view gone");
                                    mArtWorkInfoContainer.setVisibility(View.GONE);
                                });
                        mBackButton.animate()
                                .alpha(0f)
                                .translationY(-metadataSlideDistance)
                                .setDuration(animationDuration)
                                .withEndAction(() -> {
                                    App.log(TAG, "setting end action view gone");
                                    mBackButton.setVisibility(View.GONE);
                                });
                        mStatusBarScrimView.animate()
                                .alpha(0f)
                                .setDuration(animationDuration);

                    }
                });
    }

    public void loadImageIntoView(String imageUrl) {
        App.log(TAG, "in loadImageIntoView for url: " + imageUrl);
        /*
        * Load/set normal resolution image before highRes image has been loaded
        * */
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.RESULT) // todo: look into cache starts
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        App.log(TAG, "in onResourceReady normal resolution");
                        mLoadingSpinner.setVisibility(View.GONE);
                        if (highResImageSet) {
                            App.logError(TAG, "!!!highResImage already set on imageView. Nop need to set lowRes");
                            return;
                        }
                        /*
                        * Only set normal resolution image when highRes has not been loaded/cached yet.
                        * */
                        /** When image is loaded set it to the imageZoomView either upscale it, preserving aspect ratio, when there is whitespace
                         *  or downscale it so that either width or length of image fits screen size */
                        mArtWorkBitmap = bitmap;
                        imageZoomView.setImage(ImageSource.bitmap(mArtWorkBitmap));
                        imageZoomView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

                        scale = (float) (ScreenUtils.getScreenHeightPx() + ScreenUtils.getNavigationBarHeightPx()) / (float) bitmap.getHeight();
                        // todo adjust for when scale should be set according to width instead of height

                        // Zoom settings
                        float maxScaleFactor = 8f;
                        imageZoomView.setMinScale(scale);
                        imageZoomView.setMaxScale(maxScaleFactor * scale);
                        imageZoomView.setDoubleTapZoomScale(scale * 4);
                        imageZoomView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);

                        // todo centering view not working as desired
                        PointF pf = new PointF(0.5f, 0.5f);
                        imageZoomView.setScaleAndCenter(scale, pf);
                    }
                });

        if (shouldDownloadHRImages) {
            if (mHighResImageUrl == null || mHighResImageUrl.isEmpty()) {
                App.logError(TAG, "highResImage url issue: null or empty!");
                return;
            }
            Glide.with(this)
                    .load(mHighResImageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT) // todo: look into cache starts
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            App.log(TAG, "in onResourceReady high res resolution");
                            highResImageSet = true; // cancels potential setting of lower res image back on image view
                            mLoadingSpinner.setVisibility(View.GONE);

                        /*
                        * Only set normal resolution image when highRes has not been loaded/cached yet.
                        * */
                            /** When image is loaded set it to the imageZoomView either upscale it, preserving aspect ratio, when there is whitespace
                             *  or downscale it so that either width or length of image fits screen size */
                            mArtWorkBitmap = bitmap;
                            imageZoomView.setImage(ImageSource.bitmap(mArtWorkBitmap));
                            imageZoomView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

                            scale = (float) (ScreenUtils.getScreenHeightPx() + ScreenUtils.getNavigationBarHeightPx()) / (float) bitmap.getHeight();
                            // todo adjust for when scale should be set according to width instead of height

                            // Zoom settings
                            float maxScaleFactor = 8f;
                            imageZoomView.setMinScale(scale);
                            imageZoomView.setMaxScale(maxScaleFactor * scale);
                            imageZoomView.setDoubleTapZoomScale(scale * 4);
                            imageZoomView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);

                            // todo centering view not working as desired
                            PointF pf = new PointF(0.5f, 0.5f);
                            imageZoomView.setScaleAndCenter(scale, pf);
                        }
                    });
        }
    }

    private void setOverflowMenu() {
        mOverflowMenu = new PopupMenu(this, mOverflowButton);
        mOverflowMenu.getMenu().clear();
        mOverflowMenu.inflate(R.menu.overflow_menu);
        setMenuHighResItemTitle();

        mOverflowButton.setOnClickListener(v -> v.postDelayed(() -> {
            mOverflowMenu.show();
        }, 200));
        mOverflowMenu.setOnDismissListener(popupMenu -> {
            hideUiBars();
        });
        mOverflowMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_share_artwork:
                    new ShareArtworkTask(this, mArtWork);
                    return true;
                case R.id.action_enable_highres_loading:
                    App.logError(TAG, "in button click action_enable_highres_loading");
                    if (shouldDownloadHRImages) {
                        App.toast("Disabling downloading high resolution images");
                    } else {
                        App.toast("Enabling downloading high resolution images");
                    }

                    shouldDownloadHRImages = !shouldDownloadHRImages;
                    AppPreferences.sharedInstance().setDownloadHighResImages(shouldDownloadHRImages);
                    setMenuHighResItemTitle();


                    return true;
                default:
                    App.logError(TAG, "Couldn't retreive id for overflow menu clicked. Id= " + menuItem.getItemId());
            }
            return false;
        });
    }

    private void setMenuHighResItemTitle() {
        if (mOverflowMenu == null) {
            String errorMessage = "mOverflowMenu==null";
            App.logError(TAG, errorMessage);
            return;
        }
        MenuItem HRItem = mOverflowMenu.getMenu().findItem(R.id.action_enable_highres_loading);
        if (HRItem == null) {
            App.logError(TAG, "item is NULL!!!");
        } else {
            String title = shouldDownloadHRImages ? getString(R.string.menu_disable_loading_highres) : getString(R.string.menu_enable_loading_highres);
            HRItem.setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.log(TAG, "in onResume");
        hideUiBars();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mOverflowMenu.dismiss();
    }

    private void hideUiBars() {
        ScreenUtils.hideStatusBar(this);
        ScreenUtils.hideNavigationBar(this);
    }

    private void showHideUiOverlay(boolean show) {
        int flags = show ? 0 : View.SYSTEM_UI_FLAG_LOW_PROFILE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            if (!show) {
                flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
        }
        mContainerView.setSystemUiVisibility(flags);
    }
}
