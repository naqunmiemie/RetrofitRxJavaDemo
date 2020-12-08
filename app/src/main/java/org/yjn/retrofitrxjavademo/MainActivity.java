package org.yjn.retrofitrxjavademo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;

    private static final String TAG = "RxJava";
    private static final String Url = "https://fanyi.youdao.com/";
    private String src;
    private String tgt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv);
        editText = findViewById(R.id.et);
        button = findViewById(R.id.bottom);

        //步骤4：创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        // 步骤5：创建 网络请求接口 的实例
        IGetRequest request = retrofit.create(IGetRequest.class);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                src = editText.getText().toString();
                // 步骤6：采用Observable<...>形式 对 网络请求 进行封装
                Observable<Translation> observable = request.getCall(src);
                // 步骤7：发送网络请求
                observable.subscribeOn(Schedulers.io())               // 在IO线程进行网络请求
                        .observeOn(AndroidSchedulers.mainThread())  // 回到主线程 处理请求结果
                        .subscribe(new Observer<Translation>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "开始采用subscribe连接");
                            }

                            @Override
                            public void onNext(Translation result) {
                                // 步骤8：对返回的数据进行处理
//                                Log.d(TAG, result.toString());
                                tgt = result.getTranslateResult().get(0).get(0).getTgt();
                                Log.d(TAG, tgt);
                                textView.setText(tgt);

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "请求失败");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "请求成功");
                            }
                        });

            }
        });


    }
}