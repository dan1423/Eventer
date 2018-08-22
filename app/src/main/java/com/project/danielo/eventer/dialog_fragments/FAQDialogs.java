package com.project.danielo.eventer.dialog_fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.project.danielo.eventer.R;

public class FAQDialogs extends Fragment {

    public FAQDialogs(){

    }

    private View faqDialogView;
    ImageView exitDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       faqDialogView = inflater.inflate(R.layout.layout_for_faq_2,null, false);
       exitDialog = (ImageView)faqDialogView.findViewById(R.id.img_exit_faq_dialog);
       exitDialog.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fm = getFragmentManager();
               fm.popBackStack();
           }
       });

       return faqDialogView;
    }
}
