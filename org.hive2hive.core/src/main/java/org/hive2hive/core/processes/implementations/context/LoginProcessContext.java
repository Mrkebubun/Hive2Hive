package org.hive2hive.core.processes.implementations.context;

import java.security.KeyPair;

import org.hive2hive.core.model.Locations;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.processes.implementations.context.interfaces.IConsumeLocations;
import org.hive2hive.core.processes.implementations.context.interfaces.IConsumeProtectionKeys;
import org.hive2hive.core.processes.implementations.context.interfaces.IConsumeUserProfile;
import org.hive2hive.core.processes.implementations.context.interfaces.IProvideLocations;
import org.hive2hive.core.processes.implementations.context.interfaces.IProvideUserProfile;

public class LoginProcessContext implements IProvideUserProfile, IConsumeUserProfile, IProvideLocations,
		IConsumeLocations, IConsumeProtectionKeys {

	private UserProfile profile;
	private Locations locations;
	private boolean isInitial;

	@Override
	public void provideUserProfile(UserProfile profile) {
		this.profile = profile;
	}

	@Override
	public void provideLocations(Locations locations) {
		this.locations = locations;
	}

	@Override
	public Locations consumeLocations() {
		return locations;
	}

	public void setIsInitial(boolean isInitial) {
		this.isInitial = isInitial;
	}

	public boolean getIsInitial() {
		return isInitial;
	}

	@Override
	public KeyPair consumeProtectionKeys() {
		return profile.getProtectionKeys();
	}

	@Override
	public UserProfile consumeUserProfile() {
		return profile;
	}

}
