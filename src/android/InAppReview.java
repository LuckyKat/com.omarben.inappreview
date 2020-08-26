package com.luckykat;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.tasks.Task;

public class InAppReview extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("requestReview".equals(action)) {
            requestReview(callbackContext);
            return true;
        }

        return false;
    }

    private void requestReview(CallbackContext callbackContext) {
        // ReviewManager manager = new FakeReviewManager(cordova.getActivity());
        ReviewManager manager = ReviewManagerFactory.create(cordova.getActivity());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = manager.launchReviewFlow(cordova.getActivity(), reviewInfo);
                flow.addOnCompleteListener(t -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
                    callbackContext.sendPluginResult(pluginResult);
                });

            } else {
                // There was some problem, continue regardless of the result.
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Could not request In App Review"));
            }
        });
    }
}
