package phoenixministries.biblestudytrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.http.HttpConnection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import phoenixministries.biblestudytrainer.R;

//TODO - Add route to chooser button

public class ReadingActivity extends ActionBarActivity {

    //WebView readingWebView;
    TextView readingTextView;
    TextView copyrightTextView;
    TextView headerTextView;
    Passage passageToDisplay;
    ScrollView scrollContainer;
    WebView readingWebView;
    String currentPassage;
    int selectedVerse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        //Setup variables
        Bundle b = getIntent().getExtras();
        String passage = b.getString("passage");
        selectedVerse = b.getInt("verse");
        headerTextView = (TextView)findViewById(R.id.readingHeader);
        currentPassage = passage+":"+selectedVerse;
        headerTextView.setText(currentPassage);

        //TODO - Setup checker for chosen version
        String requestedVersion = getString(R.string.defaultVersion);

        passageToDisplay = new Passage(
                b.getString("book"), b.getInt("chapter"), b.getInt("verse"), requestedVersion);
        readingWebView = (WebView)findViewById(R.id.reading_webview);
        //readingTextView = (TextView)findViewById(R.id.readingContent);
        //copyrightTextView = (TextView)findViewById(R.id.copyrightContent);
        //scrollContainer = (ScrollView)findViewById(R.id.reading_scroll);

        //Check connectivity
        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        //Connected
        if(netInfo != null && netInfo.isConnected()){
            String passageText = passage;
            String versionShort = "eng-ESV";

            //Downloads full chapter
            passage = passage.replace(" ","+");
            String url = "https://" +
                    getString(R.string.api_url) + passage + "&version=" + versionShort;

            //passageText=downloadUrl(url);
            DownloadWebpageTask downloadWebpageTask = new DownloadWebpageTask();
            downloadWebpageTask.execute(url);


            //readingTextView.setText(passageText);

        }else{
            headerTextView.setText("Error: No network connection");
        }
    }

    private String downloadUrl(String myUrl) throws IOException{
        InputStream is = null;
        int len = 500;

        try{
            URL url = new URL(myUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //Add authentication
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    String username = getString(R.string.api_key);
                    String password = "pass";
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });

            //TODO - Add TrustManager to work after certificate expires

            /*TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            TrustManager[] trustManagers = tmf.getTrustManagers();
            final X509TrustManager origTrustManager = (X509TrustManager) trustManagers[0];

            TrustManager[] wrappedTrustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            origTrustManager.checkClientTrusted(chain,authType);
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            try{
                                origTrustManager.checkClientTrusted(chain,authType);
                            }catch (CertificateException e){}
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return origTrustManager.getAcceptedIssuers();
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, wrappedTrustManagers, null);
            connection.setDefaultSSLSocketFactory(sc.getSocketFactory());*/

            connection.connect();
            int responseCode = connection.getResponseCode();
            is=connection.getInputStream();

            //TODO - Create XML parser for InputStream

            //Updated buffer to read whole response
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                response.append(line);
            }

            return response.toString();
        }finally {
            if(is != null){
                is.close();
            }
        }


    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            showLoadingPage();
            try{
                return downloadUrl(urls[0]);
            }catch (IOException e) {
                return "Unable to return webpage";
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.contains("<")) {
                //Parse XML and apply to Passage
                String passageText = getContentsOfTag(result, "text");
                passageToDisplay.setText(passageText);

                String passageCopyright = getContentsOfTag(result, "copyright");
                passageToDisplay.setCopyright(passageCopyright);
            }

            //TODO - Highlight selected passage
            String tempToReplace = passageToDisplay.getText();
            int indexOfSup = tempToReplace.indexOf(">"+selectedVerse+"</sup");
            int indexOfSupTag = tempToReplace.substring(0,indexOfSup).lastIndexOf("<");
            //TODO - Replace tags to include a span with background-color
            //tempToReplace.re
            //passageToDisplay.setText();

            //Apply parsed text to View
            String pageHtml = passageToDisplay.getText()+"<br>"+passageToDisplay.getCopyright();
            pageHtml+="<style>body{font-size:1.8em; background: transparent;}</style>";
            readingWebView.loadData(pageHtml,"text/html","UTF-8");
            readingWebView.setBackgroundColor(Color.TRANSPARENT);
            headerTextView.setText(currentPassage);
            //readingTextView.setText(Html.fromHtml(passageToDisplay.getText()));
            //copyrightTextView.setText(passageToDisplay.getCopyright());

            //scrollContainer.scrollTo(0, 100);
            /*scrollContainer.post(new Runnable() {
                @Override
                public void run() {
                    int[] startIndex = new int[readingTextView.getLineCount()];
                    for (int i=0; i<readingTextView.getLineCount(); i++){
                        startIndex[i]=readingTextView.getLayout().getLineStart(i);
                    }
                    int lineToScrollTo = -1;
                    for(int i=0; i<startIndex.length; i++){
                        int start = startIndex[i];
                        int end;
                        if(i<startIndex.length-1){
                            end = startIndex[i+1];
                        }else{
                            end = start + 1;
                        }

                        String line = readingTextView.getText().subSequence(start, end).toString();

                        if(line.contains(""+selectedVerse)){
                            lineToScrollTo = i;
                            break;
                        }
                    }
                    int heightToScroll = readingTextView.getLineHeight()*lineToScrollTo;
                    scrollContainer.scrollTo(0, heightToScroll);
                }
            });*/
        }

        private void showLoadingPage(){
            headerTextView.setText("Loading...");
        }

        private String getContentsOfTag(String xml, String tag){
            switch (tag){
                case "text":
                    int preTextIndex = xml.indexOf("<text>");
                    int startTextIndex = xml.indexOf("CDATA[",preTextIndex) + 6;
                    int endTextIndex = xml.indexOf("]]",startTextIndex);
                    return xml.substring(startTextIndex,endTextIndex);
                case "copyright":
                    int copyStartIndex = xml.indexOf("<copyright>") + 11;
                    int copyEndIndex = xml.indexOf("</copyright>");
                    return  xml.substring(copyStartIndex,copyEndIndex);
            }
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.reading_to_chooser:
                goToChooser();
                return true;
            case R.id.action_settings:
                //settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToChooser(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                "phoenixministries.biblestudytrainer.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Intent intent = new Intent(this,ChooserActivity.class);
        editor.putInt("selected_verse_id",-1);
        editor.commit();
        startActivity(intent);
        finish();
    }
}
