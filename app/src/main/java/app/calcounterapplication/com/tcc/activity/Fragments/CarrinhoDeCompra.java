package app.calcounterapplication.com.tcc.activity.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.calcounterapplication.com.tcc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarrinhoDeCompra extends Fragment {


    public CarrinhoDeCompra() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carrinho_de_compra, container, false);
    }

}
