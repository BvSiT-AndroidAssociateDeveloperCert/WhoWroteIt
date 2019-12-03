package com.example.android.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String,Void,String> {

    WeakReference<TextView> mTitleText;
    WeakReference<TextView> mAuthorText;

    FetchBook(TextView titleText,TextView authorText){
        mTitleText= new WeakReference<>(titleText);
        mAuthorText = new WeakReference<>(authorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String authors = null;

            while (i<jsonArray.length()&& (title==null && authors==null)){
                JSONObject book = jsonArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    JSONArray authors_array = volumeInfo.getJSONArray("authors");
                    StringBuilder builderAuthors = new StringBuilder();
                    for (int j=0;j< authors_array.length();j++){
                        if (j>0) builderAuthors.append(", ");
                        builderAuthors.append(authors_array.get(j).toString());
                    }
                    authors = builderAuthors.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            
            if (title!=null && authors!=null){
                mAuthorText.get().setText(authors);
                mTitleText.get().setText(title);
            }
            else {
                mAuthorText.get().setText(R.string.no_results);
                mTitleText.get().setText("");
            }
        }
        catch (Exception e){
            mAuthorText.get().setText(R.string.no_results);
            mTitleText.get().setText("");
        }
    }
}
