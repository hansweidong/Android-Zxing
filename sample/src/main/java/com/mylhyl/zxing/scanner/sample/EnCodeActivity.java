package com.mylhyl.zxing.scanner.sample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.zxing.Result;
import com.mylhyl.zxing.scanner.ScannerView;
import com.mylhyl.zxing.scanner.decode.QRDecode;
import com.mylhyl.zxing.scanner.sample.picture.PickPictureTotalActivity;

/**
 * 扫描
 */
public class EnCodeActivity extends DeCodeActivity {

    public static final String EXTRA_LASER_LINE_MODE = "laser_line_mode";
    public static final int EXTRA_LASER_LINE_MODE_0 = 0;
    public static final int EXTRA_LASER_LINE_MODE_1 = 1;
    public static final int EXTRA_LASER_LINE_MODE_2 = 2;

    private ScannerView mScannerView;
    private Result mLastResult;
    private int laserMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_encode);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mScannerView = (ScannerView) findViewById(R.id.scanner_view);
        mScannerView.setOnScannerCompletionListener(this);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mScannerView.toggleLight(isChecked);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickPictureTotalActivity.gotoActivity(EnCodeActivity.this);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            laserMode = extras.getInt(EXTRA_LASER_LINE_MODE);
        }
        mScannerView.setMediaResId(R.raw.beep);//设置扫描成功的声音

//        mScannerView.setLaserFrameTopMargin(100);//扫描框与屏幕上方距离
//        mScannerView.setLaserFrameSize(200, 200);//扫描框大小
//        mScannerView.setLaserFrameCornerLength(25);//设置4角长度
//        mScannerView.setLaserLineHeight(5);//设置扫描线高度

        switch (laserMode) {
            case EXTRA_LASER_LINE_MODE_0:
                mScannerView.setLaserLineResId(R.mipmap.wx_scan_line);//线图
                break;
            case EXTRA_LASER_LINE_MODE_1:
                mScannerView.setLaserGridLineResId(R.mipmap.zfb_grid_scan_line);//网格图
                mScannerView.setLaserFrameBoundColor(0xFF26CEFF);//支付宝颜色
                break;
            case EXTRA_LASER_LINE_MODE_2:
                mScannerView.setLaserColor(Color.RED);
                break;
        }
    }

    @Override
    protected void onResume() {
        mScannerView.onResume();
        resetStatusView();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mLastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void restartPreviewAfterDelay(long delayMS) {
        mScannerView.restartPreviewAfterDelay(delayMS);
        resetStatusView();
    }

    private void resetStatusView() {
        mLastResult = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED && resultCode == Activity.RESULT_OK) {
            if (requestCode == PickPictureTotalActivity.REQUEST_CODE_SELECT_PICTURE) {
                String picturePath = data.getStringExtra(PickPictureTotalActivity.EXTRA_PICTURE_PATH);
                QRDecode.decodeQR(picturePath, this);
            }
        }
    }

    public static void gotoActivity(Activity activity, boolean isBackResult, int laserMode) {
        activity.startActivityForResult(new Intent(activity, EnCodeActivity.class)
                        .putExtra(EnCodeActivity.EXTRA_RETURN_SCANNER_RESULT, isBackResult)
                        .putExtra(EXTRA_LASER_LINE_MODE, laserMode)
                , EnCodeActivity.REQUEST_CODE_SCANNER);
    }
}
