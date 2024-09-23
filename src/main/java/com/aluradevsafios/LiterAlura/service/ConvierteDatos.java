package com.aluradevsafios.LiterAlura.service;

public interface ConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);
}
