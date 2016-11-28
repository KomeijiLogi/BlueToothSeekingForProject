package tools;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 2015/9/22.
 */
public class AsyncTaskHttpUtilTools {

    public static String url="http://192.168.23.1:8080/";

    public static void sendMessage_doGet(String url,final TextView tv){

        new AsyncTask<String, Void, String>() {
            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected String doInBackground(String... params) {
                String urln=params[0];
                return doGet(urln);
            }

            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param s The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(!s.equals("")){
                    tv.setText(s);
                }
            }
        }.execute(url);

    }

    public static void sendMessage_doPost(String url,final TextView tv,final String str){
        new AsyncTask<String, Void, String>() {
            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected String doInBackground(String... params) {
                String urln=params[0];
                return doPost(urln, str);
            }

            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param s The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(!s.equals("")){
                    tv.setText(s);
                }
            }
        }.execute(url);
    }

    public  static  void sendPic_doGet(String url,final ImageView img){
        new AsyncTask<String, Void, Bitmap>() {
            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Bitmap doInBackground(String... params) {
                String str=params[0];
                return doGetPic(str);
            }

            /**
             * Runs on the UI thread before {@link #doInBackground}.
             *
             * @see #onPostExecute
             * @see #doInBackground
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param bitmap The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null){
                    img.setImageBitmap(bitmap);
                }
            }
        }.execute(url);
    }


    public static  String doGet(String geturl){
        HttpURLConnection urlconn=null;
        InputStream is=null;
        StringBuffer sbff=new StringBuffer();
        try {
            URL url=new URL(geturl);
            urlconn= (HttpURLConnection) url.openConnection();
            urlconn.setConnectTimeout(5 * 1000);
            urlconn.setReadTimeout(5 * 1000);
            if(urlconn.getResponseCode()==200){
                is=urlconn.getInputStream();
                int next=0;
                byte[] bt=new byte[1024*10];
                while((next=is.read(bt))>=0){
                    sbff.append(new String(bt,0,next));
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                if(urlconn!=null){
                    urlconn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sbff.toString();
    }


    public static String doPost(String url,String post_params){
        HttpURLConnection conn=null;
        InputStream is=null;
        StringBuffer sbff=new StringBuffer();
        try {
            URL urln=new URL(url);
            conn= (HttpURLConnection) urln.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

            //String params="userPwd=ffffff&userName=admin";
            //String params="userName="+et_user.getText()+"&&userPwd="+et_pwd.getText();
            OutputStream os=conn.getOutputStream();
            os.write(post_params.getBytes());
            os.flush();
            os.close();

            if(conn.getResponseCode()==200){
                is=conn.getInputStream();
                int next=0;
                byte[] bt=new byte[1024*10];
                while ((next=is.read(bt))>0){
                    sbff.append(new String(bt,0,next));
                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                if(conn!=null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  sbff.toString();

    }


    public static  Bitmap doGetPic(String geturl){
        HttpURLConnection urlconn=null;
        InputStream  is=null;
        StringBuffer sbff=new StringBuffer();
        Bitmap bitmap=null;
        try {
            URL url=new URL(geturl);
            urlconn= (HttpURLConnection) url.openConnection();
            urlconn.setConnectTimeout(5 * 1000);
            urlconn.setReadTimeout(5 * 1000);
            if(urlconn.getResponseCode()==200){
                is=urlconn.getInputStream();
                bitmap= BitmapFactory.decodeStream(is);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                if(urlconn!=null){
                    urlconn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public static List<String> doGetXml(String url){
        HttpURLConnection urlconn=null;
        InputStream is=null;
        List<String> result=null;
        try {
            URL urln=new URL(url);
            urlconn= (HttpURLConnection) urln.openConnection();
            urlconn.setConnectTimeout(5 * 1000);
            urlconn.setReadTimeout(5 * 1000);
            if(urlconn.getResponseCode()==200){
                is=urlconn.getInputStream();
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                DocumentBuilder db=dbf.newDocumentBuilder();
                Document dt=db.parse(is);
                NodeList nd=dt.getElementsByTagName("resources");
                Element element= (Element) nd.item(0);
                NodeList ndStr=element.getElementsByTagName("string");
                result=new ArrayList<String>();
                for(int i=0;i<ndStr.getLength();i++){
                    String temp=ndStr.item(i).getFirstChild().getNodeValue();
                    result.add(temp);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                if(urlconn!=null){
                    urlconn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }




}
