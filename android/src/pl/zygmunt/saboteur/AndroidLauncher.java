package pl.zygmunt.saboteur;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import pl.zygmunt.saboteur.Saboteur;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		config.disableAudio = true;
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGyroscope = false;
		
		initialize(new Saboteur(), config);
	}
}
