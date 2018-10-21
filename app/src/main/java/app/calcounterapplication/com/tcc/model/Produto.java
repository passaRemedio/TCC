package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;


public class Produto {

    private String idProduto;
    private String categoria;
    private String marca;
    private String produto;
    private String valor;
    private String descricao;
    private List<String> Fotos;

    public Produto() {
        DatabaseReference produtoRef = ConfigFirebase.getFirebase()
                .child("meus_produtos");
        setIdProduto(produtoRef.push().getKey());

    }

    public void salvar() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("meus_produtos");

        anuncioRef.child(idUsuario)
                .child(getIdProduto())
                .setValue(this);

        salvarProdutoPublico();

    }

    public void salvarProdutoPublico() {

        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("produtos");

        anuncioRef.child(getCategoria())
                .child(getIdProduto())
                .setValue(this);
    }


    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return Fotos;
    }

    public void setFotos(List<String> fotos) {
        Fotos = fotos;
    }
}
