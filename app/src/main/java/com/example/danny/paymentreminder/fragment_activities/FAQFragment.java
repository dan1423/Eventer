package com.example.danny.paymentreminder.fragment_activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.danny.paymentreminder.R;

public class FAQFragment extends Fragment{

    private View faqView;

    public FAQFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        faqView = inflater.inflate(R.layout.lay_for_faq_fragment,null,false);

        TextView link = (TextView) faqView.findViewById(R.id.txt_faq_1);
        String linkText = "<a href='http://stackoverflow.com'>"+link.getText().toString()+"</a>";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        return faqView;
    }
}
