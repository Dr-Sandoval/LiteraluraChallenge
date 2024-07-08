package com.aluraone.literalura.service;

public interface IConvierteDatos {
    <T> T obtenerDatos (String json, Class<T> clase);
}
