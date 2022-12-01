package com.ittalens.gag.services;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class ConfigPropertiesService {

        @Value("${server.port}")
        private int serverPort;

}
