package joaosa.notificareandroidtest;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RestAuthAsyncTask extends AsyncTask<String, Object, String> {

    private static final String USERNAME = "27b8d0a3b51afab71838e02766f2ff26649369988a99e926c5a32a6ff06acd2d";
    private static final String PASSWORD = "a84f34f19f8f9816f8583dbd6ed948d698be45e82204fd9d11544b4b2ca168d5";

    private Context context;
    private AbstractHttpClient httpClient;

    public RestAuthAsyncTask(Context context) {
        this.context = context;
        this.httpClient = new DefaultHttpClient();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
        this.httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            HttpResponse response = httpClient.execute(new HttpGet(params[0]));
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            String responseString = responseHandler.handleResponse(response);
            return responseString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent("restResult");
        intent.putExtra("result", result);
        context.sendBroadcast(intent);
    }
}