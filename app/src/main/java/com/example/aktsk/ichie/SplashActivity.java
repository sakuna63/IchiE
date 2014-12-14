package com.example.aktsk.ichie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.example.aktsk.ichie.SplashActivity.SendIdTask.Callback;
import com.example.aktsk.ichie.SplashActivity.SendIdTask.Result;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.UUID;

public class SplashActivity extends ActionBarActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isLogin()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

//        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String id = UUID.randomUUID().toString();
        new SendIdTask(new Callback() {
            @Override
            public void onResult(Result result) {
                if (result.equals(Result.SUCCESS)) {
                    PrefUtil.saveLogined(getApplicationContext());
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }).execute(id);

        progress = new ProgressDialog(this);
        progress.setMessage("認証中");
        progress.show();

        // TODO: send id and launch mainactivity when callbacked
    }

    private boolean isLogin() {
        return PrefUtil.isLogined(this);
    }

    static class SendIdTask extends AsyncTask<String, Void, Integer> {

        private final Callback callback;

        private SendIdTask(Callback callback) {
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(String... params) {
            String id = String.valueOf(params[0].hashCode());
            String url = "http://ec2-54-148-165-193.us-west-2.compute.amazonaws.com/cakephp/users/add";
            OkHttpClient client = new OkHttpClient();
            MultipartBuilder builder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM);
            addFormDataPart(builder, "id", id);
            addFormDataPart(builder, "name", "testuser");
            Request request = new Builder()
                    .post(builder.build())
                    .url(url)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int code = response == null ? 500 : response.code();
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            Result result = Result.SUCCESS;
            if (code >= 300) {
                result = Result.ERROR;
            }
            callback.onResult(result);
        }

        public interface Callback {
            void onResult(Result result);
        }

        public enum Result {
            SUCCESS, ERROR
        }

  /** Add a form data part to the body. */
  public void addFormDataPart(MultipartBuilder builder, String name, String value) {
    addFormDataPart(builder, name, null, RequestBody.create(null, value));
  }

  /** Add a form data part to the body. */
  public void addFormDataPart(MultipartBuilder builder, String name, String filename, RequestBody value) {
    if (name == null) {
      throw new NullPointerException("name == null");
    }
    StringBuilder disposition = new StringBuilder("form-data; name=");
    appendQuotedString(disposition, name);

    if (filename != null) {
      disposition.append("; filename=");
      appendQuotedString(disposition, filename);
    }

    builder.addPart(Headers.of("Content-Disposition", disposition.toString()), value);
  }
        private static StringBuilder appendQuotedString(StringBuilder target, String key) {
    target.append('"');
    for (int i = 0, len = key.length(); i < len; i++) {
      char ch = key.charAt(i);
      switch (ch) {
        case '\n':
          target.append("%0A");
          break;
        case '\r':
          target.append("%0D");
          break;
        case '"':
          target.append("%22");
          break;
        default:
          target.append(ch);
          break;
      }
    }
    target.append('"');
    return target;
  }

    }
}
