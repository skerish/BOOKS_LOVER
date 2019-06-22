package com.example.androidcore_asynctaskloader;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {

    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    public FetchBook(TextView TitleText, TextView AuthorText) {
        mTitleText = new WeakReference<>(TitleText);
        mAuthorText = new WeakReference<>(AuthorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            // Makes JSON object of whole result.
            JSONArray itemsArray = jsonObject.getJSONArray("items");      // "items" from source code reference.
            //Creates a new JSONArray with values from the JSON string.
            //Since max results = "10", array would contain only 10 items.
            int i = 0;
            String title = null;
            String authors = null;
            //Iterate through the itemsArray array, checking each book for title and author information.
            // With each loop, test to see if both an author and a title are found, and if so, exit the loop.
            // This way, only entries with both a title and author will be displayed.

            //The loop ends at the first match in the response. More responses might be available,
            // but this app only displays the first one.
            while (i < itemsArray.length() && (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                // book is the json object from json array of type "items".

                // "volumeInfo" again from source code reference.
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Move to the next item.
                i++;

                // If both are found, display the result.
                if (title != null && authors != null) {
                    mTitleText.get().setText(title);
                    mAuthorText.get().setText(authors);
                } else {
                    // If none are found, update the UI to
                    // show failed results.
                    mTitleText.get().setText(R.string.no_results);
                    mAuthorText.get().setText("");
                }
            }
        } catch (JSONException e) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
    }
}
