package com.amigoscode.awsimageupload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfig {
	
	@Bean
	public AmazonS3 s3() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(
				"AKIAVMNFVE5RSAJ6VH6M",
                "nzzBif+SWbNsInDFl0QoocHad4hJhOPY+noJwj78"
	
		);
	
		return AmazonS3ClientBuilder.standard().withRegion("ap-southeast-1").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
	}

}

