package cn.tuyucheng.taketoday.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.Console;
import java.io.IOException;

public class ConsoleCallbackHandler implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		Console console = System.console();
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback) {
				NameCallback nameCallback = (NameCallback) callback;
				nameCallback.setName(console.readLine(nameCallback.getPrompt()));
			} else if (callback instanceof PasswordCallback) {
				PasswordCallback passwordCallback = (PasswordCallback) callback;
				passwordCallback.setPassword(console.readPassword(passwordCallback.getPrompt()));
			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
	}
}
