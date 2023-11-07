package com.example.monopoly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class avatarFragment extends Fragment {
    protected final ActivityResultHelper<Intent, ActivityResult> activityLauncher = ActivityResultHelper.registerActivityForResult(this);
    ImageButton bullavatar, lionavatar, foxavatar, pandaavatar,takepic, gallery;
    private OnImageSelectedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_avatar, container, false);
        bullavatar=rootView.findViewById(R.id.bullavatar);
        foxavatar=rootView.findViewById(R.id.foxavatar);
        lionavatar=rootView.findViewById(R.id.lionavatar);
        pandaavatar=rootView.findViewById(R.id.pandaavatar);
        takepic=rootView.findViewById(R.id.takepic);
        gallery=rootView.findViewById(R.id.gallery);
        bullavatar.setOnClickListener(v -> sendImageId(R.drawable.bullavatar));
        lionavatar.setOnClickListener(v -> sendImageId(R.drawable.lionavatar));
        foxavatar.setOnClickListener(v -> sendImageId(R.drawable.foxavatar));
        pandaavatar.setOnClickListener(v -> sendImageId(R.drawable.pandaavatar));
        takepic.setOnClickListener(v -> launchImagePicker(1));
        gallery.setOnClickListener(v -> launchImagePicker(2));
        // Inflate the layout for this fragment
        return rootView;
    }
    private void launchImagePicker(int num) {
        if(num==1){
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityLauncher.launch(cameraIntent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (mListener != null) {
                            mListener.onImageSelected(bitmap);
                        }
                    }
                }
                else {
                    Toast.makeText(getContext(), "The picture canceled", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(num==2){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Intent chooserIntent = Intent.createChooser(intent, "Select Picture");
            activityLauncher.launch(chooserIntent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        if (mListener  != null) {
                            Bitmap bitmap = null;
                            try {
                                //get bitmap from uri process
                                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mListener.onImageSelected(bitmap);
                        }
                    }
                }
                else {
                    Toast.makeText(getContext(), "The image canceled", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public interface OnImageSelectedListener {
        //for delivered data for example image from fragment back to activity we must use interface
        void onImageSelected(Bitmap imageBitmap);
    }
    private void sendImageId(int imageId) {
        if (mListener != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId);
            mListener.onImageSelected(bitmap);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented If not it throws an exception
        try {
            mListener = (OnImageSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageButtonClickListener");
        }
    }
}