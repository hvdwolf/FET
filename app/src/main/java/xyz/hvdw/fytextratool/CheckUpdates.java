package xyz.hvdw.fytextratool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckUpdates {

    static StringBuilder content;
    static String result;
    public static String readFETVersionString(String urlStr) {
        content = new StringBuilder();
        boolean validconnection = true;
        HttpsURLConnection urlConnection = null;
        // Disable certificate validation
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            Logger.logToFile(e.toString());
            content.append(e.toString());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        /*String build_gradle = "";
        try {
            build_gradle = ShellRootCommands.shellExec("curl https://raw.githubusercontent.com/hvdwolf/FET/main/app/build.gradle");
        } catch (Exception e) {
            Logger.logToFile(e.toString());
            throw new RuntimeException(e);
        }*/
        /*try {
            URL url = new URL(urlStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            content.append( in.readLine());
            in.close();
        } catch (Exception ex) {
            Logger.logToFile("upgrade check gives error " + ex.toString());
            ex.printStackTrace();
            validconnection = false;
            //JOptionPane.showMessageDialog(null, String.format(ProgramTexts.HTML, 250, ResourceBundle.getBundle("translations/program_strings").getString("msd.nonetwlong")), ResourceBundle.getBundle("translations/program_strings").getString("msd.nonetwork"), JOptionPane.INFORMATION_MESSAGE);
        } */


        /*OkHttpClient client = new OkHttpClient();
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            //System.out.println(response.body().string());
            //content.append(response.body().string());
            result = response.body().string();
        } catch (IOException e) {
            Logger.logToFile("upgrade check gives error " + e.toString());
            //content.append(e.toString());
            result = e.toString();
            e.printStackTrace();
        } */
        return content.toString();
        //return build_gradle;
    }
}
