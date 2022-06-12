package com.fprs6.serviciosg3;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class pdfViwerActivity extends AppCompatActivity {

   private WebView pdfViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viwer);

        pdfViewer = findViewById(R.id.webViewPdf);

        String urlPdf = getIntent().getStringExtra("urlPdf");

        System.out.println(urlPdf);

        pdfViewer.setWebViewClient(new WebViewClient());
        pdfViewer.getSettings().setSupportZoom(true);
        pdfViewer.getSettings().setJavaScriptEnabled(true);


        String googleDocUrl = "https://docs.google.com/viewer?url=";

        try {
            pdfViewer.loadUrl(googleDocUrl + URLEncoder.encode(urlPdf, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}