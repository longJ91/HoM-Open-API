package com.example.songchiyun.myapplication;

import android.util.Log;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by chiyo on 2016-08-04.
 */

public class HTTPClient {
    private Document mDoc;
    private String request;
    final static String LINK ="http://52.78.155.30/insert.php";
            //"http://52.78.19.78/insert.php";
    public HTTPClient() {
      request = "";
    }
    public void setDoc(Document doc){
        mDoc = doc;
    }
    public String connect(String id, String pw) {
        String result ="";
        try {
            URL url = new URL("http://52.78.155.30/login.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();


            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
           // conn.setRequestProperty("Content-Type","application/xml; charset=UTF-8"); //xml 형태 데이터 요청 세팅
            conn.setRequestProperty("TEST", "test");
            StringBuffer buffer = new StringBuffer();
            buffer.append("ID").append("=").append(id).append("&").append("PW").append("=").append(pw);
            Log.d("디버그",buffer.toString());
// php 변수에 값 대입
            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            Log.d("디버그",buffer.toString());
            writer.flush();
            writer.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            StringBuilder sb = new StringBuilder();
            String line = "";
            Log.d("http check","check2");
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                result += line;
            }
           Log.d("result",result);

            reader.close();
            conn.disconnect();
            //reader.close();

        }catch (FileNotFoundException e){
            Log.d("err",e.toString());
        }
        catch (MalformedURLException e) {
            Log.d("err",e.toString());
            return "connection error";
        } catch (IOException e) {
            e.printStackTrace();
            return "connection error";
        }
        return result;
    }
    public void sendSensor(String pid){
        String result ="";
        try {
            URL url = new URL(LINK);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            String send =DocumentToString(mDoc);
            Log.d("http send123",send);

            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // conn.setRequestProperty("Content-Type","application/xml; charset=UTF-8"); //xml 형태 데이터 요청 세팅
            conn.setRequestProperty("TEST", "test");
            StringBuffer buffer = new StringBuffer();
            buffer.append("send").append("=").append(send).append("&");                 // php 변수에 값 대입
            buffer.append("userid").append("=").append(pid);
            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            Log.d("디버그",buffer.toString());
            writer.flush();
            writer.close();

/*
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            bufferedWriter.write(send);
            Log.d("http send123",conn.getOutputStream().toString());
            //"test="+"sss"+"&test2="+"aaa"
            bufferedWriter.flush();
            bufferedWriter.close();*/


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d("http get",line);
                break;
            }
            conn.disconnect();

            reader.close();

        }catch (FileNotFoundException e){
            Log.d("err",e.toString());
        }
        catch (MalformedURLException e) {
            Log.d("err",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendStep(String pid, String step){
        String result ="";
        try {
            URL url = new URL("http://52.78.155.30/save_exercise.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            String send =DocumentToString(mDoc);
            Log.d("http send123",send);

            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
           // conn.setRequestProperty("Content-Type","application/xml; charset=UTF-8"); //xml 형태 데이터 요청 세팅
            conn.setRequestProperty("TEST", "test");
            StringBuffer buffer = new StringBuffer();
            buffer.append("pid").append("=").append(pid).append("&").append("step").append("=").append(step);
            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            Log.d("디버그",buffer.toString());
            writer.flush();
            writer.close();

/*
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            bufferedWriter.write(send);
            Log.d("http send123",conn.getOutputStream().toString());
            //"test="+"sss"+"&test2="+"aaa"
            bufferedWriter.flush();
            bufferedWriter.close();*/


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d("http get",line);
                break;
            }
            conn.disconnect();

            reader.close();

        }catch (FileNotFoundException e){
            Log.d("err",e.toString());
        }
        catch (MalformedURLException e) {
            Log.d("err",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendPPG(String pid, String ppg){
        String result ="";
        try {
            URL url = new URL("http://52.78.155.30/insert_ppg.php");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            String send =DocumentToString(mDoc);
            Log.d("http send123",send);

            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // conn.setRequestProperty("Content-Type","application/xml; charset=UTF-8"); //xml 형태 데이터 요청 세팅
            conn.setRequestProperty("TEST", "test");
            StringBuffer buffer = new StringBuffer();
            buffer.append("pid").append("=").append(pid).append("&").append("ppg").append("=").append(ppg);
            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            Log.d("디버그",buffer.toString());
            writer.flush();
            writer.close();

/*
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8"));
            bufferedWriter.write(send);
            Log.d("http send123",conn.getOutputStream().toString());
            //"test="+"sss"+"&test2="+"aaa"
            bufferedWriter.flush();
            bufferedWriter.close();*/


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.d("http get",line);
                break;
            }
            conn.disconnect();

            reader.close();
        }catch (FileNotFoundException e){
            Log.d("err",e.toString());
        }
        catch (MalformedURLException e) {
            Log.d("err",e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String DocumentToString(Document doc) {
        try {
            StringWriter clsOutput = new StringWriter();
            Transformer clsTrans = TransformerFactory.newInstance().newTransformer();
            clsTrans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            clsTrans.setOutputProperty(OutputKeys.METHOD, "xml");
            clsTrans.setOutputProperty(OutputKeys.INDENT, "yes");
            clsTrans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            clsTrans.transform(new DOMSource(doc), new StreamResult(clsOutput));
            return clsOutput.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
