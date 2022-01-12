package com.amigoscode.awsimageupload.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.amigoscode.awsimageupload.profile.UserProfile;

@Repository
public class FakeUserProfileDataStore {
	
	private static final List<UserProfile> USER_PROFILES = new ArrayList<>();
	
	static {
		USER_PROFILES.add(new UserProfile(UUID.fromString("ced6720a-18f0-4bcb-8fe5-8cd1584b24da"), "Mary_Jane", null ));
		USER_PROFILES.add(new UserProfile(UUID.fromString("e91611e9-380d-4569-81e8-c86f29284991"), "Peter_Parker", null ));
	}

	public List<UserProfile> getUserProfiles(){
		return USER_PROFILES;
	}
}
