package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore {
 private final AmazonS3 amazonS3;
 private final String bucketname;
 private final Tika tika = new Tika();



    public S3Store(AmazonS3 amazonS3, String bucketname) {
        this.amazonS3 = amazonS3;
        this.bucketname = bucketname;
    }

     @Override
    public void put(Blob blob) throws IOException {
        amazonS3.putObject(bucketname,blob.name,blob.inputStream, new ObjectMetadata());

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
       // S3Object s3Object = new S3Object();

        if (!amazonS3.doesObjectExist(bucketname,name)) {
            return Optional.empty();
        } else {

        try(S3Object s3Object1=amazonS3.getObject(bucketname,name)){

            S3ObjectInputStream s3ObjectInputStream= s3Object1.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
            Blob myblob = new Blob(name,new ByteArrayInputStream(bytes),tika.detect(bytes));
            //coverFile = Paths.get(getSystemResource("default-cover.jpg").toURI());
            return Optional.of(myblob);



        }

        }
    }

    @Override
    public void deleteAll() {

        List<S3ObjectSummary> summaries = amazonS3.listObjects(bucketname).getObjectSummaries();
        for(S3ObjectSummary summary:summaries){

            amazonS3.deleteObject(bucketname,summary.getKey());

        }

    }
}

