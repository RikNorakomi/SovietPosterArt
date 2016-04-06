package sovietPosterArt;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.ScreenUtils;
import sovietPosterArt.utils.ScrimUtil;
import sovietPosterArt.utils.TypefaceUtil;

/**
 * Created by Rik van Velzen on 27-1-2016.
 * This class makes use of Dave Morrissey's subsampling scale image view.
 * More info on this custom view's usage can be found at: https://github.com/davemorrissey/subsampling-scale-image-view
 */
public class ArtWorkDetailViewActivity extends GenericActivity {

    private final String TAG = getClass().getSimpleName();

    private PopupMenu mOverflowMenu;
    private boolean mShowUI;
    private Bitmap mArtWorkBitmap;
    private String mImageUrl;

    @Bind(R.id.container)
    FrameLayout mContainerView;
    @Bind(R.id.back_button)
    ImageButton mBackButton;
    @Bind(R.id.overflow_button)
    ImageButton mOverflowButton;

    @Bind(R.id.imageView)
    SubsamplingScaleImageView imageZoomView;
    @Bind(R.id.art_work_info_container)
    LinearLayout mArtWorkInfoContainer;
    @Bind(R.id.status_bar_scrim)
    View mStatusBarScrimView;
    private Poster mArtWork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.art_work_detail);

        ButterKnife.bind(this);

        mBackButton.setOnClickListener(v -> v.postDelayed(this::onBackPressed, 200)); // adds a 200ms sec delay to show ripple effect on clicking back button
        setOverflowMenu();


        mShowUI = true;
        mArtWork = (Poster) getIntent().getSerializableExtra(Constants.ART_WORK_OBJECT);
        mImageUrl = mArtWork.getImageUrl();

        mContainerView.setOnClickListener(v -> {
            mShowUI = !mShowUI;
            showHideUiOverlay(mShowUI);
            App.log(TAG, "setting mShowUI to: " + mShowUI);
        });
//        mArtWorkInfoContainer.setPadding(0, 0, 0, ScreenUtils.getNavigationBarHeightPx());

//        RequestListener<? super String, Bitmap> imageLoadedListener = new RequestListener<String, Bitmap>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                App.log(TAG, "onResourceReady");
//                final Bitmap bitmap = resource;
//                float imageScale = (float) imageZoomView.getHeight() / (float) bitmap.getHeight();
//                float twentyFourDip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
//                        getResources().getDisplayMetrics());
//
//                /** When image is loaded, generate a Palette */
//                Palette.from(bitmap)
//                        .maximumColorCount(3)
//                        .clearFilters()
//                        .setRegion(0, 0, bitmap.getWidth(), (int) (twentyFourDip * 2)) // todo improve palette read out for determining back button color
//                        .generate(new Palette.PaletteAsyncListener() {
//                            @Override
//                            public void onGenerated(Palette palette) {
//                                boolean isDark;
//                                @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
//                                if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
//                                    isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
//                                } else {
//                                    isDark = lightness == ColorUtils.IS_DARK;
//                                }
//                                App.log(TAG, "Palette.onGenerated; isDark = " + isDark);
//
//                                if (!isDark) { // make back icon dark on light images
//                                    App.log(TAG, "setting color filter");
//                                    // todo keeping back icon white for now
////                                    mBackButton.setColorFilter(ContextCompat.getColor(
////                                            ArtWorkDetailViewActivity.this, R.color.dark_icon));
//                                }
//
//                                // possible animation for activity switch
//                                Interpolator interpolator = AnimationUtils.loadInterpolator(
//                                        ArtWorkDetailViewActivity.this, android.R.interpolator.fast_out_slow_in);
//                                mBackButton.animate()
//                                        .alpha(1f)
//                                        .setDuration(1200)
//                                        .setInterpolator(interpolator)
//                                        .start();
//                            }
//                        });
//
//                return false;
//            }
//        };

        Glide.with(this)
                .load(mImageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // todo: look into cache starts
//                .listener(imageLoadedListener) // inserted for getting a palette
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        /** When image is loaded set it to the imageZoomView either upscale it, preserving aspect ratio, when there is whitespace
                         *  or downscale it so that either width or length of image fits screen size */
                        mArtWorkBitmap = bitmap;
                        imageZoomView.setImage(ImageSource.bitmap(mArtWorkBitmap));
                        imageZoomView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

                        float scale = (float) (ScreenUtils.getScreenHeightPx() + ScreenUtils.getNavigationBarHeightPx()) / (float) bitmap.getHeight();
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

        /** Touch event handling:
         *  A GestureDetector(GD) is created to handle more advanced touch event detection
         *  A TouchListener is then set to the ImageView which calls the GD's onTouchEvent() */
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageZoomView.isReady()) {
//                    PointF sCoord = imageZoomView.viewToSourceCoord(e.getX(), e.getY());
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


//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            mStatusBarScrimView.setVisibility(View.GONE);
//        } else {
//            mStatusBarScrimView.setBackground(ScrimUtil.makeCubicGradientScrimDrawable(
//                    0x44000000, 8, Gravity.TOP));
//        }

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
                                .withEndAction(() -> App.log(TAG, "no EndACtion in animation"));
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


//                        if (mStatusBarScrimView != null) {
//                            mStatusBarScrimView.setVisibility(
//                                    showArtDetailChrome ? View.VISIBLE : View.GONE);
//                            mStatusBarScrimView.animate()
//                                    .alpha(visible ? 1f : 0f)
//                                    .setDuration(200)
//                                    .withEndAction(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (!visible) {
//                                                mStatusBarScrimView.setVisibility(View.GONE);
//                                            }
//                                        }
//                                    });
//                        }
                });
    }

    private void setOverflowMenu() {
        mOverflowMenu = new PopupMenu(this, mOverflowButton);
        mOverflowMenu.getMenu().clear();
        mOverflowMenu.inflate(R.menu.overflow_menu);

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
            }
            return false;
        });
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
