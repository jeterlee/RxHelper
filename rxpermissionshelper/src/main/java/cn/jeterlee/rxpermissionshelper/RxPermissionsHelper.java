package cn.jeterlee.rxpermissionshelper;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.jeterlee.rxhelper.RxHelper;
import io.reactivex.functions.Consumer;

/**
 * Rx权限申请类（1. 权限在Activity中申请；2. 使用RxHelper前请务必先注入；）
 *
 * @author jeterlee
 * @date 2017/11/30 0030
 * @email xqlee120@yeah.net
 */
public class RxPermissionsHelper {
    private final static String TAG = "RxPermissionsHelper";
    private final RxHelper mRxHelper;
    private final RxPermissions mRxPermissions;
    private Activity mActivity;

    public RxPermissionsHelper(Activity activity) {
        this.mActivity = activity;
        mRxHelper = new RxHelper(activity);
        mRxPermissions = new RxPermissions(activity);
    }

    /**
     * 请求申请摄像头,和写入外部存储的权限
     *
     * @param callback 请求权限成功后的接口回掉
     */
    public void launchCamera(IRequestPermissionCallback callback) {
        onRequestPermission(callback, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    /**
     * 请求写入外部存储的权限
     *
     * @param callback 请求权限成功后的接口回掉
     */
    public void externalStorage(IRequestPermissionCallback callback) {
        onRequestPermission(callback, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 请求改变WiFi状态的权限
     *
     * @param callback 请求权限成功后的接口回掉
     */
    public void changeWiFiState(IRequestPermissionCallback callback) {
        onRequestPermission(callback, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE);
    }

    /**
     * 请求发送短信权限
     *
     * @param callback 请求权限成功后的接口回掉
     */
    public void sendSms(IRequestPermissionCallback callback) {
        onRequestPermission(Manifest.permission.SEND_SMS, callback);
    }

    /**
     * 请求获取手机状态的权限
     *
     * @param callback 请求权限成功后的接口回掉
     */
    public void readPhoneState(IRequestPermissionCallback callback) {
        onRequestPermission(Manifest.permission.READ_PHONE_STATE, callback);
    }


    /**
     * 请求获取指定的权限的权限(申请单个权限)
     *
     * @param permission 要申请的权限
     * @param callback   请求权限成功后的接口回调
     */
    private void onRequestPermission(final String permission, IRequestPermissionCallback callback) {
        onRequestPermission(callback, permission);
    }

    /**
     * 请求获取指定的权限的权限(同时申请多个权限)
     *
     * @param callback    请求权限成功后的接口回调
     * @param permissions 要申请的权限
     */
    private void onRequestPermission(final IRequestPermissionCallback callback, final String... permissions) {
        try {
            boolean granted = false;
            // 先确保是否已经申请过此权限，同时申请多个权限
            for (String permission : permissions) {
                granted = mRxPermissions.isGranted(permission);
                if (!granted) {
                    Log.w(TAG, "this -- " + permission + " -- is not granted！");
                    break;
                }
            }

            // 已经申请过,直接执行操作
            if (granted) {
                callback.onRequestPermissionSuccess();
            } else {
                // 没有申请过,则申请
                mRxHelper.subscribe(mRxPermissions.request(permissions), new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Log.d(TAG, "Request permissions is success!");
                            callback.onRequestPermissionSuccess();
                        } else {
                            // Output permission denied
                            Toast.makeText(mActivity.getApplicationContext(), R.string.permission_refuse, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                        Toast.makeText(mActivity.getApplicationContext(), R.string.permission_err, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface IRequestPermissionCallback {
        /**
         * 请求权限成功后的回调
         */
        void onRequestPermissionSuccess();
    }
}
