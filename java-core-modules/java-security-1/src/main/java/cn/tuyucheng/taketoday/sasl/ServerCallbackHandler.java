package cn.tuyucheng.taketoday.sasl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import java.io.IOException;

public class ServerCallbackHandler implements CallbackHandler {

	@Override
	public void handle(Callback[] cbs) throws IOException, UnsupportedCallbackException {
		for (Callback cb : cbs) {
			if (cb instanceof AuthorizeCallback) {
				AuthorizeCallback ac = (AuthorizeCallback) cb;
				ac.setAuthorized(true);
			} else if (cb instanceof NameCallback) {
				NameCallback nc = (NameCallback) cb;
				nc.setName("username");

			} else if (cb instanceof PasswordCallback) {
				PasswordCallback pc = (PasswordCallback) cb;
				pc.setPassword("password".toCharArray());
			} else if (cb instanceof RealmCallback) {
				RealmCallback rc = (RealmCallback) cb;
				rc.setText("myServer");
			}
		}
	}
}