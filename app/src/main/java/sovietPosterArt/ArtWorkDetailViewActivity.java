package sovietPosterArt;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.ColorUtils;
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
    private boolean mShowUI;
    private boolean mGestureFlagSystemUiBecameVisible;

    @Bind(R.id.container)
    FrameLayout mContainerView;
    @Bind(R.id.back_button)
    ImageButton mBackButton;
    @Bind(R.id.imageView)
    SubsamplingScaleImageView imageView;
    @Bind(R.id.art_work_info_container)
    LinearLayout mArtWorkInfoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.art_work_detail);
        ScreenUtils.hideStatusBar(this);
        ScreenUtils.hideNavigationBar(this);

        ButterKnife.bind(this);

        mBackButton.setOnClickListener(v -> v.postDelayed(this::onBackPressed, 200)); // adds a 200ms sec delay to show ripple effect on clicking back button

        mShowUI = true;
        String imageUrl = getIntent().getStringExtra(Constants.ART_WORK_URL);

        mContainerView.setOnClickListener(v -> {
            mShowUI = !mShowUI;
            showHideChrome(mShowUI);
            App.log(TAG, "setting mShowUI to: " + mShowUI);
        });
//        mArtWorkInfoContainer.setPadding(0, 0, 0, ScreenUtils.getNavigationBarHeightPx());

        RequestListener<? super String, Bitmap> imageLoadedListener = new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                App.log(TAG, "onResourceReady");
                final Bitmap bitmap = resource;
                float imageScale = (float) imageView.getHeight() / (float) bitmap.getHeight();
                float twentyFourDip = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics());

                /** When image is loaded, generate a Palette */
                Palette.from(bitmap)
                        .maximumColorCount(3)
                        .clearFilters()
                        .setRegion(0, 0, bitmap.getWidth(), (int) (twentyFourDip * 2)) // todo improve palette read out for determining back button color
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                boolean isDark;
                                @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                                if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                    isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                                } else {
                                    isDark = lightness == ColorUtils.IS_DARK;
                                }
                                App.log(TAG, "Palette.onGenerated; isDark = " + isDark);

                                if (!isDark) { // make back icon dark on light images
                                    App.log(TAG, "setting color filter");
                                    // todo keeping back icon white for now
//                                    mBackButton.setColorFilter(ContextCompat.getColor(
//                                            ArtWorkDetailViewActivity.this, R.color.dark_icon));
                                }

                                // possible animation for activity switch
                                Interpolator interpolator = AnimationUtils.loadInterpolator(
                                        ArtWorkDetailViewActivity.this, android.R.interpolator.fast_out_slow_in);
                                mBackButton.animate()
                                        .alpha(1f)
                                        .setDuration(1200)
                                        .setInterpolator(interpolator)
                                        .start();

                                // color the status bar. Set a complementary dark color on L,
                                // light or dark color on M (with matching status bar icons)
