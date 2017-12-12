package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Miran on 11/12/2017.
 */

public class ProgressActivity extends Activity
{
    public ProgressDialog mProgressDialog;

    public void showProgressDialog()
    {
        if(mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog()
    {
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        hideProgressDialog();
    }
}
