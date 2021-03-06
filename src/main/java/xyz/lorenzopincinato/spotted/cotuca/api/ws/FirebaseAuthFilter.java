package xyz.lorenzopincinato.spotted.cotuca.api.ws;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.yawp.commons.http.HttpException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class FirebaseAuthFilter extends HttpFilter {

    private final static Logger LOGGER = Logger.getLogger(FirebaseAuthFilter.class.getName());

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        String authToken = request.getHeader("Authorization");
        LOGGER.info(String.format("Request received [url: %s][auth: %s]", request.getRequestURI(), authToken));

        if (Objects.nonNull(authToken)) {
            String idToken = authToken.split(" ")[1];
            FirebaseToken decodedToken = getFirebase().verifyIdTokenAsync(idToken).get();
            /*if (!decodedToken.isEmailVerified()) {
                throw new HttpException(403, "You can only login with a verified e-mail!");
            }*/
            String email = decodedToken.getEmail();
            LOGGER.info(String.format("Authenticated: [%s]", email));
            AuthHolder.email.set(email);
        } else {
            AuthHolder.email.set(null);
        }
    }

    private static FirebaseAuth _auth;

    private static FirebaseAuth getFirebase() {
        if (_auth == null) {
            FirebaseOptions options = new FirebaseOptions.Builder().setProjectId("new-spotted-cotuca").setCredentials(GoogleCredentials.newBuilder().build()).build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            _auth = FirebaseAuth.getInstance(app);
        }
        return _auth;
    }
}