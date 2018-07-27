package com.todo.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AmazonSThree {
	private static final String SUFFIX = "/";

	@SuppressWarnings("deprecation")
	public static void create() {

		AWSCredentials credentials = new BasicAWSCredentials("YourAccessKeyID", "YourSecretAccessKey");

		AmazonS3 s3client = new AmazonS3Client(credentials);

		String bucketName = "todo-bucket";
		s3client.createBucket(bucketName);
		String folderName = "images";
		createFolder(bucketName, folderName, s3client);

		String fileName = folderName + SUFFIX + "testvideo.mp4";
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, new File("C:\\Users\\user\\Desktop\\testvideo.mp4"))
						.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName + SUFFIX, emptyContent,
				metadata);

		client.putObject(putObjectRequest);
	}

}