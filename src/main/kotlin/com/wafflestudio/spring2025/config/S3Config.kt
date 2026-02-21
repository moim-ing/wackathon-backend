package com.wafflestudio.spring2025.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config(
    private val awsProperties: AwsProperties,
) {
    @Bean
    fun s3Client(): S3Client =
        S3Client
            .builder()
            .region(Region.of(awsProperties.region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
}
