package com.wafflestudio.spring2025.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan(basePackages = ["com.wafflestudio.spring2025.config"])
class PropertiesConfig
