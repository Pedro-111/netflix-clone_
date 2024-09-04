package com.example.netflix_clone.Fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflix_clone.Adapter.ContentAdapter;
import com.example.netflix_clone.DetailActivity;
import com.example.netflix_clone.Model.Content;
import com.example.netflix_clone.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ContentAdapter.OnItemClickListener {

    private RecyclerView acclaimedSeriesRecyclerView;
    private RecyclerView dramaticSeriesRecyclerView;
    private RecyclerView yourNextStoryRecyclerView;
    private ContentAdapter acclaimedSeriesAdapter;
    private ContentAdapter dramaticSeriesAdapter;
    private ContentAdapter yourNextStoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        acclaimedSeriesRecyclerView = view.findViewById(R.id.acclaimed_series_recycler_view);
        dramaticSeriesRecyclerView = view.findViewById(R.id.dramatic_series_recycler_view);
        yourNextStoryRecyclerView = view.findViewById(R.id.your_next_story_recycler_view);
        setupRecyclerViews();

        return view;
    }

    private void setupRecyclerViews() {
        List<Content> acclaimedSeries = getAcclaimedSeries();
        List<Content> dramaticSeries = getDramaticSeries();
        List<Content> yourNextStory = getYourNextStory();

        acclaimedSeriesAdapter = new ContentAdapter(acclaimedSeries, getContext(), this);
        dramaticSeriesAdapter = new ContentAdapter(dramaticSeries, getContext(), this);
        yourNextStoryAdapter = new ContentAdapter(yourNextStory,getContext(),this);
        acclaimedSeriesRecyclerView.setAdapter(acclaimedSeriesAdapter);
        dramaticSeriesRecyclerView.setAdapter(dramaticSeriesAdapter);
        yourNextStoryRecyclerView.setAdapter(yourNextStoryAdapter);
    }

    private List<Content> getAcclaimedSeries() {
        // Populate this with your actual data
        List<Content> series = new ArrayList<>();
        series.add(new Content("Peaky Blinders","https://wallpaperaccess.com/full/1087735.jpg","La historia de una familia de gánsteres en Birmingham, Inglaterra, en la década de 1920."));
        series.add(new Content("Better Call Saul", "https://wallpaperaccess.com/full/1091801.jpg","La serie sigue a Jimmy McGill, un abogado con un pasado complicado que eventualmente se convierte en Saul Goodman."));
        series.add(new Content("Breaking Bad", "https://wallpaperaccess.com/full/5840994.jpg","Un profesor de química convertido en fabricante de metanfetaminas lucha por proveer a su familia mientras se adentra en el mundo del crimen."));
        series.add(new Content("House", "https://wallpaperaccess.com/full/1827383.jpg","El Dr. Gregory House, un médico brillante pero egocéntrico, enfrenta complejos casos médicos y personales en su hospital."));
        series.add(new Content("The Crown","https://wallpaperaccess.com/full/1902880.jpg","Serie dramática que narra el reinado de la Reina Isabel II y los eventos históricos que moldearon la segunda mitad del siglo XX."));
        series.add(new Content("Stranger Things","https://wallpaperaccess.com/full/642644.jpg","Un grupo de niños en una pequeña ciudad enfrenta fenómenos paranormales mientras buscan a su amigo desaparecido."));
        return series;
    }

    private List<Content> getDramaticSeries() {
        // Populate this with your actual data
        List<Content> series = new ArrayList<>();
        series.add(new Content("Elite", "https://wallpaperaccess.com/full/2281944.jpg", "Un grupo de estudiantes en un colegio privado de élite enfrenta el asesinato de uno de sus compañeros mientras descubren secretos y relaciones."));
        series.add(new Content("Dexter", "https://wallpaperaccess.com/full/1088998.jpg","Dexter Morgan, un analista forense de la policía con una doble vida como vigilante de criminales, lucha con sus propios demonios internos."));
        series.add(new Content("Sex Education", "https://wallpaperaccess.com/full/1557429.png","Un adolescente con una madre terapeuta sexual ayuda a sus compañeros a resolver sus problemas de sexualidad y relaciones en la escuela secundaria."));
        series.add(new Content("The Handmaid's Tale", "https://wallpapercave.com/wp/wp2186846.jpg", "En un futuro distópico, las mujeres son subyugadas en una sociedad teocrática, y una criada lucha por encontrar a su hija y recuperar su libertad."));
        series.add(new Content("This Is Us", "https://wallpaperaccess.com/full/1778152.jpg", "La serie explora la vida de una familia a lo largo de varias décadas, abordando temas como el amor, la pérdida y el crecimiento personal."));
        return series;
    }

    private List<Content> getYourNextStory(){
        List<Content> contenido = new ArrayList<>();
        contenido.add(new Content("Inexpertos", "https://pics.filmaffinity.com/Inexpertos-565930605-large.jpg", "Un grupo de jóvenes sin experiencia en el mundo de los negocios se embarca en una serie de aventuras mientras intentan hacerse un nombre en el competitivo mundo corporativo."));
        contenido.add(new Content("Suits", "https://wallpaperaccess.com/full/1265940.jpg", "La serie sigue a un brillante joven sin título legal que se hace pasar por un abogado en una prestigiosa firma de abogados de Nueva York, enfrentando desafíos éticos y personales en el camino."));
        contenido.add(new Content("You", "https://wallpaperaccess.com/full/2114490.jpg", "Un librero se obsesiona con una mujer y utiliza las redes sociales y otras herramientas para acercarse a ella, mientras su comportamiento se vuelve cada vez más inquietante y peligroso."));
        return contenido;
    }

    @Override
    public void onItemClick(Content content) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("content", content);
        startActivity(intent);
    }

}