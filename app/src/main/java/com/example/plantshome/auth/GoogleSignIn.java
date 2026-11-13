package com.example.plantshome.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.example.plantshome.R;
import com.example.plantshome.util.AuthErrors;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * "Continue with Google": picks a Google account with Credential Manager,
 * then signs in to Firebase with its ID token.
 *
 * <p>Needs the Google provider turned on in Firebase and the app's SHA-1 added there;
 * the google-services plugin then generates {@code default_web_client_id}.
 */
public final class GoogleSignIn {

    private static final String TAG = "GoogleSignIn";

    public interface Callback {
        void onSignedIn();

        /** @param message what to show the user, or null if they simply cancelled */
        void onFailed(@Nullable String message);
    }

    private GoogleSignIn() {
    }

    /** False when this build's Firebase config has no Google sign-in client. */
    public static boolean isConfigured(@NonNull Context context) {
        return webClientId(context) != null;
    }

    public static void signIn(@NonNull Activity activity, @NonNull Callback callback) {
        String clientId = webClientId(activity);
        if (clientId == null) {
            callback.onFailed(activity.getString(R.string.message_google_not_configured));
            return;
        }
        GetGoogleIdOption googleOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .build();
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build();

        CredentialManager.create(activity).getCredentialAsync(
                activity,
                request,
                new CancellationSignal(),
                ContextCompat.getMainExecutor(activity),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse response) {
                        signInToFirebase(activity, response.getCredential(), callback);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException error) {
                        if (error instanceof GetCredentialCancellationException) {
                            callback.onFailed(null);
                            return;
                        }
                        Log.w(TAG, "Credential Manager failed", error);
                        callback.onFailed(activity.getString(error instanceof NoCredentialException
                                ? R.string.message_no_google_account
                                : R.string.message_generic_error));
                    }
                });
    }

    private static void signInToFirebase(Activity activity, Credential credential, Callback callback) {
        if (!(credential instanceof CustomCredential)
                || !GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
            callback.onFailed(activity.getString(R.string.message_generic_error));
            return;
        }
        String idToken;
        try {
            idToken = GoogleIdTokenCredential.createFrom(credential.getData()).getIdToken();
        } catch (Exception e) {
            Log.w(TAG, "Invalid Google ID token", e);
            callback.onFailed(activity.getString(R.string.message_generic_error));
            return;
        }
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                .addOnSuccessListener(activity, result -> callback.onSignedIn())
                .addOnFailureListener(activity, error ->
                        callback.onFailed(AuthErrors.message(activity, error)));
    }

    // The resource only exists when google-services.json has a web OAuth client,
    // so it is looked up by name instead of R.string (which would not compile without it).
    @SuppressLint("DiscouragedApi")
    @Nullable
    private static String webClientId(Context context) {
        int id = context.getResources().getIdentifier(
                "default_web_client_id", "string", context.getPackageName());
        return id == 0 ? null : context.getString(id);
    }
}
