package com.example.androidappproject;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class HomeFragment extends Fragment {
    JSONObject root;
    TextView wikipediaView;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle bundle){
        View rootView = layoutInflater.inflate(R.layout.homefragment,container,false);
        return rootView;
    }

    @Override
    public  void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        wikipediaView = getView().findViewById(R.id.wikipediaView);
        new GetWikiDescriptionTask().execute("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=Street%20Art");
    }

    public String parseJson (String json) {
        String description = "";
        try {
            root = new JSONObject(json);
            JSONObject query = root.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");
            JSONObject streetArtPage = pages.getJSONObject("2658000");
            description = streetArtPage.getString("extract");


        }
        catch (Exception e){
            Toast.makeText(this.getContext(), "Error with the JSON parsing", Toast.LENGTH_SHORT).show();
        }
        return description;
    }

    public String makeHttpRequest(URL url) throws IOException
    {
        String jsonResponse="";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();


            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        }
        catch (IOException e){

            Toast.makeText(this.getContext(), "Error with the HTTP request", Toast.LENGTH_SHORT).show();
        }
        finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    public String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }
    public URL createURL(String s)
    {
        URL url = null;
        try {
            url = new URL(s);
        }
        catch (Exception e){
            
            Toast.makeText(this.getContext(), "Error with the URL", Toast.LENGTH_SHORT).show();
        }
        return url;
    }

    private class GetWikiDescriptionTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            String description = "";
            try {
                URL wikiurl = createURL(strings[0]);
                String jsonResponse = makeHttpRequest(wikiurl);
                description = parseJson(jsonResponse);
            }
            catch (Exception e){}
            return description;
        }

        protected void onPostExecute(String result){
            wikipediaView.append("\n"+result);
        }
    }
}
