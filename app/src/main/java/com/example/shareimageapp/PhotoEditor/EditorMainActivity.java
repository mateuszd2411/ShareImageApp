package com.example.shareimageapp.PhotoEditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.shareimageapp.PhotoEditor.Adapter.ViewPagerAdapter;
import com.example.shareimageapp.PhotoEditor.Interface.EditImageFragmentListener;
import com.example.shareimageapp.PhotoEditor.Interface.FiltersListFragmentListener;
import com.example.shareimageapp.PhotoEditor.Utils.BitmapUtils;
import com.example.shareimageapp.R;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

public class EditorMainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    public static final String pictureName = "flash.jpg";

    public static final int PERMISSION_PICK_IMAGE = 1000;

    ImageView img_preview;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FilterListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    //Load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_main);
//        Toolbar toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Instagram Filter");

        //view
        img_preview = (ImageView) findViewById(R.id.image_preview);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preview.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FilterListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if (editImageFragment != null)
            editImageFragment.resetControls();
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }
}