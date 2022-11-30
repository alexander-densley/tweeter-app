package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.dao_interface.S3DAOInterface;

public class S3DAO implements S3DAOInterface {
    private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private static final String BUCKET_NAME = "alexander-tweeter-bucket";

    @Override
    public String uploadProfileImage(String alias, String image) {
        byte[] imageBytes = Base64.getDecoder().decode(image);
        InputStream fis = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");
        metadata.setCacheControl("public, max-age=31536000");
        s3Client.putObject(BUCKET_NAME, alias, fis, metadata);
        s3Client.setObjectAcl(BUCKET_NAME, alias, CannedAccessControlList.PublicRead);
        return s3Client.getUrl(BUCKET_NAME, alias).toString();
    }
}
