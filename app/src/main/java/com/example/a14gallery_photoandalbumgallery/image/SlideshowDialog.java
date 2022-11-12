package com.example.a14gallery_photoandalbumgallery.image;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.google.android.material.button.MaterialButton;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;
import java.util.Objects;

public class SlideshowDialog extends DialogFragment {
    private SliderView sliderView;
    private MaterialButton confirmBtn;
    private List<Image> dataSlideshow;

    public SlideshowDialog(List<Image> dataSlideshow) {
        this.dataSlideshow = dataSlideshow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.slideshow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sliderView = view.findViewById(R.id.imageSlider);
        SlideShowAdapter ssAdapter = new SlideShowAdapter(dataSlideshow);
        sliderView.setSliderAdapter(ssAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.setScrollTimeInSec(3);
        sliderView.startAutoCycle();
    }
}
