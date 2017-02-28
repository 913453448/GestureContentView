package com.leo.gesturepwddemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.library.bean.PointState;
import com.leo.library.view.GestureContentView;
import com.leo.library.view.IGesturePwdCallBack;
import com.leo.library.view.IndicatorView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IGesturePwdCallBack {
    private GestureContentView mGestureView;
    private IndicatorView indicatorView;
    private TextView tvIndicator;

    private int count=0;
    private String pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestureView= (GestureContentView) findViewById(R.id.id_gesture_pwd);
        indicatorView= (IndicatorView) findViewById(R.id.id_indicator_view);
        tvIndicator= (TextView) findViewById(R.id.id_indicator);
        mGestureView.setGesturePwdCallBack(this);
    }

    @Override
    public void callBack(List<Integer> pwds) {
        StringBuffer sbPwd=new StringBuffer();
        for (Integer pwd:pwds) {
            sbPwd.append(pwd);
        }
        tvIndicator.setText(sbPwd.toString());
        if(pwds!=null&&pwds.size()>0){
            indicatorView.setPwds(pwds);
        }
      if(count++==0){
            pwd=sbPwd.toString();
            Toast.makeText(this,"请再次绘制手势密码",Toast.LENGTH_SHORT).show();
            mGestureView.changePwdState(PointState.POINT_STATE_NORMAL,0);
        } else{
            count=0;
            if(pwd.equals(sbPwd.toString())){
                Toast.makeText(this,"密码设置成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"两次密码不一致，请重新绘制",Toast.LENGTH_SHORT).show();
                indicatorView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_shake));
                count=0;
                mGestureView.changePwdState(PointState.POINT_STATE_ERRO,0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGestureView.changePwdState(PointState.POINT_STATE_NORMAL,0);
                    }
                },1000);
            }
        }
    }
}
