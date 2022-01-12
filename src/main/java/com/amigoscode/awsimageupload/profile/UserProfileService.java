package com.amigoscode.awsimageupload.profile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amigoscode.awsimageupload.bucket.BucketName;
import com.amigoscode.awsimageupload.filestore.FileStore;

@Service
public class UserProfileService {

	
	private final UserProfileDataAccessService userProfileDataAccessService;
	
	private final FileStore fileStore;

	@Autowired
	public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
		super();
		this.userProfileDataAccessService = userProfileDataAccessService;
		this.fileStore = fileStore;
	}
	
	List<UserProfile> getUserProfiles(){
		return userProfileDataAccessService.getUserProfiles();
		
	}
	

	public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
		// 1. check if image is not empty
		isFileEmpty(file);
	
	    // 2. if file is an image
	    isImageFile(file);
	
		// 3. The user exists in our databasae
		UserProfile user = getUserProfileOrThrow(userProfileId);
				
		// 4. Grab  some metadata from fle if any
		Map<String, String> metadata = 	extractMetaData(file);
	
		// 5. Store the image in s3 and update database with s3 image link
		String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
		String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
		try {
			fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
			user.setUserProfileImageLink(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException(e);
		}
	}
	
    byte[] downloadUserProfileImage(UUID userProfileId) {
		UserProfile user = getUserProfileOrThrow(userProfileId);
		String path = String.format("%s/%s", 
				BucketName.PROFILE_IMAGE.getBucketName(), 
				user.getUserProfileId());
		
		return user.getUserProfileImageLink().map(key -> fileStore.download(path, key)).orElse(new byte[0]);

	}
	
	private boolean isFileEmpty(MultipartFile file) {
		if(file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file ["+ file.getSize()+
					"]");
		}
		return true;
		
    }
	
	private boolean isImageFile(MultipartFile file) {
		String contentType = file.getContentType();
		
		List<String> mime = Arrays.asList(MediaType.IMAGE_JPEG.toString(), MediaType.IMAGE_PNG.toString(), MediaType.IMAGE_GIF.toString());
		
		if(!Arrays.asList(MediaType.IMAGE_JPEG.toString(), MediaType.IMAGE_PNG.toString(), MediaType.IMAGE_GIF.toString()).contains(file.getContentType())) {
			throw new IllegalStateException("File must be an image!");
		}
		
		return true;
	}
	
	private UserProfile getUserProfileOrThrow(UUID userProfileId) {
//		List<UserProfile> list = userProfileDataAccessService.
//				getUserProfiles().
//				stream().collect(Collectors.toList());
	
				
		
		return userProfileDataAccessService.
		getUserProfiles().
		stream().
		filter(userProfile->userProfile.getUserProfileId().equals(userProfileId)).
		findFirst().
		orElseThrow(()-> new IllegalStateException(String.format("UserProfile %s not found", userProfileId)));

	}
	
	private Map<String, String> extractMetaData(MultipartFile file){
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		return metadata;
		
	}
}
