package com.aluradevsafios.LiterAlura.utilities;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class EncodearBusquedas {

    public String encodearBusquedas(String busqueda){
        return URLEncoder.encode(busqueda, StandardCharsets.UTF_8);
    }
}
