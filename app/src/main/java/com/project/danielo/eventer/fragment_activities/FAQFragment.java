package com.project.danielo.eventer.fragment_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.danielo.eventer.R;
import com.project.danielo.eventer.dialog_fragments.EditEventDialogFragment;
import com.project.danielo.eventer.dialog_fragments.FAQDialogs;

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

        TextView faq1 = (TextView) faqView.findViewById(R.id.txt_faq_1);
        TextView faq2 = (TextView) faqView.findViewById(R.id.txt_faq_2);
        TextView faq3 = (TextView) faqView.findViewById(R.id.txt_faq_3);
        TextView faq4 = (TextView)faqView.findViewById(R.id.txt_faq_4);

        String linkText1 = "<a href='https://www.youtube.com/playlist?list=PLAFUd0aBwypvNOJ6NxyactNhu18q-98c0'>"+faq1.getText().toString()+"</a>";
        faq1.setText(Html.fromHtml(linkText1));
        faq1.setMovementMethod(LinkMovementMethod.getInstance());

        String linkText2 = "<a href ='https://danieloluwadare.com/eventer/'>"+faq4.getText().toString()+"</a>";
        faq4.setText(Html.fromHtml(linkText2));
        faq4.setMovementMethod(LinkMovementMethod.getInstance());

        faq2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFaqDialogs();
            }
        });
        faq3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailDialog();
            }
        });

        return faqView;
    }

    private void openFaqDialogs(){
        Fragment fragment = new FAQDialogs();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction .setCustomAnimations(R.anim.grow_from_center, R.anim.blank,
                R.anim.blank, R.anim.shrink_to_center);
        fragmentTransaction.replace(R.id.main_nav,fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    private void openEmailDialog(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("text/html");
        String to[] = {"oluwadare.daniel21@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment

        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Email to developer");
       getContext().startActivity(Intent.createChooser(emailIntent , "Emailing developer"));
    }
}