//                                int statusBarColor = getWindow().getStatusBarColor();
//                                Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);
//                                if (topColor != null &&
//                                        (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
//                                    statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
//                                            isDark, SCRIM_ADJUSTMENT);
//                                    // set a light status bar on M+
//                                    if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                        ViewUtils.setLightStatusBar(imageView);
//                                    }
//                                }
//
//                                if (statusBarColor != getWindow().getStatusBarColor()) {
////                                imageView.setScrimColor(statusBarColor);
//                                    ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(getWindow
//                                            ().getStatusBarColor(), statusBarColor);
//                                    statusBarColorAnim.addUpdateListener(animation -> getWindow().setStatusBarColor((int) animation
//                                            .getAnimatedValue()));
//                                    statusBarColorAnim.setDuration(1000);
//                                    statusBarColorAnim.setInterpolator(AnimationUtils
//                                            .loadInterpolator(ArtWorkDetailViewActivity.this, android.R
//                                                    .interpolator.fast_out_slow_in));
//                                    statusBarColorAnim.start();
//                                }
                            }
                        });

                return false;
            }
        };
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // todo: look into cache starts
                .listener(imageLoadedListener)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        /** When image is loaded set it to the imageView either upscale it, preserving aspect ratio, when there is whitespace
                         *  or downscale it so that either width or length of image fits screen size */
                        imageView.setImage(ImageSource.bitmap(bitmap));
                        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);

                        float scale = (float) (ScreenUtils.getScreenHeightPx() + ScreenUtils.getNavigationBarHeightPx()) / (float) bitmap.getHeight();
                        imageView.setMinScale(scale);
                        PointF pf = new PointF(0.5f, 0.5f);
                        imageView.setScaleAndCenter(scale, pf);
                    }
                });

        /** Touch event handling:
         *  A GestureDetector(GD) is created to handle more advanced touch event detection
         *  A TouchListener is then set to the ImageView which calls the GD's onTouchEvent() */
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (imageView.isReady()) {
//                    PointF sCoord = imageView.viewToSourceCoord(e.getX(), e.getY());
                    mShowUI = !mShowUI;
                    showHideChrome(mShowUI);
                    App.log(TAG, "setting mShowUI to: " + mShowUI);
                }
                return true;
            }
        });

        imageView.setOnTouchListener((view, motionEvent) -> gestureDetector.onTouchEvent(motionEvent));

        // Zoom settings
        float maxZoom = 10f;
        imageView.setMaxScale(maxZoom);
        imageView.setDoubleTapZoomScale(maxZoom);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);


        // Set font and Info Text
        String titleFont = "AlegreyaSans-Black.ttf";
        String bylineFont = "AlegreyaSans-Medium.ttf";

        titleFont = "Alegreya-BlackItalic.ttf";
        bylineFont = "Alegreya-Italic.ttf";
        TextView mTitleView = (TextView) findViewById(R.id.title);
        TextView mBylineView = (TextView) findViewById(R.id.artist);

        mTitleView.setTypeface(TypefaceUtil.getAndCache(titleFont));
        mTitleView.setText("TITLE should come here!");

        mBylineView.setTypeface(TypefaceUtil.getAndCache(bylineFont));
        mBylineView.setText("Artist info should go here!");


        // setup status and navigation bar scrim: 0xaaffff00 = bright yellow
//        ScreenUtils.setMargins(mArtWorkInfoContainer, 0, 0, 0, ScreenUtils.getNavigationBarHeightPx());
        mArtWorkInfoContainer.setBackground(ScrimUtil.makeCubicGradientScrimDrawable(
                0x99000000, 8, Gravity.BOTTOM)); // original value = 0xaa000000
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mArtWorkInfoContainer.getLayoutParams();
//        layoutParams.setMargins(0,0,0,ScreenUtils.getNavigationBarHeightPx());

//        mArtWorkInfoContainer.setPadding(0, 0, 0, ScreenUtils.getNavigationBarHeightPx()+2);
//
        View mStatusBarScrimView = findViewById(R.id.status_bar_scrim);
//        ScreenUtils.setMargins(mStatusBarScrimView, 0, ScreenUtils.getStatusBarHeightPx(), 0, 0);
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

                    final boolean visible = (vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0;
                    if (visible) {
                        mGestureFlagSystemUiBecameVisible = true;
                    }

                    mArtWorkInfoContainer.setVisibility(View.VISIBLE);

                    if (mShowUI) {
                        mArtWorkInfoContainer.animate()
                                .alpha(1f)
                                .translationY(mShowUI ? 1 : -metadataSlideDistance)
                                .setDuration(400)
                                .withEndAction(() -> App.log(TAG, "no EndACtion in animation"));
                    } else {
                        mArtWorkInfoContainer.animate()
                                .alpha(0f)
                                .translationY(metadataSlideDistance)
                                .setDuration(400)
                                .withEndAction(() -> {
                                    App.log(TAG, "setting end action view gone");
                                    mArtWorkInfoContainer.setVisibility(View.GONE);
                                });
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

    private void showHideChrome(boolean show) {
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
