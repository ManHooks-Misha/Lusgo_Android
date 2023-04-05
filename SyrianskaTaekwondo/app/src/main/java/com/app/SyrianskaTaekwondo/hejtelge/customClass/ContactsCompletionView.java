package com.app.SyrianskaTaekwondo.hejtelge.customClass;/*
package com.mishainfotech.lusgo.activity.customClass;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.mishainfotech.lusgo.R;

*/
/**
 * Sample token completion view for basic contact info
 *
 * Created on 9/12/13.
 * @author mgod
 *//*

public class ContactsCompletionView extends TokenCompleteTextView<Person_invite> {

    InputConnection testAccessibleInputConnection;
    Person_invite personInviteToIgnore;

    public ContactsCompletionView(Context context) {
        super(context);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(Person_invite personInvite) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(personInvite.getEmail());
        return token;
    }

    @Override
    protected Person_invite defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new Person_invite(completionText, completionText.replace(" ", "") + "@yopmail.com");

        } else {
            return new Person_invite(completionText.substring(0, index), completionText);
        }
    }

    //Methods for testing
    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }

    void setPersonInviteToIgnore(Person_invite personInvite) {
        this.personInviteToIgnore = personInvite;
    }

    @Override
    public boolean shouldIgnoreToken(Person_invite token) {
        return personInviteToIgnore != null && personInviteToIgnore.getEmail().equals(token.getEmail());
    }

    public void simulateSelectingPersonFromList(Person_invite personInvite) {
        convertSelectionToString(personInvite);
        replaceText(currentCompletionText());
    }
}
*/
