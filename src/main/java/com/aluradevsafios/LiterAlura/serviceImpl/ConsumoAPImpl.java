package com.aluradevsafios.LiterAlura.serviceImpl;

import com.aluradevsafios.LiterAlura.service.ConsumoAPI;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ConsumoAPImpl implements ConsumoAPI {
    @Override
    public String obtenerDatos(String url) {
    HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = null;
    try{
        response = client
                .send(request, HttpResponse.BodyHandlers.ofString());
    }catch(IOException | InterruptedException e){
        throw new RuntimeException(e);
    }
        return response.body();
    }
}
