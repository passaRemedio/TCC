package app.calcounterapplication.com.tcc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.model.Produto;

public class DetalheActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo;
    private TextView valor;
    private TextView regiao;
    private TextView descricao;
    private Produto produtoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        inicializarComponentes();

        //Recupera anuncio selecionado
        produtoSelecionado = (Produto) getIntent()
                .getSerializableExtra("produtoSelecionado");

        if (produtoSelecionado != null) {

            titulo.setText(produtoSelecionado.getProduto());
            descricao.setText(produtoSelecionado.getDescricao());
            regiao.setText(produtoSelecionado.getRegiao());
            valor.setText(produtoSelecionado.getValor());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlString = produtoSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);

                }
            };

            carouselView.setPageCount(produtoSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

        }



        //Mostrar o botão
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Ativar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Detalhe Produto");
    }


    private void inicializarComponentes() {

        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        valor = findViewById(R.id.textValorDetalhe);
        regiao = findViewById(R.id.textVRegiaoDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);

    }
}
