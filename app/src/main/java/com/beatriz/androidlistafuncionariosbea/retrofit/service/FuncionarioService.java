package com.beatriz.androidlistafuncionariosbea.retrofit.service;

import com.beatriz.androidlistafuncionariosbea.model.Funcionario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface FuncionarioService {

    public static final String BASE_URL = "http://192.168.0.55:8080/";

    @GET("funcionarios/")
    Observable<List<Funcionario>> getFuncionarios();

    @POST ("funcionarios/")
    Observable<Funcionario> adiciona(@Body Funcionario funcionario);

    @PUT ("funcionarios/{id}")
    Observable<Funcionario> atualizaFuncionario(@Path("id") int id, @Body Funcionario funcionario);

    @DELETE ("funcionarios/{id}")
    Observable<Funcionario> deletaFuncionario(@Path("id") int id);
}
