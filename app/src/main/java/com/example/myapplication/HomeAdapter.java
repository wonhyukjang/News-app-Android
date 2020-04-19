package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private LayoutInflater inflater;
    private List<Object> combine;
    private static int TYPE_WEATHER = 1;
    private static int TYPE_NEWS = 2;

    HomeAdapter(Context ctx, List<Object> combine) {
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);
        this.combine = combine;
    }

    @Override
    public int getItemViewType(int position) {
        if (combine.get(position) instanceof Weather)
            return TYPE_WEATHER;
        else if (combine.get(position) instanceof News)
            return TYPE_NEWS;
        else
            return 999999;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_WEATHER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_layout, parent, false);
            viewHolder = new WeatherViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_layout, parent, false);
            viewHolder = new NewsViewHolder(view);
        }
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_WEATHER) {
            WeatherViewHolder weatherViewHolder = (WeatherViewHolder) holder;

            Weather weather = (Weather) combine.get(position);
            weatherViewHolder.city.setText(weather.getCity());
            weatherViewHolder.state.setText(weather.getState());
            weatherViewHolder.temperature.setText(weather.getTemperature() + '\u2103');
            weatherViewHolder.weather.setText(weather.getWeather());
            if (weather.getWeather().compareTo("Clouds") == 0) {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.cloudy_weather);
            } else if (weather.getWeather().compareTo("Clear") == 0) {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.clear_weather);
            } else if (weather.getWeather().compareTo("Snow") == 0) {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.snowy_weather);
            } else if (weather.getWeather().compareTo("Rain") == 0 || weather.getWeather().compareTo("Drizzle") == 0) {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.rainy_weather);
            } else if (weather.getWeather().compareTo("Thunderstorm") == 0) {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.thunder_weather);
            } else {
                weatherViewHolder.cardViewLayout.setBackgroundResource(R.drawable.sunny_weather);
            }
        } else {
            NewsViewHolder holders = (NewsViewHolder) holder;

            News news = (News) combine.get(position);
            // Change ZoneDateTime !!!!!
            ZonedDateTime timeNow = ZonedDateTime.now(ZoneId.of("America/Montreal"));
            ZonedDateTime timeGenerated = ZonedDateTime.parse(news.getTime());
            Duration d = Duration.between(timeNow, timeGenerated);
            String[] d_arr = d.toString().split("-");
            String timeReturn = "";

            for (String a : d_arr) {
                String substring = a.substring(a.length() - 1);
                if (substring.compareTo("H") == 0) {
                    timeReturn = a.substring(0, a.length() - 1) + "h ago";
                    System.out.println(a);
                    break;
                } else if (substring.compareTo("M") == 0) {
                    timeReturn = a.substring(0, a.length() - 1) + "m ago";
                    System.out.println(a);
                    break;
                } else if (substring.compareTo("S") == 0) {
                    timeReturn = (int) Float.parseFloat(a.substring(0, a.length() - 1)) + "s ago";
                    System.out.println(a);
                    break;
                }
            }

            Log.i("TIMERETURN: ", timeReturn);
            holders.newsTitle.setText(news.getTitle());
            holders.newsSource.setText(news.getSection());
            holders.newsDate.setText(timeReturn);
            holders.newsURL.setText(news.getWebURL());
            holders.newsID.setText(news.getId());
            holders.newsImgURL.setText(news.getImg());
            Picasso.get().load(news.getImg()).fit().into(holders.newsImage);
            //BookMark
            if (SharedPreference.getSavedObjectFromPreference(context, "storage", holders.newsID.getText().toString(), News.class) == null) {
                holders.newsBookmark.setTag("noBookmark");
            } else {
                holders.newsBookmark.setTag("Bookmark");
                holders.newsBookmark.setImageResource(R.drawable.ic_bookmark_24px);
            }

        }
    }

    @Override
    public int getItemCount() {
        return this.combine.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, newsSource, newsDate, newsID, newsURL, newsImgURL;

        ImageView newsImage, newsBookmark, newsTwitter, dialog_bookmark;


        NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.newsImage);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsSource = itemView.findViewById(R.id.newsSource);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsBookmark = itemView.findViewById(R.id.bookmark);
            newsTwitter = itemView.findViewById(R.id.twitter);
            newsURL = itemView.findViewById(R.id.newsURL);
            newsID = itemView.findViewById(R.id.newsID);
            dialog_bookmark = itemView.findViewById(R.id.dialog_bookmark);
            newsImgURL = itemView.findViewById(R.id.newsImgURL);

            newsBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("CONTEXT: ", view.getContext().toString());
                    if (newsBookmark.getTag().equals("noBookmark")) {
                        newsBookmark.setImageResource(R.drawable.ic_bookmark_24px);
                        newsBookmark.setTag("Bookmark");
                        Log.i("NEWS ID: ", newsID.getText().toString());
                        Toast.makeText(view.getContext(), "ID" + newsID.getText().toString(), Toast.LENGTH_LONG).show();
                        News news = new News();

                        news.setId(newsID.getText().toString());
                        news.setImg(newsImgURL.getText().toString());
                        news.setSection(newsSource.getText().toString());
                        news.setTime(newsDate.getText().toString());
                        news.setTitle(newsTitle.getText().toString());
                        news.setWebURL(newsURL.getText().toString());
                        SharedPreference.saveObjectToSharedPreference(view.getContext(), "storage", news.getId(), news);
                        Log.i("LOG ID: ", "ID" + SharedPreference.getSavedObjectFromPreference(view.getContext(), "storage", newsID.getText().toString(), News.class));

                    } else {
                        newsBookmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                        newsBookmark.setTag("noBookmark");
                        SharedPreference.removeSavedObjectFromPreference(view.getContext(), "storage", newsID.getText().toString());
//                        HomeAdapter.context = view.getContext();
                        Log.i("LOG ID: ", "ID" + SharedPreference.getSavedObjectFromPreference(view.getContext(), "storage", newsID.getText().toString(), News.class));
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ArticleActivity.class);
                    intent.putExtra("url", newsID.getText());

                    intent.putExtra("newsImage", newsImgURL.getText().toString());
                    intent.putExtra("newsSource", newsSource.getText());
                    intent.putExtra("newsDate", newsDate.getText());
                    intent.putExtra("newsTitle", newsTitle.getText());
                    intent.putExtra("newsURL", newsURL.getText());

                    v.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Dialog dialog = new Dialog(view.getContext());
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.dialog);
                    // Set dialog title
                    dialog.setTitle("Custom Dialog");

                    // set values for custom dialog components - text, image and button
                    TextView text = dialog.findViewById(R.id.textDialog);
                    text.setText(newsTitle.getText());
                    ImageView image = dialog.findViewById(R.id.imageDialog);

                    Bitmap bitmap = ((BitmapDrawable) newsImage.getDrawable()).getBitmap();

                    image.setImageBitmap(bitmap);

                    ImageView twitter = dialog.findViewById(R.id.twitter);
                    twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text=Check out this Link: &url="
                                    + newsURL.getText() + "&hashtags=CSCI571NewsSearch"));
                            view.getContext().startActivity(intent);
                        }
                    });

                    final ImageView dialog_Bookmark = dialog.findViewById(R.id.dialog_bookmark);

                    if (SharedPreference.getSavedObjectFromPreference(view.getContext(), "storage", newsID.getText().toString(), News.class) == null) {
                        dialog_Bookmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                    } else {
                        dialog_Bookmark.setImageResource(R.drawable.ic_bookmark_24px);
                    }

                    dialog_Bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (newsBookmark.getTag().equals("noBookmark")) {
                                News news = new News();

                                news.setId(newsID.getText().toString());
                                news.setImg(newsImgURL.getText().toString());
                                news.setSection(newsSource.getText().toString());
                                news.setTime(newsDate.getText().toString());
                                news.setTitle(newsTitle.getText().toString());
                                news.setWebURL(newsURL.getText().toString());

                                dialog_Bookmark.setImageResource(R.drawable.ic_bookmark_24px);
                                newsBookmark.setImageResource(R.drawable.ic_bookmark_24px);
                                SharedPreference.saveObjectToSharedPreference(view.getContext(), "storage", newsID.getText().toString(), news);
                                newsBookmark.setTag("Bookmark");

                            } else {
                                dialog_Bookmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                                newsBookmark.setImageResource(R.drawable.ic_bookmark_border_24px);
                                SharedPreference.removeSavedObjectFromPreference(view.getContext(), "storage", newsID.getText().toString());
                                newsBookmark.setTag("noBookmark");
                            }

                        }
                    });
                    dialog.show();

                    return false;
                }
            });
        }
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView city, state, weather, temperature;
        CardView cardview;
        ConstraintLayout cardViewLayout;

        WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            state = itemView.findViewById(R.id.state);
            weather = itemView.findViewById(R.id.weather);
            temperature = itemView.findViewById(R.id.temperature);
            cardview = itemView.findViewById(R.id.cardView);
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout);
        }
    }
}
