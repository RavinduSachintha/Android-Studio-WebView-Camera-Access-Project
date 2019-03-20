/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.permissionrequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * This fragment shows a {@link WebView}.
 */
public class PermissionRequestFragment extends Fragment
        implements ConfirmationDialogFragment.Listener, MessageDialogFragment.Listener {

    private static final String TAG = PermissionRequestFragment.class.getSimpleName();

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    /**
     * A reference to the {@link WebView}.
     */
    private WebView mWebView;

    /**
     * This field stores the {@link PermissionRequest} from the web application until it is allowed
     * or denied by user.
     */
    private PermissionRequest mPermissionRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permission_request, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.web_view);
        // Here, we use #mWebChromeClient with implementation for handling PermissionRequests.
        mWebView.setWebChromeClient(mWebChromeClient);
        configureWebSettings(mWebView.getSettings());
    }

    @Override
    public void onResume() {
        super.onResume();
        // This is for runtime permission on Marshmallow and above; It is not directly related to
        // PermissionRequest API.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else {
            mWebView.loadUrl("https://ravindusachintha.github.io/dialogflow-rest/");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        // This is for runtime permission on Marshmallow and above; It is not directly related to
        // PermissionRequest API.
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (permissions.length != 1 || grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Camera permission not granted.");
            } else if (mWebView != null) {
                mWebView.loadUrl("https://ravindusachintha.github.io/dialogflow-rest/");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            MessageDialogFragment.newInstance(R.string.permission_message)
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private static void configureWebSettings(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
    }

    /**
     * This {@link WebChromeClient} has implementation for handling {@link PermissionRequest}.
     */
    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        // This method is called when the web content is requesting permission to access some
        // resources.
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            System.out.println("onPermissionRequest");
            mPermissionRequest = request;
            final String[] requestedResources = request.getResources();
            for (String r : requestedResources) {
                if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                    // In this sample, we only accept video capture request.
                    ConfirmationDialogFragment
                            .newInstance(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE})
                            .show(getChildFragmentManager(), FRAGMENT_DIALOG);
                    break;
                }
            }
        }

        // This method is called when the permission request is canceled by the web content.
        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            System.out.println("onPermissionRequestCanceled");
            // We dismiss the prompt UI here as the request is no longer valid.
            mPermissionRequest = null;
            DialogFragment fragment = (DialogFragment) getChildFragmentManager()
                    .findFragmentByTag(FRAGMENT_DIALOG);
            if (null != fragment) {
                fragment.dismiss();
            }
        }

    };

    @Override
    public void onOkClicked() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onConfirmation(boolean allowed, String[] resources) {
        if (allowed) {
            mPermissionRequest.grant(resources);
            System.out.println("Permission granted.");

        } else {
            mPermissionRequest.deny();
            System.out.println("Permission request denied.");
        }
        mPermissionRequest = null;
    }

}
