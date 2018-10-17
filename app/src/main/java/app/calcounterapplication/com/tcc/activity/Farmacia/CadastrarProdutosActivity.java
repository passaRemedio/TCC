package app.calcounterapplication.com.tcc.activity.Farmacia;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.Permissoes;
import app.calcounterapplication.com.tcc.model.Produto;

public class CadastrarProdutosActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText campoMarca;
    private EditText campoProduto;
    private CurrencyEditText campoValor;
    private EditText campoDescricao;
    private ImageView imagem1;
    private Spinner campoCategorias;
    private Produto produto;

    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listafotosRecuperadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produtos);

        Permissoes.validarPermissoes(permissoes, this, 1);
        storage = ConfigFirebase.getFirebaseStorage();

        //inicializando componentes
        inicializarComponentes();

        //carregar dados spinner
        carregarDadosSpinner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageProduto:
                escolherImagem(1);
                break;
        }
    }

    //selecionar imagem a ser escolhida
    public void escolherImagem(int requestCode) {

        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);

    }

    //recuperar imagem escolhida
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            //recuperar a imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configurar a imagem no imageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            }

            listafotosRecuperadas.add(caminhoImagem);

        }
    }

    public void salvarAnuncio() {

        //salvar imagens no storege
//        for (int i = 0; i < listafotosRecuperadas.size(); i++) {
//            String urlImagem = listafotosRecuperadas.get(i);
//            int tamanhoLista = listafotosRecuperadas.size();
//            salvarFotosStorage(urlImagem, tamanhoLista, i);
//        }
        Log.i("salvar", "salvo com sucesso");

    }

    private void salvarFotosStorage(String urlString, int totalFotos, int contador) {

        //criando nós no storage
        StorageReference imagemProduto = storage.child("imagens")
                .child("produtos")
                .child(produto.getIdProduto())
                .child("imagem" + contador);

        //fazer upload do arquivo
        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));

    }

    private Produto configurarProduto() {

        String categoria = campoCategorias.getSelectedItem().toString();
        String marca = campoMarca.getText().toString();
        String prod = campoProduto.getText().toString();
        String valor = String.valueOf(campoValor.getRawValue());
        String descricao = campoDescricao.getText().toString();

        Produto produto = new Produto();
        produto.setCategoria(categoria);
        produto.setMarca(marca);
        produto.setProduto(prod);
        produto.setValor(valor);
        produto.setDescricao(descricao);

        return produto;
    }

    public void validarAnuncio(View view) {

        produto = configurarProduto();

        if (listafotosRecuperadas.size() != 0) {
            if (produto.getCategoria().equals("Categira")) {
                if (!produto.getMarca().isEmpty()) {
                    if (!produto.getProduto().isEmpty()) {
                        if (!produto.getValor().isEmpty() && !produto.getValor().equals("0")) {
                            if (!produto.getDescricao().isEmpty()) {

                                salvarAnuncio();

                            } else {
                                mensagemErro("Preencha o campo descrição");
                            }
                        } else {
                            mensagemErro("Preencha o campo valor");
                        }
                    } else {
                        mensagemErro("Preencha o campo produto");
                    }
                } else {
                    mensagemErro("Preencha o campo marca");
                }
            } else {
                mensagemErro("Preencha o campo categoria");
            }
        } else {
            mensagemErro("Selecione ao menos uma foto");
        }

    }

    public void mensagemErro(String mensagem) {
        Toast.makeText(getApplicationContext(),
                mensagem, Toast.LENGTH_SHORT).show();
    }


    //verificar se permissões foram negadas
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    //tratamento de permissões negadas
    private void alertaValidacaoPermissao() {

        //caso seja negado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para cadastrar novos produtos " +
                "é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finaliza a interface
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void carregarDadosSpinner() {

        //spinner Categorias
        String[] categorias = getResources().getStringArray(R.array.categorias);
        //adicionar valores do spinner
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCategorias.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        campoCategorias.setAdapter(adapterCategorias);

    }

    public void inicializarComponentes() {
        campoMarca = findViewById(R.id.editMarca);
        campoProduto = findViewById(R.id.editProduto);
        campoValor = findViewById(R.id.editValor);
        campoDescricao = findViewById(R.id.editDescricao);
        campoCategorias = findViewById(R.id.spinnerCategorias);
        imagem1 = findViewById(R.id.imageProduto);

        //gerenciando clique
        imagem1.setOnClickListener(this);

        //configurar localidade ptBR
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

}
