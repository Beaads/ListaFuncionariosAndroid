package com.beatriz.androidlistafuncionariosbea.ui.activity;

import static com.beatriz.androidlistafuncionariosbea.ui.activity.FuncionarioActivityConstantes.CHAVE_FUNCIONARIO;
import static com.beatriz.androidlistafuncionariosbea.ui.activity.FuncionarioActivityConstantes.CODIGO_REQUISICAO_INSERE_FUNCIONARIO;
import static com.beatriz.androidlistafuncionariosbea.ui.activity.FuncionarioActivityConstantes.CODIGO_RESULTADO_FUNCIONARIO_CRIADO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.beatriz.androidlistafuncionariosbea.R;
import com.beatriz.androidlistafuncionariosbea.dao.FuncionarioDAO;
import com.beatriz.androidlistafuncionariosbea.model.Funcionario;
import com.beatriz.androidlistafuncionariosbea.retrofit.service.FuncionarioService;
import com.beatriz.androidlistafuncionariosbea.ui.recyclerview.adapter.ListaFuncionariosAdapter;
import com.beatriz.androidlistafuncionariosbea.ui.recyclerview.adapter.OnItemClickListener;
import com.beatriz.androidlistafuncionariosbea.ui.recyclerview.helper.callback.FuncionarioItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaFuncionariosActivity extends AppCompatActivity {

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)).build();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(FuncionarioService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ListaFuncionariosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_funcionarios);

        List<Funcionario> todosFuncionarios = pegaTodosFuncionarios();
        configuraRecyclerView(todosFuncionarios);
        configuraNovoFuncionario();
    }

    private static Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FuncionarioService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static void getFunc() {
        FuncionarioService funcionarioService = getRetrofit().create(FuncionarioService.class);
        Call<List<Funcionario>> func = funcionarioService.getFuncionarios();
        func.enqueue(new Callback<List<Funcionario>>() {
            @Override
            public void onResponse(Call<List<Funcionario>> call, Response<List<Funcionario>> response) {
            if (response.isSuccessful()) {
                Log.i("sucess", response.body().toString());
            }
            }

            @Override
            public void onFailure(Call<List<Funcionario>> call, Throwable t) {
                Log.i("erro", t.getLocalizedMessage());
            }
        });
        func.request();
    }


    private List<Funcionario> pegaTodosFuncionarios() {
        FuncionarioDAO dao = new FuncionarioDAO();
        return dao.todos();
    }

    private void configuraNovoFuncionario() {
        FloatingActionButton botaoNovoAluno = findViewById(R.id.lista_funcionarios_insere_funcionario);
        botaoNovoAluno.setOnClickListener(view -> vaiParaFormularioFuncionarioActivity());
    }


    private void vaiParaFormularioFuncionarioActivity() {
        Intent iniciaFormularioNota =
                new Intent(ListaFuncionariosActivity.this,
                        FormularioFuncionarioActivity.class);
        startActivityIfNeeded(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_FUNCIONARIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ehResultadoComFuncionario(requestCode, resultCode, data)){
            Funcionario funcionarioRecebido = (Funcionario) data.getSerializableExtra(CHAVE_FUNCIONARIO);
            adiciona(funcionarioRecebido);
        }

        if(requestCode == 2 && resultCode == CODIGO_RESULTADO_FUNCIONARIO_CRIADO && temFuncionario(data) && data.hasExtra("posicao")) {
            Funcionario funcionarioRecebido = (Funcionario) data.getSerializableExtra(CHAVE_FUNCIONARIO);
            int posicaoRecebida = data.getIntExtra("posicao", -1);
            new FuncionarioDAO().altera(posicaoRecebida, funcionarioRecebido);
            adapter.altera(posicaoRecebida, funcionarioRecebido);
        }

    }

    private void adiciona(Funcionario funcionario) {
        new FuncionarioDAO().insere(funcionario);
        adapter.adiciona(funcionario);
    }

    private boolean ehResultadoComFuncionario(int requestCode, int resultCode, Intent data) {
        return ehCodigoRequisicaoInsereFuncionario(requestCode) &&
                ehCodigoResultadoFuncionarioCriado(resultCode) &&
                temFuncionario(data);
    }

    private boolean temFuncionario(Intent data) {
        return data.hasExtra(CHAVE_FUNCIONARIO);
    }

    private boolean ehCodigoResultadoFuncionarioCriado(int resultCode) {
        return resultCode == CODIGO_RESULTADO_FUNCIONARIO_CRIADO;
    }

    private boolean ehCodigoRequisicaoInsereFuncionario(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_INSERE_FUNCIONARIO;
    }

    private void configuraRecyclerView(List<Funcionario> todosFuncionarios) {
        RecyclerView listaFuncionarios = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todosFuncionarios, listaFuncionarios);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new FuncionarioItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listaFuncionarios);

    }

    private void configuraAdapter(List<Funcionario> todosFuncionarios, RecyclerView listaFuncionarios) {
        adapter = new ListaFuncionariosAdapter(this, todosFuncionarios);
        listaFuncionarios.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Funcionario funcionario, int posicao) {
                Intent abreFormularioComFuncionario = new Intent(ListaFuncionariosActivity.this, FormularioFuncionarioActivity.class);
                abreFormularioComFuncionario.putExtra(CHAVE_FUNCIONARIO, funcionario);
                abreFormularioComFuncionario.putExtra("posicao", posicao);
                startActivityIfNeeded(abreFormularioComFuncionario, 2);
            }
            });
    }
}

